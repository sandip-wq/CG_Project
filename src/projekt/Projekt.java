package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;
import lenz.opengl.Texture;

public class Projekt extends AbstractOpenGLBase {

    // shapes
    private Shape sunSphere, earthSphere, moonSphere;
    private Shape cube;

    // textures
    private Texture sunTex, earthTex, moonTex, tilesTex;

    // shader programs
    private ShaderProgram phongShader;   // "projekt_v.glsl" / "projekt_f.glsl"
    private ShaderProgram simpleShader;  // "simple_v.glsl"  / "simple_f.glsl"

    // transformation matrices
    private Matrix4 sunMatrix, earthMatrix, moonMatrix, cubeMatrix;

    // angles
    private float angleSun = 0f;
    private float angleEarth = 0f;
    private float angleMoon = 0f;

    public static void main(String[] args) {
        new Projekt().start("CG Projekt - Solar System", 800, 600);
    }

    @Override
    protected void init() {
        // 1) Load textures (with some mipmaps).
        //    Make sure these .jpg files are in your "res/textures/" folder.
        sunTex   = new Texture("2k_sun.jpg", 4, true);
        earthTex = new Texture("2k_earth_daymap.jpg", 4, true);
        moonTex  = new Texture("2k_moon.jpg", 4, true);
        tilesTex = new Texture("tiles_0115_color.jpg", 1, false);

        // 2) Compile Shaders
        // "projekt" => loads "projekt_v.glsl" and "projekt_f.glsl"
        phongShader  = new ShaderProgram("projekt");
        // "simple"  => we will create "simple_v.glsl" / "simple_f.glsl"
        simpleShader = new ShaderProgram("simple");

        // 3) Build geometry
        // a) Load sphere from .obj
        ObjReader reader = new ObjReader("src/res/3D/sphare.obj"); 
        float[] spherePositions = reader.getVertices();
        float[] sphereNormals   = reader.getNormals();
        float[] sphereTexCoords = reader.getTexCoords();

        sunSphere   = new Shape(spherePositions, null, sphereNormals, sphereTexCoords);
        earthSphere = new Shape(spherePositions, null, sphereNormals, sphereTexCoords);
        moonSphere  = new Shape(spherePositions, null, sphereNormals, sphereTexCoords);

        // b) Manually-coded cube
        // Just 12 triangles (6 faces, 2 per face)
        float[] cubePositions = {
            // front face
            -0.5f,-0.5f, 0.5f,   0.5f,-0.5f, 0.5f,   0.5f, 0.5f, 0.5f,
            -0.5f,-0.5f, 0.5f,   0.5f, 0.5f, 0.5f,  -0.5f, 0.5f, 0.5f,
            // back face
            0.5f,-0.5f,-0.5f,   -0.5f,-0.5f,-0.5f,  -0.5f, 0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,   -0.5f, 0.5f,-0.5f,   0.5f, 0.5f,-0.5f,
            // left face
            -0.5f,-0.5f,-0.5f,  -0.5f,-0.5f, 0.5f,  -0.5f, 0.5f, 0.5f,
            -0.5f,-0.5f,-0.5f,  -0.5f, 0.5f, 0.5f,  -0.5f, 0.5f,-0.5f,
            // right face
            0.5f,-0.5f, 0.5f,   0.5f,-0.5f,-0.5f,   0.5f, 0.5f,-0.5f,
            0.5f,-0.5f, 0.5f,   0.5f, 0.5f,-0.5f,    0.5f, 0.5f, 0.5f,
            // top face
            -0.5f, 0.5f, 0.5f,   0.5f, 0.5f, 0.5f,   0.5f, 0.5f,-0.5f,
            -0.5f, 0.5f, 0.5f,   0.5f, 0.5f,-0.5f,  -0.5f, 0.5f,-0.5f,
            // bottom face
            -0.5f,-0.5f,-0.5f,   0.5f,-0.5f,-0.5f,   0.5f,-0.5f, 0.5f,
            -0.5f,-0.5f,-0.5f,   0.5f,-0.5f, 0.5f,  -0.5f,-0.5f, 0.5f
        };
        // Basic normals, or pass null if using a "simple" shader:
        float[] cubeNormals = { /* ... fill out if you want lighting ... */ };
        // Repeated tex coords for each face
        float[] cubeTexCoords = {
            // front face (2 triangles)
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f,
            // back face
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f,
            // left face
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f,
            // right face
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f,
            // top face
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f,
            // bottom face
            0f,0f,  1f,0f,  1f,1f,
            0f,0f,  1f,1f,  0f,1f
        };

        cube = new Shape(cubePositions, null, null, cubeTexCoords);

        // 4) Create projection matrix
        Matrix4 projection = new Matrix4(1, 100);

        // pass projection to both shaders
        glUseProgram(phongShader.getId());
        int projLoc = glGetUniformLocation(phongShader.getId(), "projectionMatrix");
        glUniformMatrix4fv(projLoc, false, projection.getValuesAsArray());

        glUseProgram(simpleShader.getId());
        int projLoc2 = glGetUniformLocation(simpleShader.getId(), "projectionMatrix");
        glUniformMatrix4fv(projLoc2, false, projection.getValuesAsArray());

        // 5) Enable depth test & backface culling
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    @Override
    public void update() {
        // increment angles
        angleSun   += 0.3f;
        angleEarth += 0.7f;
        angleMoon  += 1.0f;

        // build hierarchical transforms

        // Sun: spin in place at origin, shift entire scene away from the camera
        sunMatrix = new Matrix4().translate(0, 0, -6).rotateY(angleSun);

        // Earth: orbit around sun
        //   rotate around origin, then translate outward, then spin
        earthMatrix = new Matrix4()
            .translate(0, 0, -6)
            .rotateY(angleEarth) 
            .translate(2.5f, 0, 0)
            .rotateY(angleEarth * 2); // spin on own axis

        // Moon: child transform of earth => start from earthMatrix
        moonMatrix = new Matrix4(earthMatrix)
            .rotateY(angleMoon)
            .translate(1.0f, 0, 0);  // revolve around earth

        // Cube: rotate separately on the side
        cubeMatrix = new Matrix4()
            .translate(-2, 0, -5)
            .rotateY(angleSun * 0.5f)
            .rotateX(angleSun * 0.3f);
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // ---------- Render Spheres with PHONG Shader ----------
        glUseProgram(phongShader.getId());

        // uniform location for the model-view matrix
        int mvLoc = glGetUniformLocation(phongShader.getId(), "mvMatrix");

        // (1) Sun
        glBindTexture(GL_TEXTURE_2D, sunTex.getId());
        glUniformMatrix4fv(mvLoc, false, sunMatrix.getValuesAsArray());
        sunSphere.render();

        // (2) Earth
        glBindTexture(GL_TEXTURE_2D, earthTex.getId());
        glUniformMatrix4fv(mvLoc, false, earthMatrix.getValuesAsArray());
        earthSphere.render();

        // (3) Moon
        glBindTexture(GL_TEXTURE_2D, moonTex.getId());
        glUniformMatrix4fv(mvLoc, false, moonMatrix.getValuesAsArray());
        moonSphere.render();

        // ---------- Render Cube with SIMPLE Shader ----------
        glUseProgram(simpleShader.getId());
        int mvLoc2 = glGetUniformLocation(simpleShader.getId(), "mvMatrix");
        glUniformMatrix4fv(mvLoc2, false, cubeMatrix.getValuesAsArray());

        glBindTexture(GL_TEXTURE_2D, tilesTex.getId());
        cube.render();
    }
}
