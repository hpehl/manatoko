# Manatoko

Manatoko ([Maori](https://maoridictionary.co.nz/search?keywords=manatoko) for verify, test) is a new approach to test the [HAL](https:://hal.github.io) management console. 

The goal is that each test is self-contained. Tests will start a WildFly instance and a browser in docker containers. Therefore, the [testcontainers](https://www.testcontainers.org/) library is used.
