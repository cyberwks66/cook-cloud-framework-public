package com.cooksys.cloud.router.core;

import java.util.Map;

/**
 * Manager that handles internal mapping of traffic ratio info to serviceId
 *
 * @author Tim Davidson
 */
public interface TrafficRatioManager {

    public void putRatio(String serviceId, RouteVersionDetails versionDetails, Integer trafficRatio);

    public void deleteRatio(String serviceId);

    public void deleteRatio(String serviceId, RouteVersionDetails versionDetails);

    public RouteVersionDetails getNextRouteVersionDetails(String serviceId);

    public boolean configuredRatiosExist(String serviceId);

    public Map<RouteVersionDetails,Integer> getRatioMap(String serviceId);

}
