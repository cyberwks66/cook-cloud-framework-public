package com.cooksys.logmanager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by timd on 3/30/17.
 */
@Component
public class TurbineStreamListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(TurbineStreamListener.class);

    @Value("${turbineUrl}")
    private String turbineURLStr;

    private void pushTurbineToElastic() throws Exception {

        URL turbineURL = new URL(turbineURLStr);
        HttpURLConnection turbineConnection = (HttpURLConnection) turbineURL.openConnection();
        log.debug("***" + turbineURL.getHost() + ":" + turbineURL.getPort() + "***");

        turbineConnection.setRequestMethod("GET");
        turbineConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = turbineConnection.getResponseCode();
        log.debug("/Sending 'GET' request to URL : " + turbineURL);
        log.debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(turbineConnection.getInputStream())
        );
        String inputLine;
        //StringBuffer response = new StringBuffer();

        StringBuilder sb = null;
        while ((inputLine = in.readLine()) != null) {
            if (": ping".equals(inputLine) || "".equals(inputLine)) {
                continue;
            }
            String turbineData = inputLine.substring(6);

            Map<String, Object> parsedJson = null;

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            parsedJson = gson.fromJson(turbineData, type);

            if (parsedJson.get("latencyExecute") != null) {
                final Map<String,Double> latencyExecute = (Map<String,Double>)parsedJson.get("latencyExecute");

                MDC.clear();
                MDC.put("elasticsearch", "");
                MDC.put("name", (String)parsedJson.get("name"));
                MDC.put("eventName", "TurbineStream");
                MDC.put("latencyTotal_mean", String.valueOf(parsedJson.get("latencyTotal_mean")));
                MDC.put("latencyExecute_mean", String.valueOf(parsedJson.get("latencyExecute_mean")));
                MDC.put("isCircuitBreakerOpen", String.valueOf(parsedJson.get("isCircuitBreakerOpen")));
                MDC.put("errorCount", String.valueOf(parsedJson.get("errorCount")));
                MDC.put("requestCount",String.valueOf(parsedJson.get("requestCount")));
                MDC.put("percent_0", String.valueOf(latencyExecute.get("0")));
                MDC.put("percent_25", String.valueOf(latencyExecute.get("25")));
                MDC.put("percent_50", String.valueOf(latencyExecute.get("50")));
                MDC.put("percent_75", String.valueOf(latencyExecute.get("75")));
                MDC.put("percent_90", String.valueOf(latencyExecute.get("90")));
                MDC.put("percent_95", String.valueOf(latencyExecute.get("95")));
                MDC.put("percent_99", String.valueOf(latencyExecute.get("99")));
                MDC.put("percent_99-5", String.valueOf(latencyExecute.get("0")));
                MDC.put("percent_100", String.valueOf(latencyExecute.get("100")));

                log.info("elastic_event");

                MDC.clear();
            }
        }
        System.out.println("CLOSING STREAM!!!!!");
        in.close();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            pushTurbineToElastic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
