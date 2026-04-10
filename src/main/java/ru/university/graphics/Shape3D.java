package ru.university.graphics;

import java.util.ArrayList;
import java.util.List;

public class Shape3D {
    private final List<Vector4> vertices;
    private final List<int[]> edges; // Индексы вершин, которые нужно соединить

    public Shape3D(List<Vector4> vertices, List<int[]> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public static Shape3D createScene() {
        List<Vector4> verts = new ArrayList<>();
        List<int[]> edgs = new ArrayList<>();


        verts.add(new Vector4(0, 0, 0)); verts.add(new Vector4(2, 0, 0));
        verts.add(new Vector4(2, 1, 0)); verts.add(new Vector4(0, 1, 0));
        verts.add(new Vector4(0, 0, 1)); verts.add(new Vector4(2, 0, 1));
        verts.add(new Vector4(2, 1, 1)); verts.add(new Vector4(0, 1, 1));

        int[][] pEdges = {{0,1},{1,2},{2,3},{3,0}, {4,5},{5,6},{6,7},{7,4}, {0,4},{1,5},{2,6},{3,7}};
        for(int[] e : pEdges) edgs.add(e);


        verts.add(new Vector4(0, 1, 0)); verts.add(new Vector4(2, 1, 0));
        verts.add(new Vector4(2, 1, 1)); verts.add(new Vector4(0, 1, 1));
        verts.add(new Vector4(1, 3, 0.5)); // Вершина пирамиды

        int[][] pyrEdges = {{8,12},{9,12},{10,12},{11,12}};
        for(int[] e : pyrEdges) edgs.add(e);

        return new Shape3D(verts, edgs);
    }

    public Shape3D applyTransform(Matrix4 m) {
        List<Vector4> newVerts = vertices.stream().map(m::transform).toList();
        return new Shape3D(newVerts, this.edges);
    }

    public List<Vector4> getVertices() { return vertices; }
    public List<int[]> getEdges() { return edges; }
}