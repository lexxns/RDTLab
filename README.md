
# RDTLab - RealDevice Testing Lab

The end-goal of this project is to allow engineers to test hardware devices on any test rig. Those rigs are likely to have multiple hardware-interfaces (API's or serial/USB etc). tex-server should be capable of registering new rigs & associating hardware abstractions with those rigs. It should also allow tests to be defined and run on rigs which support them

## Future Work
Multi-Device Support: The main() function in the Main.kt file currently creates and runs tests on only one MockDevice. It would be beneficial to provide an ability to test multiple devices simultaneously to simulate a real-world scenario.

Advanced Event Types/Triggers: The existing event types (RoutineEvent and ThresholdEvent) seem basic in nature and there's a potential to enhance this area. It may be useful to introduce complex event types or triggers based on a combination of metrics or time-bound metrics.

Test Configuration: At present, there is no clear mechanism provided for test configuration (like test duration, thresholds for telemetry, metric names etc.). It could be configured in the executeTest() function of the TestRunner.kt (duration parameter), or within the ThresholdEvent setup in SimpleResultCollector.kt, but these are hard-coded values.

Error Handling and Fault Tolerance: There is some exception handling mechanism in the run() function in TestRunner.kt, however, there should be robust strategies established for error handling and fault tolerance for dealing with device failures or erratic behaviors.

Data Storing/Persistence: The telemetry data, after being collected, is only be used for instant reports and isn't stored persistently for further analysis. The ability to store data can help perform trend analysis, model training for predictive analysis, etc.

Visual Reporting: The framework currently uses a console-based report generated by the printReport() function in Main.kt. However, in reality, telemetry data can be vast and complex, and visualizing it through charts, graphs, or a UI dashboard can significantly improve readability and analysis.

Real-Time Monitoring: While the project is capable of periodic polling of the device telemetry, it lacks a real-time monitoring feature which is pretty critical in many systems for instant anomaly detection and quick actions.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/tex-server-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)


