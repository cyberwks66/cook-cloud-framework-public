package com.cooksys.cloud.discovery.widget.api;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;

import java.util.*;

/**
 * Default implementation of {@link GraphWidgetService}
 *
 * @author Tim Davidson
 */
public class GraphWidgetServiceImpl implements GraphWidgetService {

    private static Set<String> FRAMEWORK_COMPONENTS = new HashSet<>();

    static {
        FRAMEWORK_COMPONENTS.add("discovery");
        FRAMEWORK_COMPONENTS.add("configuration");
        FRAMEWORK_COMPONENTS.add("turbine");
    }

    @Override
    public Map<String, GraphRoot> generateDiscoveryGraph() {
        final Map<String, GraphRoot> graph = new HashMap<>();

        final GraphRoot routingGraph = generateRoutingGraph();
        if (routingGraph != null && routingGraph.getChildren() !=null && !routingGraph.getChildren().isEmpty()) {
            graph.put("router-edge", routingGraph);
        }
        return graph;
    }

    private GraphRoot generateRoutingGraph() {
        final GraphRoot graphRoot = new GraphRoot();

        graphRoot.setCluster(generateEdgeCluster());
        graphRoot.setChildren(generateNodes());

        return graphRoot;
    }

    private Map<String, GraphNode> generateEdgeCluster() {
        final Application application = getRegistry().getApplication("ROUTER-EDGE");

        if (application == null) {
            return null;
        }

        final Map<String, GraphNode> edgeCluster = new HashMap<>();
        final List<InstanceInfo> instances = application.getInstances();

        for (InstanceInfo instance : instances) {
            final GraphNode node = new GraphNode();
            node.setType(NodeType.CORE_COMPONENT);
            node.setStatus(statusToString(instance.getStatus()));
            edgeCluster.put(instance.getHostName(), node);
        }
        return edgeCluster;
    }

    private List<GraphNode> generateNodes() {
        final Application routerNodeApplication = getRegistry().getApplication("ROUTER-NODE");
        if(routerNodeApplication==null) {
            return null;
        }
        final List<InstanceInfo> routerNodeInstances = routerNodeApplication.getInstances();

        if (routerNodeInstances == null || routerNodeInstances.isEmpty()) {
            return null;
        }

        final List<GraphNode> nodeList = new ArrayList<>();

        for (InstanceInfo nodeInstance : routerNodeInstances) {
            final GraphNode graphNode = new GraphNode();
            graphNode.setName("router-node");
            graphNode.setHost(nodeInstance.getHostName());
            graphNode.setChildren(generateChildrenForNode(nodeInstance.getHostName()));
            nodeList.add(graphNode);
        }
        return nodeList;
    }

    private List<GraphNode> generateChildrenForNode(String hostname) {
        final List<GraphNode> children = new ArrayList<>();
        final Applications applications = getRegistry().getApplications();

        for(Application application : applications.getRegisteredApplications()) {
            for(InstanceInfo instance : application.getInstances()) {
                if(instance.getMetadata() !=null && instance.getMetadata().containsKey("proxyHost") &&
                        hostname.equals(instance.getMetadata().get("proxyHost"))) {
                    final GraphNode child = new GraphNode();
                    child.setName(application.getName());
                    child.setType(NodeType.MICROSERVICE);
                    child.setVersion(instance.getMetadata().get("version"));
                    child.setHost(instance.getHostName());
                    child.setStatus(statusToString(instance.getStatus()));
                    children.add(child);
                }
            }
        }

        return children;
    }


    private String statusToString(InstanceInfo.InstanceStatus status) {
        switch (status) {
            case UP:
                return "UP";
            case DOWN:
                return "DOWN";
            case STARTING:
                return "STARTING";
            case OUT_OF_SERVICE:
                return "OUT_OF_SERVICE";
            default:
                return "UNKNOWN";
        }
    }

    private PeerAwareInstanceRegistry getRegistry() {
        return getServerContext().getRegistry();
    }

    private EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }
}
