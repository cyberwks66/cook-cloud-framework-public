package com.cooksys.cloud.discovery.widget.ui;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Backend MVC controller for discovery bar graph widget
 *
 * @author Tim Davidson
 */
@Controller
public class ServiceCountsWidgetController {

    @RequestMapping("/widget/services")
    public String serviceCounts() {
        return "serviceCounts";
    }

    @RequestMapping("/widget/serviceCounts/data")
    @ResponseBody
    public List<ChartData> getChartData() {
        List<ChartData> data = new ArrayList<>();

        for(Application application : getServerContext().getRegistry().getApplications().getRegisteredApplications()) {
            List<InstanceInfo> instances = application.getInstances();
            if(instances !=null) {
                data.add(new ChartData(instances.size(),application.getName().toLowerCase()));
            }
        }
        return data;
    }

    private class ChartData {
        private int y;
        private String label;

        public ChartData(int y, String label) {
            this.y=y;
            this.label=label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }
}