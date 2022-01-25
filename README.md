# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. 

The goal is that tests should be self-contained. Therefore, tests should be able to

- start a fresh WildFly instance based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly)
- run HAL as standalone console based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal)
- make sure HAL uses the right management endpoint
- launch a [`WebDriver`](https://www.testcontainers.org/modules/webdriver_containers/) container which supports screen recording

This repository is a PoC that the above is possible using [testcontainers](https://www.testcontainers.org/). Testcontainers takes care of the container lifecycle and can inject container instances with the help of some simple annotations. The biggest advantage of this approach is that it is very easy to run UI tests in a CI environment like GitHub actions (see [ci.yml](.github/workflows/ci.yml)). 

Writing tests looks something like this:

```java
@Testcontainers
class SystemPropertyTest {

    @TempDir static File recordings;
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26);
    @Container static HalContainer console = HalContainer.instance();
    @Container static Browser chrome = Browser.chrome()
            .withRecordingMode(RECORD_FAILING, recordings, MP4);;

    @BeforeAll
    static void beforeAll() {
        console.connectTo(wildFly);
        OnlineManagementClient client = wildFly.managementClient();
        // use client to set up resources 
    }

    @AfterAll
    static void afterAll() {
        try (var client = wildFly.managementClient()) {
            // clean up resources 
        }
    }

    @Test
    void read() {
        WebDriver driver = chrome.driver();
        console.navigate(driver, NameTokens.SYSTEM_PROPERTIES);
        var table = driver.findElement(By.id(Ids.SYSTEM_PROPERTY_TABLE));
        // asserts
    }
}
```

Take a look at the source code for more details. 

## Testcontainers, Podman & macOS

If you're using testcontainers with podman on macOS, start `./tcpm.sh` and make sure to set the folloowing environment variables **before** running the tests.

```sh
DOCKER_HOST=unix:///tmp/podman.sock
TESTCONTAINERS_CHECKS_DISABLE=true
TESTCONTAINERS_RYUK_DISABLED=true
```

See https://www.testcontainers.org/features/configuration/ and https://github.com/testcontainers/testcontainers-java/issues/2088#issuecomment-911586506 for more details.
