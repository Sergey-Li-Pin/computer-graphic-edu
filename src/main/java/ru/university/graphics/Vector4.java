package ru.university.graphics;

public record Vector4(double x, double y, double z, double w) {
    public Vector4(double x, double y, double z) {
        this(x, y, z, 1.0);
    }
}