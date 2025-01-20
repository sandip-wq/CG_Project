package projekt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ObjReader {

    private float[] vertices;
    private float[] normals;
    private float[] texCoords;

    public ObjReader(String path) {
        readFile(path);
    }

    public float[] getVertices()    { return vertices; }
    public float[] getNormals()     { return normals; }
    public float[] getTexCoords()   { return texCoords; }

    private void readFile(String path) {
        ArrayList<float[]> vList  = new ArrayList<>();
        ArrayList<float[]> vnList = new ArrayList<>();
        ArrayList<float[]> vtList = new ArrayList<>();
        ArrayList<String> faces   = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("v ")) {
                    // position
                    String[] tokens = line.split("\\s+");
                    vList.add(new float[]{ 
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    });
                } else if (line.startsWith("vn ")) {
                    // normal
                    String[] tokens = line.split("\\s+");
                    vnList.add(new float[]{
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    });
                } else if (line.startsWith("vt ")) {
                    // texture
                    String[] tokens = line.split("\\s+");
                    vtList.add(new float[]{
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    });
                } else if (line.startsWith("f ")) {
                    // face, e.g. "f 1/1/1 2/2/2 3/3/3"
                    String[] tokens = line.split("\\s+");
                    // tokens[0] = "f"
                    faces.add(tokens[1]);
                    faces.add(tokens[2]);
                    faces.add(tokens[3]);
                    // If the OBJ uses quads, you might have 4 vertices -> adapt if needed
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Expand each face reference "vIndex/tIndex/nIndex" into final arrays
        ArrayList<Float> outVerts = new ArrayList<>();
        ArrayList<Float> outNorms = new ArrayList<>();
        ArrayList<Float> outTex   = new ArrayList<>();

        for (String face : faces) {
            // "vIndex/vtIndex/vnIndex"
            String[] parts = face.split("/");
            int vIdx  = Integer.parseInt(parts[0]) - 1; 
            int vtIdx = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) - 1 : -1;
            int vnIdx = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) - 1 : -1;

            // positions
            float[] vp = vList.get(vIdx);
            outVerts.add(vp[0]);
            outVerts.add(vp[1]);
            outVerts.add(vp[2]);

            // texture
            if (vtIdx >= 0 && vtIdx < vtList.size()) {
                float[] vtp = vtList.get(vtIdx);
                outTex.add(vtp[0]);
                outTex.add(vtp[1]);
            } else {
                // no texture => push default
                outTex.add(0f);
                outTex.add(0f);
            }

            // normal
            if (vnIdx >= 0 && vnIdx < vnList.size()) {
                float[] vnp = vnList.get(vnIdx);
                outNorms.add(vnp[0]);
                outNorms.add(vnp[1]);
                outNorms.add(vnp[2]);
            } else {
                outNorms.add(0f);
                outNorms.add(0f);
                outNorms.add(0f);
            }
        }

        vertices = new float[outVerts.size()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = outVerts.get(i);
        }
        normals = new float[outNorms.size()];
        for (int i = 0; i < normals.length; i++) {
            normals[i] = outNorms.get(i);
        }
        texCoords = new float[outTex.size()];
        for (int i = 0; i < texCoords.length; i++) {
            texCoords[i] = outTex.get(i);
        }
    }
}
