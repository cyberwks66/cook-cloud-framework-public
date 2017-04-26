package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.cloudmanager.core.event.KillServiceInstanceEvent;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.context.ApplicationListener;

/**
 * Abstract event listener for {@link KillServiceInstanceEvent} that is published in the context when requesting
 * to kill a specific service instance.  Extended class is only required to kill the task.  Cooldown timer and state
 * is handled by the abstraction.
 *
 * Note - the extended class should be registered as a bean in the spring context
 *
 * @author Tim Davidson
 */
public abstract class KillServiceInstanceListener implements ApplicationListener<KillServiceInstanceEvent> {
    private CooldownTimer cooldownTimer;

    public KillServiceInstanceListener(CooldownTimer cooldownTimer) {
        this.cooldownTimer = cooldownTimer;
    }

    /**
     * Method is invoked when abstraction is requesting to kill a specific instance.
     *
     * @param instance eureka {@link InstanceInfo} from discovery
     * @param serviceId the serviceId registered with discovery
     * @param version the version registered in discovery metadata
     */
    protected abstract void killServiceInstance(InstanceInfo instance, String serviceId, String version);

    @Override
    public void onApplicationEvent(KillServiceInstanceEvent event) {
        this.killServiceInstance(event.getServiceInstance(),event.getServiceId(),event.getVersion());
        cooldownTimer.startServiceCooldown(event.getServiceId(),event.getVersion());
    }
}
