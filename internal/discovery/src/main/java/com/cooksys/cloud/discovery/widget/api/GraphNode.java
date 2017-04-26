package com.cooksys.cloud.discovery.widget.api;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * Node model class for graph widget API
 *
 * @author Tim Davidson
 */
public class GraphNode {
    private String name;
    private NodeType type;
    private String version;
    private String host;
    private String status;
    private List<GraphNode> children;

    public String getName() {
        return name;
    }

    public GraphNode setName(String name) {
        this.name = name;
        return this;
    }

    public NodeType getType() {
        return type;
    }

    public GraphNode setType(NodeType type) {
        this.type = type;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public GraphNode setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getHost() {
        return host;
    }

    public GraphNode setHost(String host) {
        this.host = host;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public GraphNode setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<GraphNode> getChildren() {
        return children;
    }

    public GraphNode setChildren(List<GraphNode> children) {
        this.children = children;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .add("version", version)
                .add("host", host)
                .add("status", status)
                .add("children", children)
                .toString();
    }
}
