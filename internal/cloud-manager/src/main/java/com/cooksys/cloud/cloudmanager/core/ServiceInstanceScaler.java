package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Abstract scaler that handles service state, monitor and cooldown timers.  Implementations are only responsible
 * for the actual scale up, and reporting that the scale is complete
 *
 * @author Tim Davidson
 */
public abstract class ServiceInstanceScaler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceScaler.class);

    private ServiceStateManager serviceStateManager;
    private CooldownTimer cooldownTimer;

    public ServiceInstanceScaler(ServiceStateManager serviceStateManager, CooldownTimer cooldownTimer) {
        this.serviceStateManager = serviceStateManager;
        this.cooldownTimer = cooldownTimer;
    }

    /**
     * Method that is invoked when a scale up of a service is being requested
     *
     * @param serviceId serviceId that is registered in discovery
     * @param version version of the service
     * @param instances number of instances to scale
     */
    protected abstract void scale(String serviceId, String version, Integer instances);

    /**
     * Method that is invoked when it is requesting a report on whether the scale up has completed
     *
     * @param serviceId serviceId that is registered in discovery
     * @param version version of the service
     * @return
     */
    protected abstract boolean isScaleComplete(String serviceId, String version);

    public void scaleService(String serviceId, String version, Integer instances) {
        final ScaleState scaleState = serviceStateManager.getServiceState(new Service(serviceId, version));
        if (!ScaleState.NORMAL.equals(scaleState)) {
            logger.info("Ignoring request to scale up service " + serviceId + " version: " + version + " scaleState: " + scaleState);
            return;
        }

        logger.info("Scaling up serviceId: " + serviceId + " version: " + version);

        serviceStateManager.updateServiceState(new Service(serviceId, version), ScaleState.SCALING);
        this.scale(serviceId,version,instances);
        startServiceScaleMonitor(serviceId,version);
    }

    private void startServiceScaleMonitor(String serviceId, String version) {
        final Timer serviceScaleMonitorTimer = new Timer();
        serviceScaleMonitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isScaleComplete(serviceId,version)) {
                    logger.info("Scaleup complete for serviceId: " + serviceId + " version: " + version);
                    cooldownTimer.startServiceCooldown(serviceId,version);
                    this.cancel();
                }
            }
        }, 0, 10_000);
    }
}
