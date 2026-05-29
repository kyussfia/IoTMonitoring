package com.aldisued.iot.monitoring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "sensor_readings")
@Entity
public class SensorReading {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Double value;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;

  public SensorReading() {
  }

  public SensorReading(
      Double value,
      LocalDateTime timestamp,
      Sensor sensor
  ) {
    this.value = value;
    this.timestamp = timestamp;
    this.sensor = sensor;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getValue() {
    return this.value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Sensor getSensor() {
    return this.sensor;
  }

  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
}
