package com.aldisued.iot.monitoring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "alerts")
@Entity
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;

  public Alert() {
  }

  public Alert(
      String message,
      LocalDateTime timestamp,
      Sensor sensor
  ) {
    this.message = message;
    this.timestamp = timestamp;
    this.sensor = sensor;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
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
