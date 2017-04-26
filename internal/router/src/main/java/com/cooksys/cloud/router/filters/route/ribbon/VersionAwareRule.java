package com.cooksys.cloud.router.filters.route.ribbon;

/**
 * Defines the rule for ribbon to use when filtering the server list.  By configuring this as a spring bean, it
 * will override Spring cloud's default rule.  Upon creation, sets our custom composite predicate to be used when filtering
 *
 * @author Tim Davidson
 */
public class VersionAwareRule extends DiscoveryEnabledRule {

    /**
     * Sets our custom composite predicate to be used when filtering
     */
    public VersionAwareRule() {
        this(new VersionAwarePredicate());
    }

    public VersionAwareRule(DiscoveryEnabledPredicate predicate) {
        super(predicate);
    }
}
