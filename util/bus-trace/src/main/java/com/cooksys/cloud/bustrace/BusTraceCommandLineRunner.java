package com.cooksys.cloud.bustrace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by timd on 4/13/17.
 */
public class BusTraceCommandLineRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(BusTraceCommandLineRunner.class);

    private BusTraceProperties config;

    private Connection conn;
    private Channel channel;

    public BusTraceCommandLineRunner(BusTraceProperties config) {
        this.config = config;
    }

    @Override
    public void run(String... args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(config.getRabbitmq().getUsername());
        factory.setPassword(config.getRabbitmq().getPassword());
        factory.setVirtualHost("/");
        factory.setHost(config.getRabbitmq().getHost());
        factory.setPort(config.getRabbitmq().getPort());
        conn = factory.newConnection();

        channel = conn.createChannel();

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, "springCloudBus", "#");

        boolean autoAck = false;
        channel.basicConsume(queueName, autoAck, "busTraceTag",
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        String routingKey = envelope.getRoutingKey();
                        String contentType = properties.getContentType();
                        long deliveryTag = envelope.getDeliveryTag();

                        String bodyString = new String(body);
                        ObjectMapper mapper = new ObjectMapper();
                        Map json = (Map) mapper.readValue(bodyString, Object.class);

                        if ("AckRemoteApplicationEvent".equalsIgnoreCase((String) json.get("type"))) {
                            return;
                        }

                        // apply filters
                        if(args.length > 0) {
                            boolean foundMatch=false;
                            for (String arg : args) {
                                if(arg.equalsIgnoreCase((String)json.get("type"))) {
                                    foundMatch=true;
                                    break;
                                }
                            }
                            if(!foundMatch) {
                                return;
                            }
                        }

                        String indentedBodyString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                        System.out.println(indentedBodyString);

                        channel.basicAck(deliveryTag, false);
                    }
                });
    }

    @PreDestroy
    public void cleanUp() {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
