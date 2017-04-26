package com.cooksys.cloud.commons.event.router;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Event that is triggered when a new Ratio is configured - consumed by router
 *
 * @author Tim Davidson
 */
public class ConfigureTrafficRatioBusEvent extends BusEvent {
    private  String serviceId;
    private  List<Ratio> trafficRatios;

    public ConfigureTrafficRatioBusEvent() {}

    public ConfigureTrafficRatioBusEvent(Object source, String serviceId, List<Ratio> trafficRatios) {
        super(source);
        this.serviceId = serviceId;
        this.trafficRatios=trafficRatios;
    }

    public String getServiceId() {
        return serviceId;
    }

    public ConfigureTrafficRatioBusEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public List<Ratio> getTrafficRatios() {
        return trafficRatios;
    }

    public ConfigureTrafficRatioBusEvent setTrafficRatios(List<Ratio> trafficRatios) {
        this.trafficRatios = trafficRatios;
        return this;
    }

    /**
     * Domain object for Traffic Ratio event
     */
    public static class Ratio {

        @NotBlank
        private String version;

        @NotNull
        private Integer trafficRatio;

        @NotBlank
        private String accuracy;

        private List<String> excludedVersions;

        public Ratio() {
        }

        public Ratio(String version, Integer trafficRatio, String accuracy, List<String> excludedVersions) {
            this.version = version;
            this.trafficRatio = trafficRatio;
            this.accuracy = accuracy;
            this.excludedVersions = excludedVersions;
        }

        public String getVersion() {
            return version;
        }

        public Ratio setVersion(String version) {
            this.version = version;
            return this;
        }

        public Integer getTrafficRatio() {
            return trafficRatio;
        }

        public Ratio setTrafficRatio(Integer trafficRatio) {
            this.trafficRatio = trafficRatio;
            return this;
        }

        public String getAccuracy() {
            return accuracy;
        }

        public Ratio setAccuracy(String accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public List<String> getExcludedVersions() {
            return excludedVersions;
        }

        public Ratio setExcludedVersions(List<String> excludedVersions) {
            this.excludedVersions = excludedVersions;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Ratio ratio = (Ratio) o;

            if (version != null ? !version.equals(ratio.version) : ratio.version != null) return false;
            if (trafficRatio != null ? !trafficRatio.equals(ratio.trafficRatio) : ratio.trafficRatio != null) return false;
            if (accuracy != null ? !accuracy.equals(ratio.accuracy) : ratio.accuracy != null) return false;
            return excludedVersions != null ? excludedVersions.equals(ratio.excludedVersions) : ratio.excludedVersions == null;

        }

        @Override
        public int hashCode() {
            int result = version != null ? version.hashCode() : 0;
            result = 31 * result + (trafficRatio != null ? trafficRatio.hashCode() : 0);
            result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
            result = 31 * result + (excludedVersions != null ? excludedVersions.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("version", version)
                    .add("trafficRatio", trafficRatio)
                    .add("accuracy", accuracy)
                    .add("excludedVersions", excludedVersions)
                    .toString();
        }
    }
}
