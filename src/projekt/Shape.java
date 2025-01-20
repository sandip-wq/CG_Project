package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Shape {

    private int vao;
    private int vertexCount;

    // Create a shape from arrays. If you have no colors, pass null for 'colors', etc.
    public Shape(float[] positions, float[] colors, float[] normals, float[] texCoords) {
        vertexCount = positions.length / 3; // each vertex has 3 components (x,y,z)

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // 1) Positions -> attribute location 0
        int vboPos = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboPos);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // 2) Colors -> attribute location 1 (optional)
        if (colors != null) {
            int vboColor = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboColor);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        }

        // 3) Normals -> attribute location 2 (optional)
        if (normals != null) {
            int vboNormal = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboNormal);
            glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        }

        // 4) Texture coordinates -> attribute location 3 (optional)
        if (texCoords != null) {
            int vboTex = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboTex);
            glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);
        }

        // unbind
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // Draw with glDrawArrays
    public void render() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    }
}
