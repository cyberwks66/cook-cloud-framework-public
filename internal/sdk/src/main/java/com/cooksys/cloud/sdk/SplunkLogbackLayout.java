package com.cooksys.cloud.sdk;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

/**
 * Custom Logback layout for log files using Splunk.  Layout is defined in logback.xml
 *
 * @author Tim Davidson
 */
public class SplunkLogbackLayout extends LayoutBase<ILoggingEvent> {
    public String doLayout(ILoggingEvent event) {

        StringBuilder sb = new StringBuilder();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timestamp = new Date(event.getTimeStamp());
        sb.append(dateFormatter.format(timestamp));
        sb.append(" ");

        sb.append(event.getLevel().levelStr);
        sb.append(" ");

        sb.append(event.getLoggerName());
        sb.append(" ");

        for (Entry<String, String> mdcEntry : event.getMDCPropertyMap().entrySet()) {
            if (mdcEntry.getValue() != null && !mdcEntry.getValue().isEmpty()) {
                sb.append(mdcEntry.getKey());
                sb.append("=");
                sb.append(quote(mdcEntry.getValue()));
                sb.append(" ");
            }
        }

        sb.append("message=");
        sb.append(quote(event.getFormattedMessage()));
        sb.append("\n");
        return sb.toString();
    }

    private static String quote(String s) {
        return "\"" + s.replace("\"", "'") + "\"";
    }
}
