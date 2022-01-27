# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. It builds on top of [Testcontainers](https://www.testcontainers.org/). 

The goal is that tests should be self-contained. Tests can easily

- start a fresh WildFly instance (based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly))
- run HAL as standalone console (based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal))
- make sure the console uses the right management endpoint
- use a remote web driver connected to a browser running in a [WebDriver container](https://www.testcontainers.org/modules/webdriver_containers/) (with support of screen recording)
- leverage [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) (provided by an [Arquillian Drone extension](https://github.com/arquillian/arquillian-extension-drone/blob/master/docs/drone-spi.adoc))

The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment (as this repository [does](.github/workflows/ci.yml)).

This repository is a PoC with a [test](test-configuration-systemproperty/src/test/java/org/jboss/hal/manatoko/configuration/systemproperty/SystemPropertyTest.java) from the [HAL test suite](https://github.com/hal/testsuite.next) using the above features. See also [`ManatokoTest`](test-common/src/main/java/org/jboss/hal/manatoko/test/ManatokoTest.java) for the container setup.

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
