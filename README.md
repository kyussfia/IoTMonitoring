# IoT Monitoring
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

Before running the application or the integration tests, make sure the following tools are available:

- Java 26
- Docker Desktop or Docker Engine
- Git
- Maven
- An IDE such as IntelliJ IDEA is recommended

> The integration tests rely on PostgreSQL through Testcontainers, so Docker must be running before executing the test suite.

---

### Clone the Repository
```bash
bash git clone <repository-url> cd monitoring
```

---

### Running the Tests

The project contains task-oriented integration tests that validate the assignment requirements.

Maven:
```bash
bash ./mvnw test
```
On Windows:
```bash
bash mvnw.cmd test
```

You can also run individual task tests from the IDE, for example:

Maven:
```bash
bash ./mvnw -Dtest=Task4Tests test
```

---

### Database Setup

The preferred setup is using **Testcontainers**, which automatically starts a PostgreSQL container for the integration tests.

No manual database setup is required if Docker is available and running.

Alternatively, a local PostgreSQL database can be used. In that case, create the database and user as described in [`TASKS.md`](./TASKS.md), then activate the local Spring profile according to the assignment instructions.

---

### Running the Application

Maven:
```bash
bash ./mvnw spring-boot:run
```
The application should start on the configured Spring Boot port, typically on `http://localhost:8080`.

---

### Useful API Examples
Create a sensor:
```shell
bash curl -X POST [http://localhost:8080/sensors](http://localhost:8080/sensors)
-H "Content-Type: application/json"
-d '{ "name": "temperature-sensor-1", "type": "TEMPERATURE" }'
```

Retrieve the latest alert for a sensor:
```shell
bash curl "[http://localhost:8080/alerts/latest?sensorId=](http://localhost:8080/alerts/latest?sensorId=)<sensor-id>"
```

---

### Kafka

Some optional tasks involve publishing messages to Kafka.

Kafka is expected to be configured by the existing application setup. The implemented alert workflow publishes alert DTO messages to the `alerts` topic.

---

### Troubleshooting

#### Docker/Testcontainers API Version Issue

If Testcontainers fail because of a Docker API version mismatch, verify that Docker is running and that the Docker API version is compatible with the required Testcontainers setup.

In my local environment, the issue was related to Docker API compatibility and was solved by adjusting the Docker configuration to support the required API version.

#### Database Driver Error

If the application fails with an error similar to: `"Failed to determine a suitable driver class"`, check that the correct Spring profile is active and that the PostgreSQL datasource configuration is available for that profile.

#### Port Already in Use

If port `8080` is already occupied, change the configured server port or stop the process currently using it.

---

## 🧰 Tech Stack

- Java 26
- Spring Boot
- Spring MVC
- Spring Data JPA
- Jakarta Persistence API
- PostgreSQL
- Testcontainers
- Kafka
- JUnit 5

## 🧠 Thought Process

The implementation was approached task by task, following the order of the provided assignment.

The main goals were:

- keep the service layer focused on business logic
- keep persistence concerns in the entity/repository layer
- rely on Spring MVC validation and exception handling for API-level behavior
- use JPA constraints where data integrity must be guaranteed
- keep the implementation minimal, testable, and aligned with the prepared integration tests

Where possible, I preferred standard Spring Boot and JPA patterns over custom infrastructure.

### 🔧 Task #0: Use Testcontainers
- Installed and configured docker for Windows. 
- Updated WSL subsystem.
- Provided project persistence using docker: `docker-compose up`
- Still unable to run the test, due to docker version incompatibility.
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
- The "good enough" saving method (save or saveAndFlush) 

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
Triggering a Kafka message, consider using premade tools.

I implemented the usual repository interaction with additional Kafka message publishing on the `alerts` topic using the dto as payload.

### 🔧 Task #8: Extend Measurement API
> ***First touch*** \
> New controller-action implementation.

### 📌 Task #8: Extend Measurement API
```
📝 8.Task:
To enable further processing, the application must be able to retrieve all measurement values for a specific sensor type.

Your task:
Implement the MeasurementService.getMeasurementValuesBySensorType method.
The method must return a list of measurement values for a given sensor type, ordered by their timestamp.
```
> ***💬 First touch*** \
> New controller-action implementation.
> Implement `List<Measurement> : MeasurementService.getMeasurementValuesBySensorType` with order on `timestamp`.
> - Order direction is not specified.
> - So what should be the returning type? Only a list of numbers hiding the order and the object heritage or can keep the object perspective?

After checking the available environment, the solution should be made by calling a feature-specific repository method in the service.

---

## Selectable tasks
For a review situation <ins>I would pick the Tasks #9</ins> over the #10.
The reason is that I prefer to advance sequentially, and the #10 is a bit more opened in some aspects, which leads to more questions.

Maybe Task #9 is more focused on the current task.
But for the sake of completeness, I'll solve it anyway.
Firstly, because it is a good practice to discuss on the uncharted corners of the situation.

### 📌 Task #9: Deviation filtering
```
📝 9.Task:
Sensor data is often noisy and may contain outliers. To improve the accuracy of reports, a new calculation is being introduced.

Your task:
Implement the MeasurementCalculatorService.filterByAverageDeviation method.
The method input is a list of Double values representing sensor measurement.
The method must filter out values that deviate too much from the average.
The acceptable deviation is provided as a parameter (a double between 0.0 and 1.0), 
representing the allowed percentage of deviation from the average.
The method must validate the deviation parameter as,
if the parameter is outside the range [0.0, 1.0], the method should throw an IllegalArgumentException.
```
> ***💬 First touch*** \
> Implementation of a custom calculation method: Input parameters (samples and deviation) are declared as well as the integration requirements (filtering).
> I can immediately see two loops:
> 1. To determine the average value 
> 2. For the actual filtering.
> 
> I also expect to delegate the validations behind a type.
> Consider delegating the relevant mathematical abstractions behind that type too.
> Considering further optimizations.

After analyzing the description of deviation, I would like to formulate a more structured definition. 
Deviation can be interpreted as a structure associated with a target value and its surrounding neighborhood (both positive and negative).

I imagine one possible deviation as a pair of a target central value and a target radius, whose definition is based on the target value and the deviation coefficient.
The extent of this neighborhood is determined by the deviation coefficient, since the deviation tolerance is calculated relative to the target value as the center point.
Defining a clean `contains` method for the `Deviation` type is looking straightforward in terms of usage at the calling side of the solution.
On the other hand, initially taking care of inclusive- / exclusive-edge cases also seems handy.

One point that I disagree with is a hidden side effect, which is only discovered when I ran the concept test.
Namely, the case when the sampleset is empty. 
The provided test expects an empty list result on valid deviation parameter, but on an empty sampleset.
In my interpretation, it is an undefined behavior. 
Without samples, there is no average value. So the center of the deviation cannot be declared, the `Deviation` type can't tolerate that.

So I try to delegate the context specifics where they have been introduced:
The allowance of empty sets is derived from the filtering (service) approach.
The provided test cases expect deviation parameter validation independently of the cases related to empty samplesets.

When I switched their order, the test cases failed. (Which should be ok, because those are independent validation rules, none of each should be prioritized over the other.)

For example, in case of invalid deviation parameter and empty sampleset, the tests are expecting the deviation parameter violation, over the empty sampleset violation.

As a solution, I implemented the `Deviation` type and the `MeasurementService.filterByAverageDeviation` method reducing the complexity to a simple filtering operation on the supplied intput.
Both of the validations were implemented: the deviation type holds the validity of the deviation parameter, the filter method in the service handles the empty sampleset case.

### 📌 Task #10: Sliding window reduction
```
📝 10.Task:
Sensor data is often noisy and may contain outliers. To improve the accuracy of reports, a new calculation is being introduced.

Your task:
Implement the MeasurementCalculatorService.getMovingAverage method. The method input is a list of Double values representing sensor measurements.
windowSize is the size of the moving average window and must be a positive integer.
The method must validate the windowSize parameter: 
- if windowSize <= 0, throw an IllegalArgumentException, 
- windowSize is greater than the number of values throw an IllegalArgumentException.
The method should return a list of Double values,
where each element represents the moving average for a window of size windowSize.
```
> ***💬 First touch*** \
> Same use-case context as the previous task.
> Implementation of another custom calculation method over input samples. Customized iteration, which is basically compressing a window of samples into a new sample.
> Handle some restrictions over the window definition, so validation is also required. However, the definition felt a bit incomplete, so during the analysis I rely on the natural/intuitive interpretation.  
> 
> I imagine this, more of a sliding window concept over the data series. Which basically generates new compressed samples as the slider advances over the data series.
> Every element should be participated in the calculation. This hints a possible O(sample length) complexity.
>  
> Consider use of a mirrored solution, with delegating window requirements to a custom type.
> Consider further optimizations or alternatives with the help of premade tools/concepts. (reducer, collector, queue, etc.)

The initial validations are trivial, so the only question is where to allocate them.
The brute-force approach would do exactly what the description says: calculating the averages moving the slider from the beginning to the end.
That would cost O(window size * samples length). But with exploiting that the size of the windows can't exceed the size of the data series, it is ensured that the slider loop can be merged into the calculator loop.

The expectation on the result is something like:
```
avg(data[0], data[1], data[2])
avg(data[1], data[2], data[3])
avg(data[2], data[3], data[4])
```
Notice that the overlapping summaries can be defined recursively:
```
sum(0..2) = data[0] + data[1] + data[2]
sum(1..3) = previousSum - data[0] + data[3]
sum(2..4) = previousSum - data[1] + data[4]
```
We exchanged two addition operations to a subtraction and an addition. Which means zero impact on the cost.
Gaining the O(samples length) advantage of the sliding window approach while binding the summing to the "merged" loop and losing the ability to detach the window property of the logic.
Therefore, the (window-ness) related validations can be also placed here.
Two more optimization points I can see are:
1. To inverse the division to multiplication and leave out of the loop
2. To predetermine result container size.

With this native-close implementation, I'm also sacrificing alternatives at the Altar of Optimization, because none of the aforementioned tools would provide further performance improvements.

---

## 🔧 Possible Future Improvements

- The actual implementation of `Deviation` also shows further possible generalizations:
  - Define center other than the average value
  - Make `Deviation` generic and access polimorfism
- Generalize `Deviation` and `"MovingAverage"` under something like `MeasurementSeriesOperation` a `Function<List<Double>, List<Double>>` transformator function.
- Resolve vulnerability issues on the dependency tree.
- Add OpenAPI/Swagger documentation for the REST endpoints.
- Improve API error responses by returning a consistent error response body.
- Add more unit tests around service-layer validation and calculation logic.

---

## 📄 License

This repository was created as part of a coding assignment for an interview process.

The code is provided for portfolio and reference purposes. Please do not reuse it as a direct submission for the same or similar assignment.

---

_Submitted for ALDI interview – kyussfia - 2026_
