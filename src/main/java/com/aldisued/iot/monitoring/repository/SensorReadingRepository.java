package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    @Query("""
        select avg(reading.value)
        from SensorReading reading
        where reading.sensor.type = :sensorType
            and reading.timestamp between :from and :to
    """)
    Double findAverageValueBySensorTypeAndTimestampBetween(SensorType sensorType, LocalDateTime from, LocalDateTime to);

    @Query("""
        select reading.value
        from SensorReading reading
        where reading.sensor.type = :sensorType
            and reading.timestamp between :from and :to
        order by reading.timestamp
    """)
    List<Double> findValuesBySensorTypeAndTimestampBetweenOrderByTimestamp(SensorType sensorType, LocalDateTime from, LocalDateTime to);
}
