package com.cooksys.cloud.router.filters.route.ribbon;

import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.github.zafarkhaja.semver.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.util.List;

/**
 * Wrapper around the ribbon client that allows us to load balance based on semantic version
 *
 * @author Tim Davidson
 */
public class VersionAwareRibbonClient {
    private static final Logger logger = LoggerFactory.getLogger(VersionAwareRibbonClient.class);

    private final LoadBalancerClient ribbonClient;

    public VersionAwareRibbonClient(LoadBalancerClient ribbonClient) {
        this.ribbonClient = ribbonClient;
    }

    public ServiceInstance getNextServerFromLoadBalancer(String serviceId, Version version, SemanticAccuracy semanticAccuracy) {
        // This tells ribbon to filter its list of servers by version and semantic accuracy
        // in addition to serviceId, which we pass in to the choose() method
        if(version!=null) {
            RibbonFilterContextFactory.getCurrentContext().setSemanticAccuracy(semanticAccuracy);
            RibbonFilterContextFactory.getCurrentContext().setVersion(version);
        } else {
            RibbonFilterContextFactory.getCurrentContext().setVersion(null);
        }

        // Get the next service instance from the load balancer
        final ServiceInstance serviceInstance = ribbonClient.choose(serviceId);

        logger.info("getNextServerFromLoadBalancer: " + serviceInstance);
        return serviceInstance;
    }

    public ServiceInstance getNextServerFromLoadBalancer(String serviceId, Version version, SemanticAccuracy semanticAccuracy,String proxyHost) {
        if(proxyHost!=null) {
            RibbonFilterContextFactory.getCurrentContext().setProxyHost(proxyHost);
        }
        return getNextServerFromLoadBalancer(serviceId,version,semanticAccuracy);
    }

    public ServiceInstance getNextServerFromLoadBalancer(String serviceId, Version version, SemanticAccuracy semanticAccuracy, List<Version> excludedVersions) {
        RibbonFilterContextFactory.getCurrentContext().setExcludedVersions(excludedVersions);
        return getNextServerFromLoadBalancer(serviceId,version,semanticAccuracy);
    }
}
