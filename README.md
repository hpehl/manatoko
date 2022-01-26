# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. It builds on top of [testcontainers](https://www.testcontainers.org/). 

The goal is that tests should be self-contained. Tests can easily

- start a fresh WildFly instance (based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly))
- run HAL as standalone console (based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal))
- make sure the console uses the right management endpoint
- use a remote web driver connected to a browser running in a [WebDriver container](https://www.testcontainers.org/modules/webdriver_containers/) (with support of screen recording)
- leverage [Arquillian Graphene 2](http://arquillian.org/arquillian-graphene/) (using the [Arquillian Drone SPI](https://github.com/arquillian/arquillian-extension-drone/blob/master/docs/drone-spi.adoc))

The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment like GitHub actions (see [ci.yml](.github/workflows/ci.yml)).

This repository is a PoC with a simplified test from the [HAL test suite](https://github.com/hal/testsuite.next) using the above features. In a nutshell, tests look something like this:

```java
@Testcontainers
@ExtendWith(ArquillianExtension.class)
class SystemPropertyTest {

    @TempDir static File recordings;
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26);
    @Container static Console console = Console.newInstance();
    @Container static Browser chrome = Browser.chrome()
            .withRecordingMode(RECORD_FAILING, recordings, MP4);

    @BeforeAll
    static void beforeAll() throws Exception {
        console.connectTo(wildFly);

        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(systemPropertyAddress(READ_NAME), Values.empty().and(VALUE, READ_VALUE));
    }

    @AfterAll
    static void afterAll() throws Exception {
        try (OnlineManagementClient client = wildFly.managementClient()) {
            Operations operations = new Operations(client);
            operations.removeIfExists(systemPropertyAddress(READ_NAME));
        }
    }

    @Page SystemPropertyPage page;

    @BeforeEach
    void beforeEach() {
        page.navigate();
    }

    @Test
    void readPage() {
        assertTrue(page.getTable().bodyContains(READ_NAME));
        assertTrue(page.getTable().bodyContains(READ_VALUE));
    }
}
```

## Testcontainers, Podman & macOS

If you're using testcontainers with podman on macOS, please start `./tcpm.sh` and make sure to set the following environment variables **before** running the tests.

```sh
DOCKER_HOST=unix:///tmp/podman.sock
TESTCONTAINERS_CHECKS_DISABLE=true
TESTCONTAINERS_RYUK_DISABLED=true
```

See https://www.testcontainers.org/features/configuration/ and https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506 for details.
