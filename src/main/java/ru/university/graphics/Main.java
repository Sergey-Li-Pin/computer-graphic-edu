package ru.university.graphics;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Лабораторная работа №1 ===");
        System.out.println("1. Запустить 2D Преобразования");
        System.out.println("2. Запустить 3D Преобразования");
        System.out.print("Выберите пункт меню: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> run2D();
            case 2 -> run3D();
            default -> System.out.println("Неверный выбор. Перезапустите программу.");
        }
    }

    private static void run2D() {

        Shape original = new Shape(List.of(
                new Vector3(0, 0), new Vector3(2, -1), new Vector3(1, 1),
                new Vector3(2.5, 1.5), new Vector3(0, 4), new Vector3(-2.5, 1.5),
                new Vector3(-1, 1), new Vector3(-2, -1)
        ));
        Shape currentShape = original;
        List<Shape> intermediates = new ArrayList<>();

        Matrix3 mRef = Matrix3.reflectionX();
        Matrix3 mTrans = Matrix3.translation(4, -3);
        Matrix3 mRot = Matrix3.rotation(225);
        Matrix3 mScale = Matrix3.scale(0.9, 1.1);

        System.out.println("\n=== 2D МАТРИЦЫ ПРЕОБРАЗОВАНИЯ ===");
        System.out.println("1. Отражение (OX):\n" + mRef);
        System.out.println("2. Перенос (4, -3):\n" + mTrans);
        System.out.println("3. Поворот (225°):\n" + mRot);
        System.out.println("4. Масштаб (0.9, 1.1):\n" + mScale);

        Matrix3 mResult = mScale.multiply(mRot).multiply(mTrans).multiply(mRef);
        System.out.println("ИТОГОВАЯ 2D МАТРИЦА (M = S * R * T * Ref):\n" + mResult);


        currentShape = currentShape.applyTransform(mRef); intermediates.add(currentShape);
        currentShape = currentShape.applyTransform(mTrans); intermediates.add(currentShape);
        currentShape = currentShape.applyTransform(mRot); intermediates.add(currentShape);
        Shape finalShape = currentShape.applyTransform(mScale);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ЛР1: 2D Преобразования");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new DrawingPanel(original, intermediates, finalShape));
            frame.setSize(900, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void run3D() {
        Shape3D original = Shape3D.createScene();


        Matrix4 mTrans = Matrix4.translation(-2, 1, 3);
        Matrix4 mRotZ = Matrix4.rotationZ(270);
        Matrix4 mScale = Matrix4.scale(1.3, 0.7, 1);
        Matrix4 mRotY = Matrix4.rotationY(90);

        System.out.println("\n=== 3D МАТРИЦЫ ПРЕОБРАЗОВАНИЯ ===");
        System.out.println("1. Перенос (-2, 1, 3):\n" + mTrans);
        System.out.println("2. Поворот Z (270°):\n" + mRotZ);
        System.out.println("3. Масштаб (1.3, 0.7, 1):\n" + mScale);
        System.out.println("4. Поворот Y (90°):\n" + mRotY);


        Matrix4 mResult = mRotY.multiply(mScale).multiply(mRotZ).multiply(mTrans);

        System.out.println("ИТОГОВАЯ 3D МАТРИЦА (M = Ry * S * Rz * T):\n" + mResult);
        System.out.println("Детерминант итоговой матрицы: " + String.format("%.3f", mResult.determinant()));

        Shape3D transformed = original.applyTransform(mResult);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ЛР1: 3D Каркасная Сцена");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new DrawingPanel3D(original, transformed));
            frame.setSize(900, 900);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}