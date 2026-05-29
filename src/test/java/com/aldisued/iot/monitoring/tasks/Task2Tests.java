package com.aldisued.iot.monitoring.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import com.aldisued.iot.monitoring.IntegrationTestBase;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "/sql/task-2-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class Task2Tests extends IntegrationTestBase {

  private static final UUID TEMPERATURE_SENSOR_ID = UUID.fromString("e3242ea2-0514-46d3-aad8-b2012980c41c");
  private static final UUID HUMIDITY_SENSOR_ID = UUID.fromString("ac723c77-955f-469d-9d6a-d56bac39c202");

  private Sensor temparatureSensor;
  private Sensor humiditySensor;

  @Autowired
  private SensorRepository sensorRepository;

  @BeforeEach
  public void setup() {
    this.temparatureSensor = this.sensorRepository.findById(TEMPERATURE_SENSOR_ID).orElseThrow();
    this.humiditySensor = this.sensorRepository.findById(HUMIDITY_SENSOR_ID).orElseThrow();
  }

  @Test
  @Transactional
  public void verifySensorReadingsInSensorEntity() {
    assertThat(List.of(1L, 2L, 3L)).hasSameElementsAs(this.temparatureSensor.getSensorReadings().stream().map(SensorReading::getId).toList());
    assertThat(List.of(4L, 5L)).hasSameElementsAs(this.humiditySensor.getSensorReadings().stream().map(SensorReading::getId).toList());
  }

  @Test
  @Transactional
  public void verifyAlertsInSensorEntity() {
    assertThat(List.of(1L)).hasSameElementsAs(this.temparatureSensor.getAlerts().stream().map(Alert::getId).toList());

    assertThat(List.of()).hasSameElementsAs(this.humiditySensor.getAlerts().stream().map(Alert::getId).toList());
  }

  @Test
  @Transactional
  public void verifySensorInAlertEntity() {
    Assertions.assertTrue(this.temparatureSensor.getAlerts().stream().allMatch(alert -> alert.getSensor().equals(this.temparatureSensor)));
  }

  @Test
  @Transactional
  public void verifySensorInSensorReadingEntity() {
    Assertions.assertTrue(this.temparatureSensor.getSensorReadings().stream().allMatch(sensorReading -> sensorReading.getSensor().equals(this.temparatureSensor)));
  }
}
