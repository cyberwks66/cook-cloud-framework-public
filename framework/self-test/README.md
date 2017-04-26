# Self-Test

This service contains various APIs for performing self tests on the framework itself.

## Event Bus Self-test
This self-test contains an API that broadcasts an event over the spring-cloud-bus to the other self-test instances.  Each instance that receives this event will respond by broadcasting an ACK event to all the other instances with its own unique instanceId.  There is a second API that retrieves this test's results.

### Configuration

TBD

### APIs

#### POST /busTest
This API initiates the spring cloud bus test scenario, and will return a json object with the TestID, and a list of self-test instances registered with discovery.

> Since this test relies on the discovery cache to be current, make sure you wait at least 2 minutes after starting or scaling the self-test service.  At least 2 instances of self-test must be running for proper validation of the event bus.

##### Request Parameters
None

##### Response Body
| Parameter | Description |
| --------- | ----------- |
testId | Unique ID of the test that is started
discoverySelftestInstances | Array (strings) of self-test unique instanceIds that are currently registered with Eureka

##### Example Request/response

Request:

```
POST http://localhost:8800/busTest
```

Response Body:

```
{
  "testId": "e705015d-9832-41da-bdb1-1625fad830c7",
  "discoverySelftestInstances": [
    "0ca1f45f-c253-4728-b10e-87a9bfd2b5c3",
    "06af8176-2617-4c42-b273-b27dd9d8a6f9"
  ]
}
```

#### GET /busTest/{testId}

This API retrieves the test results from the test initiated using the `POST /busTest` API.  It returns a list of the self-test instance IDs that have received the initial event and have published an ACK event.  By comparing this list to the list of discoverySelfTestInstances in the first API, you can verify that the event bus is working properly (the 2 lists should contain the same set of instances).

##### RequestParamters
| Parameter | Type | Description |
| --------- | ---- | ----------- |
testId | Path Variable | The testId returned from the POST request from this resource

##### Response Body
| Paramter | Description |
| -------- | ----------- |
testId | The testId requested
testInitiatedTimestamp | The timestamp of when the POST request was initiated to start the test
acknowledgedInstances | A list of self-test instances that have acknowledged the initial event

##### Example Request/Response

Request:

```
GET http://localhost:8800/busTest/e705015d-9832-41da-bdb1-1625fad830c7
```

Response:

```
{
  "testId": "e705015d-9832-41da-bdb1-1625fad830c7",
  "testInitiatedTimestamp": 1487713688106,
  "acknowledgedInstances": [
    "0ca1f45f-c253-4728-b10e-87a9bfd2b5c3",
    "06af8176-2617-4c42-b273-b27dd9d8a6f9"
  ]
}
```


