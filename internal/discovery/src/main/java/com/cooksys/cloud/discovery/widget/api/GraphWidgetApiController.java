package com.cooksys.cloud.discovery.widget.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Graph widget API - used to display registered nodes/services in a tree graph
 *
 * @author Tim Davidson
 */
@Controller
@CrossOrigin(maxAge = 3400)
public class GraphWidgetApiController {
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS_DESC = "statusDesc";
    public static final String MESSAGE = "message";
    public static final String GRAPH = "graph";

    private GraphWidgetService graphWidgetService;

    @Autowired
    public GraphWidgetApiController(GraphWidgetService graphWidgetService) {
        this.graphWidgetService = graphWidgetService;
    }

    @CrossOrigin
    @RequestMapping(value = "/graph",method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String,Object>> getGraph() {
        final Map<String,Object> response = new LinkedHashMap<>();

        Map<String,GraphRoot> graph = graphWidgetService.generateDiscoveryGraph();

        if(graph==null || graph.isEmpty()) {
            response.put(STATUS_CODE, "404");
            response.put(STATUS_DESC,"Not Found");
            response.put(MESSAGE,"There are no instances registered with Eureka");

            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

        response.put(STATUS_CODE,"200");
        response.put(STATUS_DESC,"Success");
        response.put(GRAPH,graph);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
