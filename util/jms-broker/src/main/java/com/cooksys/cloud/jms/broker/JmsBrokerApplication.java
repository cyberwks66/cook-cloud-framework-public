package com.cooksys.cloud.jms.broker;

import org.apache.activemq.broker.BrokerService;

/**
 * Starts an embedded ActiveMQ broker
 *
 * @author timd
 */
public class JmsBrokerApplication {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: jms-broker <port> <healthcheckPort");
            System.exit(1);
        }

        Integer port = new Integer(args[0]);
        Integer healthCheckPort = new Integer(args[1]);

        // Start the embedded ActiveMQ broker
        BrokerService broker = new BrokerService();

        try {
            broker.addConnector("tcp://0.0.0.0:" + port);

            broker.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
