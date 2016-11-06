package com.beolnix.marvin.im.plugin.statistics;

import com.beolnix.marvin.im.api.model.IMIncomingMessage;
import com.beolnix.marvin.im.plugin.statistics.uploader.AsyncStatisticsUploader;
import com.beolnix.marvin.plugins.api.PluginConfig;
import com.beolnix.marvin.statistics.api.ChatApi;
import com.beolnix.marvin.statistics.api.model.ChatDTO;
import com.beolnix.marvin.statistics.api.model.CreateChatDTO;
import feign.FeignException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by beolnix on 07/02/16.
 */
@Service
public class StatisticsService {

    // dependencies
    private final ChatApi chatApi;
    private final PluginConfig pluginConfig;
    private final Logger logger;
    private final AsyncStatisticsUploader uploader;

    @Autowired
    public StatisticsService(PluginConfig pluginConfig, Logger logger, ChatApi chatApi, AsyncStatisticsUploader uploader) {
        this.chatApi = chatApi;
        this.pluginConfig = pluginConfig;
        this.logger = logger;
        this.uploader = uploader;
    }

    public void newMessage(IMIncomingMessage msg) {
        logger.info("Message to persist: " + msg);
        if (!msg.isConference()) {
            logger.info("ignore nonConference message: " + msg.getAuthor() +
                    " - " + msg.getRawMessageBody());
        }

        ChatDTO chatDTO = getOrCreateChatByName(msg.getConferenceName(), msg.getProtocol());
        if (chatDTO == null) {
            logger.error("Can't get chat for name " + msg.getConferenceName());
            return;
        }

        uploader.asyncUpload(chatDTO, msg);

    }


    private ChatDTO getOrCreateChatByName(String chatName, String protocol) {
        chatName = simplifyChatName(chatName);
        ChatDTO chatDTO = null;
        try {
            chatDTO = chatApi.getChatByName(chatName);
        } catch (FeignException e) {
            // nop
        }
        if (chatDTO == null) {
            logger.debug("Got null chat. Creating new one with name " + chatName);
            CreateChatDTO createChatDTO = new CreateChatDTO();
            createChatDTO.setConference(true);
            createChatDTO.setName(chatName);
            createChatDTO.setProtocol(protocol);
            chatDTO = chatApi.createChat(createChatDTO);
            logger.debug("Created chat: " + chatDTO);
        }

        return chatDTO;
    }

    private String simplifyChatName(String originalChatName) {
        return originalChatName.replace(".", "-");
    }
}
