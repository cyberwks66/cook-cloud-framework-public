# Routes API Documentation

## Routes Resource `/routes`
The /routes resource allows you to configure the Edge-proxy and Node-proxy's  routes to a specific serviceId. 

### Route Field Definition

| Field | Description |
| ----- | ----------- |
| serviceId | The name of the service to route to - also the unique Id of a route |
| vHost | The host header associated with the route.  Each service should have a unique DNS name that points to the IP address of the Edge-proxy. |
| defaultVersion | The minimum version of the service to load-balance to (if version is not supplied in the request URL)
| proxyRoutes | a Map of URL prefix to version, used by the node proxy to forward to an exact service running its same host |

### Endpoints

#### `GET /routes`
Gets a list of all configured routes.

#### `GET /routes/{serviceId}`
Returns a specific route

#### `POST /routes`

Creates a route.  When creating a route using the API, an event is sent over the bus, so all router instances are updated with the new route.  The following is an example request body (application/json)


```
{
  "serviceId": "hello-world",
  "vHost": "hello-world.cooksys.com",
  "defaultVersion": "1.0.1",
  "proxyRoutes": {
    "/hello-world/1/0/1": "1.0.1",
    "/hello-world/1/0/2": "1.0.2"
  }
}
```

#### `PUT /routes/{serviceId}`
Modify and existing route.  All fields are optional.

#### `DELETE /routes/{serviceId}`
Deletes a route.

## Ratios Resource `/routes/{serviceId}/ratios`
The Ratios resource is used to configure advanced routing in the case of canary and blue/green deployments and is defined as an array of the following object::

### Ratio Field Definition
| Field | Description |
| ----- | ----------- |
| version | version to apply the ratio to - also the uniqueID
| trafficRatio | ratio (weight) of requests to forward to this version
| accuracy | specifies version accuracy for load balancing - MAJOR, MINOR, or PATCH
| excludeVersions | specifies exact versions to exclude from loadBalancer when accuracy is set to MAJOR or PATCH

### Endpoints

#### `GET /routes/{serviceId}/ratios`
Returns a list of configured traffic ratios for a route.

#### `POST /routes/{serviceId}/ratios`
Creates a new set of traffic ratio rules for a route. Message body is json payload containing an array of Ratio object.

#### `DELETE /routes/{serviceId}/ratios`
Deletes the ratio rules for a service.

#### Example
> It is recommended to use the lowest common denominator when configurin ratios.  For example, if you want 50% load to each version:
> 
> ```
> version: 1.0.1 - trafficRatio: 1 (ratio is 1/2)
> version: 1.0.3 - trafficRatio: 1 (ratio is 1/2)
> ```
> 
> This is better than:
> 
> ```
> version: 1.0.1 - trafficRatio: 5 (ratio is 5/10)
> version: 1.0.3 - trafficRatio: 5 (ratio is 5/10)
> ```
> 
> The first scenario will route every other request to each version, whereas the second will route 5 to one, then 5 to the other.

The following shows a requests that will define a simple canary deployment.  In this example, if we have 1.0.1, 1.0.2, and 1.0.3 versions of a service running, the ratio configuration tells the filters to load balance 9 out of 10 requests to versions 1.0.1 and 1.0.2 (excluding 1.0.3, since we are specifying a specific ratio for it) - and 1 out of 10 requests will route specifically to 1.0.3.

```
POST /routes/hello-world/ratios
[
  {
    "version": "1.0.1",
    "trafficRatio": 9,
    "accuracy": "MINOR",
    "excludeVersions": [
      "1.0.3"
    ]
  },
  {
    "version": "1.0.3",
    "trafficRatio": 1,
    "accuracy": "PATCH" 
  }
]
```

