package com.cooksys.cloud.router.filters.route.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

/**
 * Wrapper for ribbon's AbstractServerPredicate that only calls apply() when the PredicateKey is an an instance
 * of DiscoveryEnabledServer.
 *
 * @author Tim Davidson
 */
public abstract class DiscoveryEnabledPredicate extends AbstractServerPredicate {
    @Override
    public boolean apply(PredicateKey input) {
        return input != null
                && input.getServer() instanceof DiscoveryEnabledServer
                && apply((DiscoveryEnabledServer) input.getServer());
    }

    protected abstract boolean apply(DiscoveryEnabledServer server);
}
