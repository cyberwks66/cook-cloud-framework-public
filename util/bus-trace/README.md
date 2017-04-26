# Bus Trace Utility

This command line utility allows you to see events on the Spring Cloud Bus in realtime for debugging purposes.

## Usage

To view the event stream (all events), simply run the command:

```
docker run bus-trace
```

You can also filter by specific events by passing their class name as a command line parameter.  For instance to only see `ConfigureTrafficRatioBusEvent` events:

```
docker run bus-trace ConfigureTrafficRatioBusEvent
```

You may also specifiy multiple event types:

```
docker run bus-trace ConfigureTrafficRatioBusEvent ScaleUpServiceBusEvent
```

## Example Output

```
{
  "type" : "ConfigureTrafficRatioBusEvent",
  "timestamp" : 1492108346320,
  "originService" : "router-edge:53953106-4a28-42ac-acc9-572c06ec033d",
  "destinationService" : "**",
  "id" : "d2447efe-7291-421b-87e8-d8483131beb2",
  "serviceId" : "airlines",
  "trafficRatios" : [ {
    "version" : "1.0.1",
    "trafficRatio" : 1,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  }, {
    "version" : "1.0.2",
    "trafficRatio" : 1,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  } ]
}
{
  "type" : "ConfigureTrafficRatioBusEvent",
  "timestamp" : 1492108362457,
  "originService" : "router-edge:5111d8af-eef4-4ab9-8ce7-9f7ea4cbd1c4",
  "destinationService" : "**",
  "id" : "a2ad685e-a001-4de6-a54c-446cc1c0dcf1",
  "serviceId" : "airlines",
  "trafficRatios" : [ {
    "version" : "1.0.1",
    "trafficRatio" : 51,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  }, {
    "version" : "1.0.2",
    "trafficRatio" : 49,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  } ]
}
{
  "type" : "ConfigureTrafficRatioBusEvent",
  "timestamp" : 1492108371755,
  "originService" : "router-edge:df662b12-909a-450b-8a48-91bb5a840127",
  "destinationService" : "**",
  "id" : "b432bcf9-a8c9-464a-9f95-96760b3b87e7",
  "serviceId" : "airlines",
  "trafficRatios" : [ {
    "version" : "1.0.1",
    "trafficRatio" : 1,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  }, {
    "version" : "1.0.2",
    "trafficRatio" : 0,
    "accuracy" : "PATCH",
    "excludedVersions" : null
  } ]
}
```
