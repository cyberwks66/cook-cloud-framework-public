package com.cooksys.cloud.bustrace;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by timd on 4/13/17.
 */
@ConfigurationProperties(prefix = "busTrace")
public class BusTraceProperties {
    private RabbitMqProps rabbitmq;

    public RabbitMqProps getRabbitmq() {
        return rabbitmq;
    }

    public BusTraceProperties setRabbitmq(RabbitMqProps rabbitmq) {
        this.rabbitmq = rabbitmq;
        return this;
    }

    public static class RabbitMqProps {
        private String host="localhost";
        private int port=5672;
        private String username="guest";
        private String password="guest";

        public String getHost() {
            return host;
        }

        public RabbitMqProps setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public RabbitMqProps setPort(int port) {
            this.port = port;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public RabbitMqProps setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public RabbitMqProps setPassword(String password) {
            this.password = password;
            return this;
        }
    }

}
