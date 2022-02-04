# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. It builds on top of

- [Testcontainers](https://www.testcontainers.org/)
- [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) and [Arquillian Drone](http://arquillian.org/arquillian-extension-drone/)
- [JUnit 5](https://junit.org/junit5/)

The goal is that tests should be self-contained. Containers are started and stopped when necessary and tests can focus on testing the UI and verifying management model changes. The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment (as this repository [does](.github/workflows/ci.yml)).

## Write Tests

Tests need to be annotated with two annotations (in this order!):

1. `@Manatoko`: This annotation activates two Junit 5 extensions:
   - `SystemSetupExtension`: Takes care of starting / stopping singleton containers once before / after all tests.
   - `ArquillianExtension`: Takes care about the Arquillian integration
2. `@Testcontainers`: Takes care of starting containers marked with `@Container`.

A simple test which tests adding a new system property could look like this:

```java
@Manatoko
@Testcontainers
class SystemPropertyTest {

   @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

   @Page SystemPropertyPage page;
   @Inject CrudOperations crud;
   TableFragment table;
   FormFragment form;

   @BeforeEach
   void prepare() {
      page.navigate();
      form = page.getForm();
      table = page.getTable();
      table.bind(form);
   }

   @Test
   void create() throws Exception {
      crud.create(systemPropertyAddress(CREATE_NAME), table, form -> {
         form.text(NAME, CREATE_NAME);
         form.text(VALUE, CREATE_VALUE);
      });
   }
}
```

## Run Tests

Tests can be run in two modes, controlled by the system property `test.environment`. Valid values are either `local` or `remote`.

### Remote

This is the default mode. In this mode a [web driver container](https://www.testcontainers.org/modules/webdriver_containers/) (with support of screen recording) is stated before all tests. An Arquillian extension is registered which provides a remote web driver connected to the browser running in this container.

### Local

This mode is activated by the maven profile `local`. In this mode a browser is started locally and Arquillian Graphene takes care of providing the web driver.

To run all tests, simply execute

```shell
./mvnw test -P all-tests 
```

To run the tests with a local browser, use 

```shell
./mvnw test -P all-tests,local 
```

If you just want to execute specific tests, `cd` into the directory containing the tests and run 

```shell
./mvnw test
```

If you want to execute one specific test or test method, use one of the following commands:

```shell
./mvnw test -Dtest=org.jboss.hal.testsuite.configuration.systemproperty.SystemPropertyTest
./mvnw test -Dtest=org.jboss.hal.testsuite.configuration.systemproperty.SystemPropertyTest#create
```

If you want to debug a test, append `-Dmaven.surefire.debug` and attach a debugger to port 5005. 

## Testcontainers, Podman & macOS

If you're using testcontainers with podman on macOS, please start `./tcpm.sh` and make sure to set the following environment variables **before** running the tests.

```sh
DOCKER_HOST=unix:///tmp/podman.sock
TESTCONTAINERS_CHECKS_DISABLE=true
TESTCONTAINERS_RYUK_DISABLED=true
```

See https://www.testcontainers.org/features/configuration/ and https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506 for details.
