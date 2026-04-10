package ru.university.graphics;

public class Matrix4 {
    private final double[][] data;

    public Matrix4(double[][] data) {
        this.data = data;
    }

    public static Matrix4 translation(double tx, double ty, double tz) {
        return new Matrix4(new double[][]{
                {1, 0, 0, tx}, {0, 1, 0, ty}, {0, 0, 1, tz}, {0, 0, 0, 1}
        });
    }

    public static Matrix4 scale(double sx, double sy, double sz) {
        return new Matrix4(new double[][]{
                {sx, 0, 0, 0}, {0, sy, 0, 0}, {0, 0, sz, 0}, {0, 0, 0, 1}
        });
    }

    public static Matrix4 rotationZ(double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        return new Matrix4(new double[][]{
                {Math.cos(rad), -Math.sin(rad), 0, 0},
                {Math.sin(rad), Math.cos(rad), 0, 0},
                {0, 0, 1, 0}, {0, 0, 0, 1}
        });
    }

    public static Matrix4 rotationY(double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        return new Matrix4(new double[][]{
                {Math.cos(rad), 0, Math.sin(rad), 0},
                {0, 1, 0, 0},
                {-Math.sin(rad), 0, Math.cos(rad), 0},
                {0, 0, 0, 1}
        });
    }

    public Matrix4 multiply(Matrix4 other) {
        double[][] res = new double[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    res[i][j] += this.data[i][k] * other.data[k][j];
        return new Matrix4(res);
    }

    public Vector4 transform(Vector4 v) {
        return new Vector4(
                data[0][0]*v.x() + data[0][1]*v.y() + data[0][2]*v.z() + data[0][3]*v.w(),
                data[1][0]*v.x() + data[1][1]*v.y() + data[1][2]*v.z() + data[1][3]*v.w(),
                data[2][0]*v.x() + data[2][1]*v.y() + data[2][2]*v.z() + data[2][3]*v.w(),
                data[3][0]*v.x() + data[3][1]*v.y() + data[3][2]*v.z() + data[3][3]*v.w()
        );
    }

    public double determinant() {
        return det4x4(this.data);
    }

    private double det4x4(double[][] m) {
        double res = 0;
        for (int i = 0; i < 4; i++) {
            res += Math.pow(-1, i) * m[0][i] * det3x3(getMinor(m, 0, i));
        }
        return res;
    }

    private double det3x3(double[][] m) {
        return m[0][0]*(m[1][1]*m[2][2] - m[1][2]*m[2][1]) -
                m[0][1]*(m[1][0]*m[2][2] - m[1][2]*m[2][0]) +
                m[0][2]*(m[1][0]*m[2][1] - m[1][1]*m[2][0]);
    }

    private double[][] getMinor(double[][] m, int row, int col) {
        double[][] minor = new double[3][3];
        int r = 0;
        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                minor[r][c] = m[i][j];
                c++;
            }
            r++;
        }
        return minor;
    }

    public static Matrix4 rotationX(double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        return new Matrix4(new double[][]{
                {1, 0, 0, 0},
                {0, Math.cos(rad), -Math.sin(rad), 0},
                {0, Math.sin(rad), Math.cos(rad), 0},
                {0, 0, 0, 1}
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : data) {
            sb.append("| ");
            for (double val : row) sb.append(String.format("%8.2f ", val));
            sb.append(" |\n");
        }
        return sb.toString();
    }
}