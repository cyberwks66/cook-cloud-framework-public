package com.cooksys.logmanager;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.cooksys.cloud.commons.util.SpringContext;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.LocalDateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




public class ElasticsearchLoggingAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        final Map<String, String> mdcMap = event.getMDCPropertyMap();

        if(!mdcMap.containsKey("elasticsearch")) {
            return;
        }

        final String host = SpringContext.getApplicationContext().getEnvironment().getProperty("elasticsearchLoggingAppender.host");
        final Integer port = Integer.valueOf(SpringContext.getApplicationContext().getEnvironment().getProperty("elasticsearchLoggingAppender.port"));
        Client elasticsearchClient = null;

        if (host == null || port == 0) {
            return;
        }

        try {
            elasticsearchClient = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            System.out.println("Unable to configure transport client: " + e.getMessage());
            return;
        }

        Map<String, Object> json = new HashMap<>();
        json.put("timestamp", new Date(event.getTimeStamp()));
        json.put("classpath", event.getLoggerName());
        json.put("loggerLevel", event.getLevel());
        json.put("message", event.getMessage());

        // Get all the key-values from mdc, and include them in the object
        for (Map.Entry<String, String> entry : mdcMap.entrySet()) {
            if (isInteger(entry.getValue())) {
                json.put(entry.getKey(), new Integer(entry.getValue()));
            } else if (isNumeric(entry.getValue())) {
                json.put(entry.getKey(), new Double(entry.getValue()));
            } else {
                json.put(entry.getKey(), entry.getValue());
            }
        }

        // build the time-based index (new index every hour)
        LocalDateTime dateTime = new LocalDateTime();


        String timeBasedIndex = SpringContext.getApplicationContext().getEnvironment()
                .getProperty("elasticsearchLoggingAppender.indexPrefix")
                + "-" + dateTime.getMonthOfYear() + "." +
                dateTime.getDayOfMonth() + "." +
                dateTime.getYear() + "." +
                dateTime.getHourOfDay();


        IndexResponse response = null;
        try {
            response = elasticsearchClient.prepareIndex(timeBasedIndex, "log")
                    .setSource(json)
                    .get();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println(response.toString() + "***LogEntry");
        elasticsearchClient.close();
    }

    public static boolean isInteger(String str) {
        try {
            double d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
