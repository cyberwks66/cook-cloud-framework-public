package com.cooksys.cloud.discovery.widget.api;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

/**
 * Root model class for graph widget API
 *
 * @author Tim Davidson
 */
public class GraphRoot {
    private Map<String,GraphNode> cluster;
    private List<GraphNode> children;

    public Map<String, GraphNode> getCluster() {
        return cluster;
    }

    public GraphRoot setCluster(Map<String, GraphNode> cluster) {
        this.cluster = cluster;
        return this;
    }

    public List<GraphNode> getChildren() {
        return children;
    }

    public GraphRoot setChildren(List<GraphNode> children) {
        this.children = children;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cluster", cluster)
                .add("children", children)
                .toString();
    }
}
