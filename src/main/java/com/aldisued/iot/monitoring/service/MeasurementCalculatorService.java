package com.aldisued.iot.monitoring.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class MeasurementCalculatorService {

  public record AverageDeviation(double value) {
    public AverageDeviation {
      if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
        throw new IllegalArgumentException("AverageDeviation must be between 0.0 and 1.0");
      }
    }

    public double lowerBound(double average) {
      return average - Math.abs(average) * this.value;
    }

    public double upperBound(double average) {
      return average + Math.abs(average) * this.value;
    }

    public boolean isWithinBounds(double value, double average) {
      return value >= this.lowerBound(average) && value <= this.upperBound(average);
    }
  }

  @SuppressWarnings("MethodMayBeStatic")
  public List<Double> filterByAverageDeviation(@NonNull List<Double> values, @NonNull Double deviation) {
    return filterByAverageDeviation(values, new AverageDeviation(deviation));
  }

  public static List<Double> filterByAverageDeviation(@NonNull List<Double> values, @NonNull AverageDeviation deviation) {
    final double average = values
                         .stream()
                         .mapToDouble(Double::doubleValue)
                         .average()
                         .orElseThrow();

    return values
               .stream()
               .filter(v -> deviation.isWithinBounds(v, average))
               .toList();
  }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    // TODO: Task 10
    return List.of();
  }

}
