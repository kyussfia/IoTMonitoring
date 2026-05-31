package com.kyussfia.util.structure;

public record Deviation(double tolerance, double center) {

    public Deviation {
        if (!Double.isFinite(tolerance) || tolerance < 0.0 || tolerance > 1.0) {
            throw new IllegalArgumentException("Tolerance of deviation must be between 0.0 and 1.0.");
        }
    }

    public double radius() {
        return Math.abs(this.tolerance) * this.center;
    }

    public double lowerBound() {
        return this.center - this.radius();
    }

    public double upperBound() {
        return this.center + this.radius();
    }

    public boolean contains(double value) {
        return this.contains(value, true, true);
    }

    public boolean contains(double value, boolean lowerInclusive, boolean upperInclusive) {
        return value > this.lowerBound()
                   && value < this.upperBound()
                   && (lowerInclusive || Double.compare(this.lowerBound(), value) != 0)
                   && (upperInclusive || Double.compare(this.upperBound(), value) != 0);
    }
}
