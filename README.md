# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. 

The goal is that tests should be self-contained. Therefore, tests should be able to

- start a fresh WildFly instance based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly)
- run HAL as standalone console based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal)
- make sure HAL uses the right management endpoint
- launch a [`WebDriver`](https://www.testcontainers.org/modules/webdriver_containers/) container which supports screen recording

The biggest advantage of this approach is that it is very easy to run the tests on GitHub actions as this repository [does](.github/workflows/ci.yml). 

This repository is a PoC that the above is possible using [testcontainers](https://www.testcontainers.org/). Testcontainers takes care of the container lifecycle and can inject container instances with the help of some simple annotations. Writing tests looks something like this:

```java
@Testcontainers
class SystemPropertyTest {

    @TempDir static File recordings;
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26);
    @Container static HalContainer console = HalContainer.instance();
    @Container static Browser chrome = Browser.chrome()
            .withRecordingMode(RECORD_FAILING, recordings, MP4);;

    @BeforeAll
    public static void beforeAll() {
        console.connectTo(wildFly);
        OnlineManagementClient client = wildFly.managementClient();
        // use client to set up resources 
    }

    @AfterAll
    public static void afterAll() {
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
