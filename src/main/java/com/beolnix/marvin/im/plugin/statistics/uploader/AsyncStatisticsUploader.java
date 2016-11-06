package com.beolnix.marvin.im.plugin.statistics.uploader;

import com.beolnix.marvin.im.api.model.IMIncomingMessage;
import com.beolnix.marvin.statistics.api.StatisticsApi;
import com.beolnix.marvin.statistics.api.model.ChatDTO;
import com.beolnix.marvin.statistics.api.model.StatisticsDTO;
import com.beolnix.marvin.statistics.api.model.UserDTO;
import com.beolnix.marvin.statistics.api.model.UserSpecificMetricsDTO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AsyncStatisticsUploader {

    private final StatisticsApi statisticsApi;
    private final Logger logger;
    private final ReentrantLock updateLock = new ReentrantLock();
    private volatile AtomicReference<List<MetricEvent>> listRef = new AtomicReference<>(new LinkedList<>());

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Autowired
    public AsyncStatisticsUploader(StatisticsApi statisticsApi, Logger logger) {
        this.statisticsApi = statisticsApi;
        this.logger = logger;

        executor.scheduleAtFixedRate(new UploadTask(), 0, 15, TimeUnit.SECONDS);
    }

    public void asyncUpload(ChatDTO chatDTO, IMIncomingMessage msg) {
        logger.info("Adding event:" + chatDTO.toString() + " and msg: " + msg.toString());
        updateLock.lock();
        try {
            listRef.get().add(new MetricEvent(chatDTO, msg));
        } finally {
            updateLock.unlock();
        }
    }

    private class UploadTask implements Runnable {
        @Override
        public void run() {
            updateLock.lock();
            List<MetricEvent> accumulatedEvents = null;
            try {
                accumulatedEvents = listRef.getAndSet(new LinkedList<>());
            } finally {
                updateLock.unlock();
            }

            if (accumulatedEvents == null || accumulatedEvents.size() < 1) {
                logger.info("No accumulated events, skip upload.");
            }

            push(accumulatedEvents);
        }

        private void push(List<MetricEvent> accumulatedEvents) {
            long startTotal = System.currentTimeMillis();
            Map<String, StatisticsDTO> statisticsDTOMap = convert(accumulatedEvents);
            for (StatisticsDTO statisticsDTO : statisticsDTOMap.values()) {
                long startSingle = System.currentTimeMillis();
                statisticsApi.postStatistics(statisticsDTO);
                long singleDuration = System.currentTimeMillis() - startSingle;
                logger.info("Statistics for the chat with id: " + statisticsDTO.getChat().getId() +
                        " successfully pushed in " + singleDuration + "ms.");
            }
            long totalDuration = System.currentTimeMillis() - startTotal;
            logger.info("Statistics for " + statisticsDTOMap.size() + " pushed in " + totalDuration + "ms.");
        }
    }

    private Map<String, StatisticsDTO> convert(List<MetricEvent> metricEvents) {
        Map<MetricKey, Integer> cache = new HashMap<>();
        Map<String, ChatDTO> chatsMap = new HashMap<>();

        for (MetricEvent event : metricEvents) {
            MetricKey key = new MetricKey(event.getMsg().getAuthor(), event.getChatDTO().getId(), "msgCount");
            Integer oldValue = cache.get(key);
            if (oldValue == null) {
                oldValue = 0;
            }
            cache.put(key, oldValue + 1);
            chatsMap.put(event.getChatDTO().getId(), event.getChatDTO());
        }

        Map<String, StatisticsDTO> result = new HashMap<>();

        for (Map.Entry<MetricKey, Integer> entry : cache.entrySet()) {
            String chatId = entry.getKey().getChatId();
            String username = entry.getKey().getUsername();
            String metricName = entry.getKey().getMetricName();

            StatisticsDTO statisticsDTO = result.get(chatId);
            if (statisticsDTO == null) {
                statisticsDTO = new StatisticsDTO();
                statisticsDTO.setChat(chatsMap.get(chatId));
                result.put(chatId, statisticsDTO);
            }

            UserSpecificMetricsDTO userMetric = new UserSpecificMetricsDTO();
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userMetric.setUser(userDTO);
            userMetric.getMetricsMap().put(metricName, entry.getValue());

            statisticsDTO.getUserSpecificMetrics().add(userMetric);
        }

        return result;
    }
}
