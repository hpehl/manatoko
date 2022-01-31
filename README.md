# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. It builds on top of

- [Testcontainers](https://www.testcontainers.org/)
- [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) and [Arquillian Drone](http://arquillian.org/arquillian-extension-drone/)
- [JUnit 5](https://junit.org/junit5/)

The goal is that tests should be self-contained. Containers are started when necessary and test classes can focus on testing the UI and verifying management model changes.

1. Before **all** tests (one time setup)
   1. Provide a remote web driver connected to a browser running in a [WebDriver container](https://www.testcontainers.org/modules/webdriver_containers/) (with support of screen recording)
   3. Start a HAL standalone console (based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal))
2. Before **all** tests of a class extending [`WildFlyTest`](test-common/src/main/java/org/jboss/hal/manatoko/test/WildFlyTest.java)
   1. Start a fresh WildFly instance (based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly))
3. For all tests
   1. Leverage [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) (provided by an [Arquillian Drone extension](https://github.com/arquillian/arquillian-extension-drone/blob/master/docs/drone-spi.adoc))

The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment (as this repository [does](.github/workflows/ci.yml)).

This repository is a PoC with some tests from the [HAL test suite](https://github.com/hal/testsuite.next) using the above features:

- [`BatchConfigurationTest`](test-configuration-batch/src/test/java/org/jboss/hal/manatoko/configuration/batch/BatchConfigurationTest.java)
- [`SystemPropertyTest`](test-configuration-systemproperty/src/test/java/org/jboss/hal/manatoko/configuration/systemproperty/SystemPropertyTest.java)

## Run Tests

In the base directory, simple execute

```shell
./mvnw verify -P all-tests 
```

## Testcontainers, Podman & macOS

If you're using testcontainers with podman on macOS, please start `./tcpm.sh` and make sure to set the following environment variables **before** running the tests.

```sh
DOCKER_HOST=unix:///tmp/podman.sock
TESTCONTAINERS_CHECKS_DISABLE=true
TESTCONTAINERS_RYUK_DISABLED=true
```

See https://www.testcontainers.org/features/configuration/ and https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506 for details.
