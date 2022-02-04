Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. It builds on top of

- [Testcontainers](https://www.testcontainers.org/)
- [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) and [Arquillian Drone](http://arquillian.org/arquillian-extension-drone/)
- [JUnit 5](https://junit.org/junit5/)

The goal is that tests should be self-contained. Containers are started and stopped when necessary and tests can focus on testing the UI and verifying management model changes. 

# Test Environment

Tests can be run in two modes, controlled by the system property `test.environment`. Valid values are either `local` or `remote`. 

## Remote

This is the default mode. In this mode a [web river container](https://www.testcontainers.org/modules/webdriver_containers/) (with support of screen recording) is stated before all tests. An Arquillian extension is registered which provides a remote web driver connected to the browser running in this container. 

## Local

This mode is activated by the maven profile `local`. In this mode the browser is started locally and Arquillian Graphene takes care of providing the web driver.

# Lifecycle

Independent of the mode, a HAL standalone console (based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal)) is started before all tests.  

Therefore, this repository defines a testing lifecycle and some base classes:  

1. Before **all** tests (one time setup)
   1. 
   2. Start a HAL standalone console (based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal))
2. Before **all** tests of a class extending [`WildFlyTest`](test-common/src/main/java/org/jboss/hal/testsuite/test/WildFlyTest.java)
   1. Start a fresh WildFly instance (based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly))
3. All tests (extending [`ManatokoTest`](test-common/src/main/java/org/jboss/hal/testsuite/test/ManatokoTest.java))
   1. Leverage [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) (provided by an [Arquillian Drone extension](https://github.com/arquillian/arquillian-extension-drone/blob/master/docs/drone-spi.adoc))

The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment (as this repository [does](.github/workflows/ci.yml)).

This repository is a PoC with some tests from the [HAL test suite](https://github.com/hal/testsuite.next) using the above features:

- [`BatchConfigurationTest`](test-configuration-batch/src/test/java/org/jboss/hal/testsuite/configuration/batch/BatchConfigurationTest.java)
- [`SystemPropertyTest`](test-configuration-systemproperty/src/test/java/org/jboss/hal/testsuite/configuration/systemproperty/SystemPropertyTest.java)

# Run Tests

In the base directory, simple execute

```shell
./mvnw test -P all-tests 
```

If you want to run only specific tests, change into one of the `test-configuration-*` directory and execute

```shell
./mvnw test 
```

# Testcontainers, Podman & macOS

If you're using testcontainers with podman on macOS, please start `./tcpm.sh` and make sure to set the following environment variables **before** running the tests.

```sh
DOCKER_HOST=unix:///tmp/podman.sock
TESTCONTAINERS_CHECKS_DISABLE=true
TESTCONTAINERS_RYUK_DISABLED=true
```

See https://www.testcontainers.org/features/configuration/ and https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506 for details.
