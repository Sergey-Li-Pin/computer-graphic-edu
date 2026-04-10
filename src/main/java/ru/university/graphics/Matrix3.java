package ru.university.graphics;

public class Matrix3 {
    private final double[][] data;

    public Matrix3(double[][] data) {
        if (data.length != 3 || data[0].length != 3) {
            throw new IllegalArgumentException("Матрица должна быть 3x3");
        }
        this.data = data;
    }


    // Отражение относительно оси OX
    public static Matrix3 reflectionX() {
        return new Matrix3(new double[][]{
                {1,  0, 0},
                {0, -1, 0},
                {0,  0, 1}
        });
    }

    // Перенос на вектор (tx, ty)
    public static Matrix3 translation(double tx, double ty) {
        return new Matrix3(new double[][]{
                {1, 0, tx},
                {0, 1, ty},
                {0, 0, 1}
        });
    }

    // Поворот на угол в градусах (вокруг начала координат
    public static Matrix3 rotation(double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Matrix3(new double[][]{
                {cos, -sin, 0},
                {sin,  cos, 0},
                {0,    0,   1}
        });
    }

    // Масштабирование (sx, sy)
    public static Matrix3 scale(double sx, double sy) {
        return new Matrix3(new double[][]{
                {sx, 0, 0},
                {0, sy, 0},
                {0, 0, 1}
        });
    }

    public Matrix3 multiply(Matrix3 other) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += this.data[i][k] * other.data[k][j];
                }
            }
        }
        return new Matrix3(result);
    }

    public Vector3 transform(Vector3 v) {
        double nx = data[0][0] * v.x() + data[0][1] * v.y() + data[0][2] * v.w();
        double ny = data[1][0] * v.x() + data[1][1] * v.y() + data[1][2] * v.w();
        double nw = data[2][0] * v.x() + data[2][1] * v.y() + data[2][2] * v.w();
        return new Vector3(nx, ny, nw);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : data) {
            sb.append("| ");
            for (double val : row) {
                sb.append(String.format("%8.3f ", val));
            }
            sb.append(" |\n");
        }
        return sb.toString();
    }
}