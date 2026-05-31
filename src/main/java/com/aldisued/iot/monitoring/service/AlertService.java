package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository, KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Transactional
  public Alert saveAlert(AlertDto alertDto) {
    Alert alert = this.alertRepository
                      .save(new Alert(
                          alertDto.message(),
                          alertDto.timestamp(),
                          this.sensorRepository.findById(alertDto.sensorId()).orElseThrow()
                      ));

    this.kafkaTemplate.send("alerts", alertDto);

    return alert;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    return this.alertRepository
               .findFirstBySensor_IdOrderByTimestampDesc(sensorId)
               .map(alert -> new AlertDto(alert.getSensor().getId(), alert.getMessage(), alert.getTimestamp()))
               .orElseThrow(() -> new EntityNotFoundException("Alert not found"));
  }
}
