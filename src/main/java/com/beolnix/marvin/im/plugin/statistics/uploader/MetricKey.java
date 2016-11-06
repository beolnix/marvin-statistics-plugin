package com.beolnix.marvin.im.plugin.statistics.uploader;

public class MetricKey {
    private final String username;
    private final String chatId;
    private final String metricName;

    public MetricKey(String username, String chatId, String metricName) {
        this.username = username;
        this.chatId = chatId;
        this.metricName = metricName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricKey metricKey = (MetricKey) o;

        if (username != null ? !username.equals(metricKey.username) : metricKey.username != null) return false;
        if (chatId != null ? !chatId.equals(metricKey.chatId) : metricKey.chatId != null) return false;
        return metricName != null ? metricName.equals(metricKey.metricName) : metricKey.metricName == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        result = 31 * result + (metricName != null ? metricName.hashCode() : 0);
        return result;
    }

    public String getUsername() {
        return username;
    }

    public String getChatId() {
        return chatId;
    }

    public String getMetricName() {
        return metricName;
    }
}
