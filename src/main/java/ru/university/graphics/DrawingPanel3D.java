package ru.university.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

public class DrawingPanel3D extends JPanel {
    private final Shape3D originalShape;

    // Камера и масштаб
    private double viewRotX = 15;
    private double viewRotY = 30;
    private int lastMouseX, lastMouseY;
    private double scale = 60.0;

    // --- ПЕРЕМЕННЫЕ АНИМАЦИИ ---
    private Timer timer;
    private double animProgress = 1.0; // От 0.0 (старт) до 1.0 (финиш)
    private boolean isAnimating = false;

    public DrawingPanel3D(Shape3D original, Shape3D transformed) {
        this.originalShape = original;
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout()); // Устанавливаем Layout для UI элементов

        // --- ДОБАВЛЯЕМ КНОПКУ АНИМАЦИИ ---
        JButton animButton = new JButton("▶ Запустить анимацию");
        animButton.setFocusPainted(false);
        animButton.setFont(new Font("Arial", Font.BOLD, 14));
        animButton.addActionListener(e -> startAnimation());

        JPanel uiPanel = new JPanel();
        uiPanel.setOpaque(false);
        uiPanel.add(animButton);
        add(uiPanel, BorderLayout.SOUTH);

        // Обработчики мыши (камера)
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                viewRotY += (e.getX() - lastMouseX) * 0.5;
                viewRotX += (e.getY() - lastMouseY) * 0.5;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scale -= e.getWheelRotation() * 5;
                if (scale < 10) scale = 10;
                repaint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    // --- ЛОГИКА АНИМАЦИИ ---
    private void startAnimation() {
        if (isAnimating) return;
        isAnimating = true;
        animProgress = 0.0;

        timer = new Timer(16, e -> { // ~60 FPS (1000ms / 60)
            animProgress += 0.01; // Шаг анимации
            if (animProgress >= 1.0) {
                animProgress = 1.0;
                timer.stop();
                isAnimating = false;
            }
            repaint();
        });
        timer.start();
    }

    // Динамический расчет текущей формы на основе прогресса 't'
    private Shape3D getAnimatedShape(double t) {
        // Интерполяция параметров
        // Перенос: (-2, 1, 3)
        Matrix4 mTrans = Matrix4.translation(-2 * t, 1 * t, 3 * t);
        // Вращение Z: 270
        Matrix4 mRotZ = Matrix4.rotationZ(270 * t);
        // Масштаб: (1.3, 0.7, 1) -> от 1.0
        Matrix4 mScale = Matrix4.scale(1.0 + 0.3 * t, 1.0 - 0.3 * t, 1.0);
        // Вращение Y: 90
        Matrix4 mRotY = Matrix4.rotationY(90 * t);

        Matrix4 mResult = mRotY.multiply(mScale).multiply(mRotZ).multiply(mTrans);
        return originalShape.applyTransform(mResult);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Matrix4 viewMatrix = Matrix4.rotationX(viewRotX).multiply(Matrix4.rotationY(viewRotY));
        drawAxes(g2d, cx, cy, viewMatrix);

        // Исходный объект
        drawShape(g2d, originalShape, cx, cy, new Color(70, 70, 70, 180), viewMatrix, true);

        // --- ОТРИСОВКА ДИНАМИЧЕСКОГО ОБЪЕКТА ---
        Shape3D currentShape = getAnimatedShape(animProgress);
        drawShape(g2d, currentShape, cx, cy, Color.RED, viewMatrix, false);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Управление: ЛКМ - камера | Колесико - масштаб", 20, 30);
    }

    private void drawAxes(Graphics2D g2d, int cx, int cy, Matrix4 viewMatrix) {
        g2d.setStroke(new BasicStroke(2));
        double len = 5.0;
        Vector4 origin = viewMatrix.transform(new Vector4(0, 0, 0));
        Vector4 xAxis = viewMatrix.transform(new Vector4(len, 0, 0));
        Vector4 yAxis = viewMatrix.transform(new Vector4(0, len, 0));
        Vector4 zAxis = viewMatrix.transform(new Vector4(0, 0, len));

        g2d.setColor(Color.RED); drawLine(g2d, origin, xAxis, cx, cy);
        g2d.drawString("X", cx + (int)(xAxis.x() * scale), cy - (int)(xAxis.y() * scale));
        g2d.setColor(new Color(0, 150, 0)); drawLine(g2d, origin, yAxis, cx, cy);
        g2d.drawString("Y", cx + (int)(yAxis.x() * scale), cy - (int)(yAxis.y() * scale));
        g2d.setColor(Color.BLUE); drawLine(g2d, origin, zAxis, cx, cy);
        g2d.drawString("Z", cx + (int)(zAxis.x() * scale), cy - (int)(zAxis.y() * scale));
    }

    private void drawShape(Graphics2D g2d, Shape3D shape, int cx, int cy, Color color, Matrix4 viewMatrix, boolean isDashed) {
        g2d.setColor(color);
        if (isDashed) {
            float[] dash = {5.0f};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(2));
        }

        List<Vector4> verts = shape.getVertices();
        Vector4[] projected = new Vector4[verts.size()];
        for (int i = 0; i < verts.size(); i++) {
            projected[i] = viewMatrix.transform(verts.get(i));
        }
        for (int[] edge : shape.getEdges()) {
            drawLine(g2d, projected[edge[0]], projected[edge[1]], cx, cy);
        }
    }

    private void drawLine(Graphics2D g2d, Vector4 v1, Vector4 v2, int cx, int cy) {
        int x1 = cx + (int) (v1.x() * scale);
        int y1 = cy - (int) (v1.y() * scale);
        int x2 = cx + (int) (v2.x() * scale);
        int y2 = cy - (int) (v2.y() * scale);
        g2d.drawLine(x1, y1, x2, y2);
    }
}