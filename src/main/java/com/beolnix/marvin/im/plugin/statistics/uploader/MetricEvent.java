package com.beolnix.marvin.im.plugin.statistics.uploader;

import com.beolnix.marvin.im.api.model.IMIncomingMessage;
import com.beolnix.marvin.statistics.api.model.ChatDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class MetricEvent {

    private final ChatDTO chatDTO;
    private final IMIncomingMessage msg;
    private final LocalDateTime dateTime;

    public MetricEvent(ChatDTO chatDTO, IMIncomingMessage msg) {
        this.chatDTO = chatDTO;
        this.msg = msg;

        if (msg.getTimestamp() != null) {
            Calendar timestamp = msg.getTimestamp();
            Instant instantDateTime = timestamp.getTime().toInstant();
            String timeZoneId = timestamp.getTimeZone().getID();
            this.dateTime = LocalDateTime.ofInstant(instantDateTime, ZoneId.of(timeZoneId));
        } else {
            this.dateTime = LocalDateTime.now();
        }
    }

    public ChatDTO getChatDTO() {
        return chatDTO;
    }

    public IMIncomingMessage getMsg() {
        return msg;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
