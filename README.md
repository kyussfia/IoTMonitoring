# Monitoring
## Introduction
This repository contains a solution for a coding assignment given during an **ALDI** job interview in 2026.  
The task was to pull up the environment for the received, incomplete project prototype to finish it while ensuring its errorless, smooth execution.
The project is about an IoT device monitoring tool.
## Description

We are building an **IoT monitoring system** to collect, process, and analyze device data.
The project has already been started, and we are looking for it to be completed. 

A detailed project description and the remaining tasks can be found in the [TASKS.md](./TASKS.md) file located in the root directory.

---

## 🚀 Assignment Instructions
1. Clone this repository
2. Review the [TASKS.md](./TASKS.md) file.
3. Complete the remaining implementation tasks.
4. Ensure the project is working end-to-end.
5. Upload the finished project into your own **GitHub repository**.
6. Share the repository link with us.

Please complete the assignment **within one week**.

---

## 🚀 Setup & Running

### Prerequisites
- Java 17 or later
- JUnit 5 (the test suite uses `org.junit.jupiter.api`)

### Build & Test
No external build tool is required – the code compiles with plain `javac`.  
To compile and run the tests manually:

```bash
# Compile all .java files (assuming standard directory layout)
javac -d out $(find . -name "*.java")

# Run the test class (JUnit Platform Console Launcher needed for standalone execution)
# Alternatively, use your IDE or a build tool (Maven/Gradle) with JUnit 5.
```

### Example Usage

```java
```

---

## 🧠 Thought Process

### 🔧 Task #0: Use Testcontainers
- Installed and configured docker for Windows. 
- Updated WSL subsystem.
- Provided project persistence using docker: `docker-compose up`
- Still aFailing run tests, due to docker version incompatibility.
- Discovered that the application was looking for docker version `1.32`.
- Extended docker `min-api-version` to `1.32`.
- Successfully ran PoC test (`Task0Test`) to verify.

---
### 🔧 Task #1: `Sensor.type` persistent type migration
> ***First touch*** \
> Can't yet decide whether it's an app lifecycle - migration problem or some remapping somewhere on the serialization-normalization-persistence line.

After checking of the source files, it seems like the `Sensor` entity is well composed, including the type of field `type`. Clean enum. And that's why I also try to keep it that way, so as the solution I choose the spring supported `@Enumerated` annotation with its type parameter. (Declared as String) 

### 🔧 Task #2: Sensor workflow components' relations
> ***First touch*** \
> It seems like two independent oneToMany relation declarations in EntityFirst approach, consider use of annotations.

Further requirements:
- Extend base entities: `Sensor`, `SensorReading`, `Alert`.
- Slave-side foreign key constraint: `sensor_id`.
- Slave-side parent back reference: `Sensor : getSensor()`.
- Master-side collector functions: `List<SensorReading>`, `List<Alert>`.

Solved with standard best practices (annotations).

### 🔧 Task #3: Centralized service
> ***First touch*** \
> A little centralization practice: generalized persisting under specialized interfaces.

Focusing on the `SensorReadingService`, implementing `SensorReadingService.saveSensorReading`.
Constructed instance of `SensorReading` from the dto parameter, then persisted it.

Also noticed that the signature of `SensorReadingRepository` contains a possible typo. To keep the type consistency, the extended type `JpaRepository<SensorReading, String>` should be changed to `Long` as it is the primary key.  

Further notable points: concurrency, transactional, error handling.

### 🔧 Task #4: Sensor POST endpoint issues
> ***First touch*** \
> Seems like some error handling: the first one seems closer for validations, the second is differing on origin: that one indicates some database driver error handling storyline.

As the task requires fixes in the implementation, I pointed out four subtasks:
1. I added validation constraints in the subject entity `SensorDto`.
2. Implement a basic uniqueness check upon save.
3. Enabling validation on the controller-action via annotation.
4. Drop the uniqueness down to the jpa level.

Further notable points: concurrency, transactional, error handling.

Hanging points:
- I think the error-handling is starting to get into a point where it is getting to be beneficial if it would be separated to gain more control over that. I precisely mean the goal to not let interface related exceptions on the service layer, generalize the exception instead there. And left the specific `ResponseStatusException`s usages only in the controller layer.
- The "good enough" unique constraint dilemma (extended table annotation?)

### 🔧 Task #5: Alert endpoint extensions
> ***First touch*** \
> New controller-action implementation.

Example: `http://localhost:8080/alerts/latest?sensorId=777d727e-6650-415f-85eb-9c9ca05f65c1`
Further requirements:
- Introduce `AlertController#getRecentAlertsBySensorId`
- URL: `/alerts/latest`
- Mandatory query parameter: `sensorId`
- 404 Not Found on an empty result

The solution has three elements:
- `AlertController`: The entrypoint calling the service layer.
- `AlertService`: The service using the repository layer to composose the desired functionality.
- `AlertRepository`: The repository layer, to define feautre specific method with wired ordering.

### 🔧 Task #7: Adding MeasurementService functions
> ***First touch*** \
> If its place is given, only the function's body is missing.

Implement `SensorReading : MeasurementService.getAverageTemperature` as the method, which must return the average temperature reading over a specified time period.
- The parameter period is passed as a parameter or as a configuration property?
- Is there any need for some type validation?

After checking the implementation, the first opened questions could be closed.
The problem is solved with standard cook-book practices:
- Feature-specific querying in the repository layer.
- Straight forward and minimal service layer implementation.

---
## Optionals
### 🔧 Task #6: AlertService & Kafka integration
> ***First touch*** \
> Is there any way to generally centralize any entities?...

Centralized entity service again, now it is on `Alert`.
Triggering Kafka messages, consider using premade tools.

### 🔧 Task #8: Extend Measurement API
> ***First touch*** \
> New controller-action implementation.

Implement `List<Measurement> : MeasurementService.getMeasurementValuesBySensorType` with order on `timestamp`.
QA:
- Order direction is not specified.
- So what should be the returning type? Only a list of numbers hiding the order and the object heritage or can keep the object perspective?

### 🔧 Task #9: TODO1
> ***First touch*** \
> New controller-action implementation.

TODO

### 🔧 Task #10: TODO2
> ***First touch*** \
> New controller-action implementation.

TODO

## 🔧 Interesting Generalisations
---

## ✅ What Was Learned
---

## 📄 License

This code is provided for portfolio purposes as part of a job interview process.  
Feel free to use it as a reference.
---

*Submitted for ALDI interview – kyussfia - 2026*
