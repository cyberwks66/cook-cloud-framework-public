package com.cooksys.cloud.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Config properties for elasticsearch logging appender
 *
 * @author Tim Davidson
 */
@Component
@ConfigurationProperties(prefix = "logger")
public class LoggerProperties {

    private ElasticsearchAppenderProperties elasticsearch;

    public static class ElasticsearchAppenderProperties {
        private boolean enabled=false;
        private String host="localhost";
        private int port = 9300;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public ElasticsearchAppenderProperties getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(ElasticsearchAppenderProperties elasticsearch) {
        this.elasticsearch = elasticsearch;
    }
}
