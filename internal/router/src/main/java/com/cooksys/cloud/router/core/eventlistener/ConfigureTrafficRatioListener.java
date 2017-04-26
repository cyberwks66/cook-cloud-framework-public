package com.cooksys.cloud.router.core.eventlistener;

import com.cooksys.cloud.router.core.RouteVersionDetails;
import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.cooksys.cloud.router.core.TrafficRatioManager;
import com.cooksys.cloud.commons.event.router.ConfigureTrafficRatioBusEvent;
import com.github.zafarkhaja.semver.Version;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Event listener for {@link ConfigureTrafficRatioBusEvent}
 *
 * @author Tim Davidson
 */
public class ConfigureTrafficRatioListener implements ApplicationListener<ConfigureTrafficRatioBusEvent> {

    private TrafficRatioManager trafficRatioManager;

    public ConfigureTrafficRatioListener(TrafficRatioManager trafficRatioManager) {
        this.trafficRatioManager = trafficRatioManager;
    }

    @Override
    public void onApplicationEvent(ConfigureTrafficRatioBusEvent event) {
        for (ConfigureTrafficRatioBusEvent.Ratio ratio : event.getTrafficRatios()) {

            List<Version> excludedVersions=null;

            if(ratio.getExcludedVersions()!=null) {
                excludedVersions=ratio.getExcludedVersions()
                        .stream()
                        .map(s -> Version.valueOf(s))
                        .collect(Collectors.toList());
            }

            final SemanticAccuracy accuracy;
            switch (ratio.getAccuracy()) {
                case "MAJOR":
                    accuracy = SemanticAccuracy.MAJOR;
                    break;
                case "MINOR":
                    accuracy = SemanticAccuracy.MINOR;
                    break;
                default:
                    accuracy = SemanticAccuracy.PATCH;
            }

            final Version version = Version.valueOf(ratio.getVersion());
            final RouteVersionDetails routeVersionDetails = new RouteVersionDetails(version,accuracy,excludedVersions);

            trafficRatioManager.putRatio(event.getServiceId(),routeVersionDetails,ratio.getTrafficRatio());
        }
    }
}
