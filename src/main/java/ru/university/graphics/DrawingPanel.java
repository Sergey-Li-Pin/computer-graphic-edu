package ru.university.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

public class DrawingPanel extends JPanel {
    private final Shape originalShape;
    private final List<Shape> intermediateShapes;
    private final Shape finalShape;

    // Параметры "камеры" для 2D
    private double scale = 50.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private int lastMouseX, lastMouseY;

    // Переменные анимации
    private Timer timer;
    private double animProgress = 1.0;
    private boolean isAnimating = false;

    public DrawingPanel(Shape original, List<Shape> intermediates, Shape finalShape) {
        this.originalShape = original;
        this.intermediateShapes = intermediates;
        this.finalShape = finalShape;

        setBackground(new Color(250, 250, 250));
        setLayout(new BorderLayout());

        // --- ДОБАВЛЯЕМ КНОПКУ АНИМАЦИИ ---
        JButton animButton = new JButton("▶ Запустить анимацию 2D");
        animButton.setFocusPainted(false);
        animButton.setFont(new Font("Arial", Font.BOLD, 14));
        animButton.addActionListener(e -> startAnimation());

        JPanel uiPanel = new JPanel();
        uiPanel.setOpaque(false);
        uiPanel.add(animButton);
        add(uiPanel, BorderLayout.SOUTH);

        // --- УПРАВЛЕНИЕ МЫШЬЮ (Панорама и Зум) ---
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX += e.getX() - lastMouseX;
                offsetY += e.getY() - lastMouseY;
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

        timer = new Timer(16, e -> {
            animProgress += 0.01;
            if (animProgress >= 1.0) {
                animProgress = 1.0;
                timer.stop();
                isAnimating = false;
            }
            repaint();
        });
        timer.start();
    }

    private Shape getAnimatedShape(double t) {
        // Базовые матрицы для уже полностью пройденных этапов
        Matrix3 mRefFull = Matrix3.reflectionX();
        Matrix3 mTransFull = Matrix3.translation(4, -3);
        Matrix3 mRotFull = Matrix3.rotation(225);

        // Разбиваем общее время t (от 0.0 до 1.0) на 4 последовательных этапа (0, 1, 2, 3)
        int stage = (int) (t * 4);
        // Вычисляем локальный прогресс внутри текущего этапа (от 0.0 до 1.0)
        double localT = (t * 4) - stage;

        // Защита от выхода за пределы массива при t = 1.0
        if (t >= 1.0) {
            stage = 3;
            localT = 1.0;
        }

        Matrix3 currentMatrix;

        // Кусочная логика анимации
        if (stage == 0) {
            // Только отражение
            currentMatrix = Matrix3.scale(1.0, 1.0 - 2.0 * localT);

        } else if (stage == 1) {
            // Отражение завершено -> перенос.
            Matrix3 mTransAnim = Matrix3.translation(4 * localT, -3 * localT);
            currentMatrix = mTransAnim.multiply(mRefFull);

        } else if (stage == 2) {
            // Отражение и перенос завершены -> поворот.
            Matrix3 mRotAnim = Matrix3.rotation(225 * localT);
            currentMatrix = mRotAnim.multiply(mTransFull).multiply(mRefFull);

        } else {
            // Отражение, перенос, поворот завершены -> масштаб.
            Matrix3 mScaleAnim = Matrix3.scale(1.0 - 0.1 * localT, 1.0 + 0.1 * localT);
            currentMatrix = mScaleAnim.multiply(mRotFull).multiply(mTransFull).multiply(mRefFull);
        }

        // Применяем вычисленную матрицу к исходному объекту
        return originalShape.applyTransform(currentMatrix);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Центр с учетом сдвига мышью
        int cx = getWidth() / 2 + offsetX;
        int cy = getHeight() / 2 + offsetY;

        drawGrid(g2d, getWidth(), getHeight(), cx, cy);
        drawAxes(g2d, cx, cy);

        // 1. Промежуточные состояния
        for (Shape s : intermediateShapes) {
            drawShape(g2d, s, cx, cy, Color.GRAY, true, 0.15f);
        }

        // 2. Исходный объект
        drawShape(g2d, originalShape, cx, cy, new Color(70, 70, 70, 180), true, 1.0f);

        // 3. Динамический анимируемый объект
        Shape currentShape = (animProgress >= 1.0 && !isAnimating) ? finalShape : getAnimatedShape(animProgress);
        drawShape(g2d, currentShape, cx, cy, Color.RED, false, 1.0f);

        // Инструкция
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Управление: ЛКМ - перемещение | Колесико - масштаб", 20, 30);
    }

    private void drawGrid(Graphics2D g2d, int w, int h, int cx, int cy) {
        g2d.setColor(new Color(230, 230, 230));
        int s = (int) scale;
        // Отрисовка сетки с учетом смещения
        for (int x = cx % s; x < w; x += s) g2d.drawLine(x, 0, x, h);
        for (int y = cy % s; y < h; y += s) g2d.drawLine(0, y, w, y);
    }

    private void drawAxes(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, cy, 10000, cy); // Ось X
        g2d.drawLine(cx, 0, cx, 10000); // Ось Y
    }

    private void drawShape(Graphics2D g2d, Shape shape, int cx, int cy, Color color, boolean isDashed, float alpha) {
        g2d.setColor(color);
        if (isDashed) {
            float[] dash = {5.0f};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        } else {
            g2d.setStroke(new BasicStroke(2));
        }

        List<Vector3> verts = shape.getVertices();
        int n = verts.size();
        int[] xPts = new int[n];
        int[] yPts = new int[n];

        for (int i = 0; i < n; i++) {
            Vector3 v = verts.get(i);
            xPts[i] = cx + (int) (v.x() * scale);
            yPts[i] = cy - (int) (v.y() * scale); // Инверсия Y для экрана
        }

        g2d.drawPolygon(xPts, yPts, n);

        // Заливка с учетом переданного Alpha-канала
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.2f));
        g2d.fillPolygon(xPts, yPts, n);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}