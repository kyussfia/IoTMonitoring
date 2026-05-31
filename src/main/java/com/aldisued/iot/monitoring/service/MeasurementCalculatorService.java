package com.aldisued.iot.monitoring.service;

import com.kyussfia.util.structure.Deviation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class MeasurementCalculatorService {

  @SuppressWarnings("MethodMayBeStatic")
  public List<Double> filterByAverageDeviation(@NonNull List<Double> values, @NonNull Double deviation) {
      final OptionalDouble optAvg = values.stream().mapToDouble(Double::doubleValue).average();

      final Deviation dev = new Deviation(deviation, optAvg.orElse(0.0));
      return optAvg.isEmpty()
                 ? Collections.emptyList()
                 : values.stream().filter(dev::contains).toList();
  }

  @SuppressWarnings("MethodMayBeStatic")
  public List<Double> getMovingAverage(@NonNull List<Double> data, int windowSize) {
    if (windowSize <= 0) {
      throw new IllegalArgumentException("Window size must be positive");
    }

    if (windowSize > data.size()) {
      throw new IllegalArgumentException("Window size must not exceed data size");
    }

    final List<Double> result = new ArrayList<>(data.size() - windowSize + 1);
    final double inverseMultiplier = 1.0 / windowSize;

    double slidingSum = 0.0;

    for (int i = 0; i < data.size(); i++) {
      slidingSum += data.get(i);

      if (i >= windowSize) {
        slidingSum -= data.get(i - windowSize);
      }

      if (i >= windowSize - 1) {
        result.add(slidingSum * inverseMultiplier);
      }
    }

    return result;
  }
}
