package ru.university.graphics;

import java.util.List;
import java.util.stream.Collectors;

public class Shape {
    private final List<Vector3> vertices;

    public Shape(List<Vector3> vertices) {
        this.vertices = vertices;
    }

    public Shape applyTransform(Matrix3 matrix) {
        List<Vector3> transformed = vertices.stream()
                .map(matrix::transform)
                .collect(Collectors.toList());
        return new Shape(transformed);
    }

    public List<Vector3> getVertices() {
        return vertices;
    }
}