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

## 🧠 Solution Path

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
> #### First read
> Can't yet decide whether it's a migration problem or some remapping around serialization or normalization.

### 🔧 Task #2: Sensor workflow components' relations
> #### First read
> It seems like two independent oneToMany relation declarations in EntityFirst approach, consider use of annotations.

Further requirements:
- Extend base entities: `Sensor`, `SensorReading`, `Alert`.
- Slave-side foreign key constraint: `sensor_id`.
- Slave-side parent back reference: `Sensor : getSensor()`
- Master-side collector functions: List<SensorReading>, List<Alert>.
- ???? Getter and setter method stubs are already prepared for the new properties and must be fully implemented.

### 🔧 Task #3: Centralized service
> #### First read
> A little centralization practice: generalized persisting under specialized interfaces.

Focusing on the `SensorReadingService`, implementing `SensorReadingService.saveSensorReading`.

### 🔧 Task #4: Sensor POST endpoint issues
> #### First read
> Seems like some error handling: the first one seems closer for validations, the second is differing on origin: that one indicates some database driver error handling storyline.

### 🔧 Task #5: Alert endpoint extensions
> #### First read
> New controller-action implementation.

Example: `http://localhost:8080/alerts/latest?sensorId=777d727e-6650-415f-85eb-9c9ca05f65c1`
Further requirements:
- Introduce `AlertController#getRecentAlertsBySensorId`
- URL: `/alerts/latest`
- Mandatory query parameter: `sensorId`
- 404 Not Found on an empty result

### 🔧 Task #7: Adding MeasureMentService functions
> #### First read
> If its place is given, only the function's body is missing.

Implement `SensorReading : MeasurementService.getAverageTemperature` as the method, which must return the average temperature reading over a specified time period.
- The parameter period is passed as a parameter or as a configuration property?
- Is there any need for some type validation?

---
## Optionals
### 🔧 Task #6: AlertService & Kafka integration
> #### First read
> Is there any way to generally centralize any entities?...

Centralized entity service again, now it is on `Alert`.
Triggering Kafka messages, consider using premade tools.

### 🔧 Task #8: Extend Measurement API
> #### First read
> New controller-action implementation.

Implement `List<Measurement> : MeasurementService.getMeasurementValuesBySensorType` with order on `timestamp`.
QA:
- Order direction is not specified.
- So what should be the returning type? Only a list of numbers hiding the order and the object heritage or can keep the object perspective?

### 🔧 Task #9: TODO1
> #### First read
> New controller-action implementation.

TODO

### 🔧 Task #10: TODO2
> #### First read
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
