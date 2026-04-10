package ru.university.graphics;

public record Vector3(double x, double y, double w) {
    public Vector3(double x, double y) {
        this(x, y, 1.0);
    }
}