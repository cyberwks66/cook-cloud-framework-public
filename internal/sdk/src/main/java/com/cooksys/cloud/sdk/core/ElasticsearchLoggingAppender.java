package com.cooksys.cloud.sdk.core;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.cooksys.cloud.sdk.LoggerProperties;
import com.cooksys.cloud.sdk.SpringContext;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.joda.time.LocalDateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchLoggingAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {

        final LoggerProperties loggerProperties = (LoggerProperties) SpringContext.getBean("loggerProperties");
        if (loggerProperties == null) return;

        LoggerProperties.ElasticsearchAppenderProperties elasticsearchProps = loggerProperties.getElasticsearch();
        if (elasticsearchProps == null) {
            return;
        }

        if (!elasticsearchProps.isEnabled()) {
            return;
        }

        Client elasticsearchClient = null;

        String host = elasticsearchProps.getHost();
        int port = elasticsearchProps.getPort();

        if (host == null || port == 0) {
            return;
        }

        try {
            elasticsearchClient = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(
                            InetAddress.getByName(host),
                            port));
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
        Map<String,String> mdcMap = event.getMDCPropertyMap();
        for(Map.Entry<String,String> entry : mdcMap.entrySet()) {
            if(isInteger(entry.getValue())) {
                json.put(entry.getKey(),new Integer(entry.getValue()));
            }
            else if(isNumeric(entry.getValue())) {
                json.put(entry.getKey(), new Double(entry.getValue()));
            } else {
                json.put(entry.getKey(),entry.getValue());
            }
        }

        // build the time-based index (new index every hour)
        LocalDateTime dateTime = new LocalDateTime();


        String timeBasedIndex = "cook-" + dateTime.getMonthOfYear() + "." +
                dateTime.getDayOfMonth() + "." +
                dateTime.getYear() + "." +
                dateTime.getHourOfDay();

        IndexResponse response = elasticsearchClient.prepareIndex(timeBasedIndex, "log")
                .setSource(json)
                .get();

        elasticsearchClient.close();
    }
    public static boolean isInteger(String str)
    {
        try
        {
            double d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}
