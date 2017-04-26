package com.cooksys.cloud.router.filters.route.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateBasedRule;

/**
 * Predicate based rule that tells ribbon to filter the server list with the given predicate before load balancing using
 * resulted list of servers.
 *
 * @author Tim Davidson
 */
public abstract class DiscoveryEnabledRule extends PredicateBasedRule {
    private final CompositePredicate predicate;

    public DiscoveryEnabledRule(DiscoveryEnabledPredicate discoveryEnabledPredicate) {
        this.predicate = createCompositePredicate(discoveryEnabledPredicate, new AvailabilityPredicate(this, null));
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }

    /**
     * Creates the composite predicate containing given predicate for filtering and Availability predicate that
     * will filter the list even more by checking max connections and circuit breakers
     * @param discoveryEnabledPredicate
     * @param availabilityPredicate
     * @return
     */
    private CompositePredicate createCompositePredicate(DiscoveryEnabledPredicate discoveryEnabledPredicate, AvailabilityPredicate availabilityPredicate) {
        return CompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate)
                .build();
    }
}
