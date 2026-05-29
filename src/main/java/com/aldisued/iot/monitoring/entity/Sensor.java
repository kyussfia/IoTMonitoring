package com.aldisued.iot.monitoring.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(name = "sensors")
@Entity
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SensorType type;

  @OneToMany(mappedBy = "sensor")
  private List<SensorReading> sensorReadings = new ArrayList<>();

  @OneToMany(mappedBy = "sensor")
  private List<Alert> alerts = new ArrayList<>();

  public Sensor() {}

  public Sensor(String name, SensorType type) {
    this.name = name;
    this.type = type;
  }

  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SensorType getType() {
    return this.type;
  }

  public void setType(SensorType type) {
    this.type = type;
  }

  public List<Alert> getAlerts() {
    return this.alerts;
  }

  public void setAlerts(List<Alert> alerts) {
    this.alerts = alerts;
  }

  public List<SensorReading> getSensorReadings() {
    return this.sensorReadings;
  }

  public void setSensorReadings(List<SensorReading> sensorReadings) {
    this.sensorReadings = sensorReadings;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sensor sensor = (Sensor) o;
    return Objects.equals(this.id, sensor.id)
               && Objects.equals(this.name, sensor.name)
               && this.type == sensor.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.type);
  }
}
