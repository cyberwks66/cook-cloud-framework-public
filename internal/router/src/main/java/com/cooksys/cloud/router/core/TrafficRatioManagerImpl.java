package com.cooksys.cloud.router.core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of TrafficRatioManager
 *
 * @author Tim Davidson
 */
public class TrafficRatioManagerImpl implements TrafficRatioManager {

    Map<String,List<RouteVersionDetails>> serviceTrafficRatioLists = new HashMap<>();
    Map<String,AtomicCounter> counters = new HashMap<>();

    @Override
    public void putRatio(String serviceId, RouteVersionDetails versionDetails, Integer ratio) {
        if(serviceTrafficRatioLists.get(serviceId) == null) {
            serviceTrafficRatioLists.put(serviceId,new LinkedList<>());
        }

        deleteRatio(serviceId,versionDetails);

        for(int i=0; i < ratio; i++) {
            serviceTrafficRatioLists.get(serviceId).add(versionDetails);
        }
        counters.put(serviceId,new AtomicCounter(serviceTrafficRatioLists.get(serviceId).size()));
    }

    @Override
    public void deleteRatio(String serviceId) {
        serviceTrafficRatioLists.remove(serviceId);
        counters.remove(serviceId);
    }

    @Override
    public void deleteRatio(String serviceId, RouteVersionDetails versionDetails) {
        final List<RouteVersionDetails> trafficRatios = serviceTrafficRatioLists.get(serviceId);

        if(trafficRatios.contains(versionDetails)) {
            Iterator<RouteVersionDetails> it = trafficRatios.iterator();

            while(it.hasNext()) {
                RouteVersionDetails entry = it.next();
                if(entry.equals(versionDetails)) {
                    it.remove();
                }
            }
        }
        counters.put(serviceId,new AtomicCounter(serviceTrafficRatioLists.get(serviceId).size()));
    }

    @Override
    public RouteVersionDetails getNextRouteVersionDetails(String serviceId) {
        if(counters.get(serviceId) == null) {
            return null;
        }
        int index = counters.get(serviceId).getAndIncrementOrReset();
        return serviceTrafficRatioLists.get(serviceId).get(index);
    }

    @Override
    public boolean configuredRatiosExist(String serviceId) {
        return serviceTrafficRatioLists.containsKey(serviceId) &&
                serviceTrafficRatioLists.get(serviceId) != null &&
                !serviceTrafficRatioLists.get(serviceId).isEmpty();
    }

    @Override
    public Map<RouteVersionDetails, Integer> getRatioMap(String serviceId) {
        Map<RouteVersionDetails, Integer> ratioMap = new HashMap<>();
        Set<RouteVersionDetails> uniqueDetails = new HashSet<>();

        if(serviceTrafficRatioLists.containsKey(serviceId)) {
            serviceTrafficRatioLists.get(serviceId).forEach(item -> {
                uniqueDetails.add(item);
            });

            for(RouteVersionDetails details : uniqueDetails) {
                int count=0;
                for(RouteVersionDetails item : serviceTrafficRatioLists.get(serviceId)){
                    if(item.equals(details)) {
                        count++;
                    }
                }
                ratioMap.put(details,count);
            }

        }

        return ratioMap;
    }

    /**
     * Thread safe counter that resets to 0 just before reaching max.
     * Set max to the array or list size.
     *
     */
    private static class AtomicCounter extends AtomicInteger {
        final int max;

        public AtomicCounter(int max) {
            super(0); // initial value
            this.max = max;
        }

        public int getAndIncrementOrReset() {
            while (true) {
                int current = get();
                int next = (current + 1) % max;
                if (compareAndSet(current, next))
                    return current;
            }
        }
    }
}
