#Configuration Tutorial
This tutorial will walk you through setting up a configuration repository and starting discovery and configuration services.   The configuration service is configured for discovery-first by default.

## Prerequsites 
* RabbitMQ should be installed and running on its default port
* RabbitMQ management plugin is enabled
* Docker should be installed and running
* Framework's Docker images are built and installed on this host

## Steps
Verify that rabbit is up and running by pointing your browser to `http://<rabbit_hostname>:15672 `
> Default username is "guest" password is "guest"

Start the discovery container:

```
docker run -d --name discovery -p8761:8761 discovery
```
Verify that discovery is running (after about a min) by pointing your browser to `http://<discovery_hostname>:8761`

Create a git repository in `$HOME/.cook/configuration-service/git`:

```
mkdir -p $HOME/.cook/configuration-service.git
cd $HOME/.cook/configuration-service.git
git init
```

Create an `application.yml` file in this directory.  This will hold configuration that is common to all framework components.  Here we will put the URLs that point to rabbitMQ.  The contents of the file should look like this (make sure to replace the hostname):

```
spring:
  rabbitmq:
    host: <hostname/IP>
    port: 5672
    username: guest
    password: guest
```
> Note that rabbit hostname in this configuration must be able to be reached from within docker containers, so using `localhost` will never work here.  If you are working in a local environment where DNS is not configured, you should use the IP address of the host itself.  If you are not sure if DNS is present, it never hurts to just use the IP address

Check in the file:

```
git add application.yml
git commit -m "initial commit" application.yml
```

Start the configuration service:

> Set the DISCOVERY_HOST shell variable to the hostname or IP of where you started discovery

```
docker run -d --name configuration -p 8888:8888 \
    -v ~/.cook/configuration-service/git:/tmp/git \
    configuration --spring.cloud.config.server.git.uri=file:///tmp/git \
    --eureka.instance.hostName=`hostname` \
    --eureka.client.serviceUrl.defaultZone=http://$DISCOVERY_HOST:8761/eureka/
```

Test that configuration is running properly:

```
curl http://localhost:8888/configuration/default
```

Verify that the rabbitmq properties we entered in the repo exist.  Response should look something like this:

```
{
  "name": "configuration",
  "profiles": [
    "default"
  ],
  "label": "master",
  "version": "6f0ca6fe459cdd5f82062608adc76c6f64b82c8c",
  "state": null,
  "propertySources": [
    {
      "name": "file:///tmp/git/application.yml",
      "source": {
        "spring.rabbitmq.host": "ip-10-254-1-144",
        "spring.rabbitmq.port": 5672,
        "spring.rabbitmq.username": "guest",
        "spring.rabbitmq.password": "guest"
      }
    }
  ]
}
```
