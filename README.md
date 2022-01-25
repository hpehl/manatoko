# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https://hal.github.io) management console. 

The goal is that tests should be self-contained. Therefore, tests should 

- start a fresh WildFly instance based on [quay.io/halconsole/wildfly](https://quay.io/repository/halconsole/wildfly)
- run HAL as standalone console based on [quay.io/halconsole/hal](https://quay.io/repository/halconsole/hal)
- make sure HAL uses the right management endpoint
- launch a [`WebDriver`](https://www.testcontainers.org/modules/webdriver_containers/) container which supports screen recording

Most of the above is made possible by the [testcontainers](https://www.testcontainers.org/) library. 

## Writing Tests

Writing tests looks something like this:

```java
@Testcontainers
class SystemPropertyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26);
    @Container static HalContainer console = HalContainer.instance();
    @Container static Browser chrome = Browser.chrome();

    @BeforeAll
    public static void beforeAll() {
        console.connectTo(wildFly);
        OnlineManagementClient client = wildFly.managementClient();
        // use client to set up resources, ... 
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

Take a look at the test source code for more details. 
