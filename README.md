# cook-cloud-framework

Test Change
Build the framework using `gradle build` from the root of the repository.  Build the docker containers locally with `gradle buildDocker`.

## Reference and Documentation
### High-level (3rd party)
* [Building distributed Systems with Netflix OSS and Spring Cloud](https://www.youtube.com/watch?v=hV5TTSiFhRs)
* [Building Microservice with Spring Cloud and Netflix OSS](http://callistaenterprise.se/blogg/teknik/2015/04/10/building-microservices-with-spring-cloud-and-netflix-oss-part-1/)

### NetFlix OSS Reference
* Eureka
  * [Source](https://github.com/Netflix/eureka)
* Hystrix
  * [Source](https://github.com/Netflix/Hystrix)
* Ribbon
  * [Source](https://github.com/Netflix/ribbon)
* Zuul
  * [Source](https://github.com/Netflix/zuul)

### Spring Reference

* [Spring Cloud + Netflix](http://cloud.spring.io/spring-cloud-static/spring-cloud-netflix/)
* [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

mainly spring boot, spring cloud netflix, and docker are the foundation
specifically in spring cloud: eureka, zuul, hystrix, ribbon, configserver, spring cloud bus

### Core Framework Components
* [Configuration](./internal/configuration/README.md)
* [Router](./internal/router/README.md)
* [Self-Test API](./framework/self-test/README.md)

## Tutorials
This is a list of tutorials for demonstrating various features of the framework.  It is recommended to follow the heirarchy in order below, as some tutorials have dependencies on others.

* [Configuration Tutorial (start here)](./internal/configuration/configuration_tutorial.md)
	* [Manually Running Router in Edge Proxy and Node Proxy modes](./internal/router/tutorial.md)
		* [Traffic Ratio Demo](./internal/router/traffic_ratio_demo.md)