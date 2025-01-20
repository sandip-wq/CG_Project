package projekt;

//Alle Operationen aendern das Matrixobjekt selbst und geben das eigene Matrixobjekt zurueck
//Dadurch kann man Aufrufe verketten, z.B.
//Matrix4 m = new Matrix4().scale(5).translate(0,1,0).rotateX(0.5f);
public class Matrix4 {
	
	private float[][] matrix;

	public Matrix4() {
		// TODO mit der Identitaetsmatrix initialisieren
		matrix = new float[4][4];
        matrix[0][0] = 1;
        matrix[1][1] = 1;
        matrix[2][2] = 1;
        matrix[3][3] = 1;
	}

	public Matrix4(Matrix4 copy) {
		// TODO neues Objekt mit den Werten von "copy" initialisieren
		matrix = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = copy.matrix[i][j];
            }
        }
	}

	public Matrix4(float near, float far) {
		// TODO erzeugt Projektionsmatrix mit Abstand zur nahen Ebene "near" und Abstand zur fernen Ebene "far", ggf. weitere Parameter hinzufuegen
		this(); // start with identity
        float fov = (float) Math.toRadians(60.0f);
        float aspect = 1.0f;
        float f = (float)(1.0f / Math.tan(fov / 2.0)); // cotangent of half fov

        matrix[0][0] = f / aspect;
        matrix[1][1] = f;
        matrix[2][2] = -(far + near) / (far - near);
        matrix[2][3] = -(2 * far * near) / (far - near);
        matrix[3][2] = -1;
        matrix[3][3] = 0;
	}

	public Matrix4 multiply(Matrix4 other) {
		// TODO hier Matrizenmultiplikation "this = other * this" einfuegen
		Matrix4 temp = new Matrix4(this); // copy of 'this'
        for(int row = 0; row < 4; row++){
            for(int col = 0; col < 4; col++){
                float sum = 0;
                for(int k = 0; k < 4; k++){
                    sum += other.matrix[row][k] * temp.matrix[k][col];
                }
                matrix[row][col] = sum;
            }
        }
		return this;
	}

	public Matrix4 translate(float x, float y, float z) {
		// TODO Verschiebung um x,y,z zu this hinzufuegen
		Matrix4 t = new Matrix4();
        t.matrix[0][3] = x;
        t.matrix[1][3] = y;
        t.matrix[2][3] = z;
        return multiply(t);
	}

	public Matrix4 scale(float uniformFactor) {
		// TODO gleichmaessige Skalierung um Faktor "uniformFactor" zu this hinzufuegen
		Matrix4 s = new Matrix4();
        s.matrix[0][0] = uniformFactor;
        s.matrix[1][1] = uniformFactor;
        s.matrix[2][2] = uniformFactor;
        return multiply(s);
	}

	public Matrix4 scale(float sx, float sy, float sz) {
		// TODO ungleichfoermige Skalierung zu this hinzufuegen
		 Matrix4 s = new Matrix4();
	        s.matrix[0][0] = sx;
	        s.matrix[1][1] = sy;
	        s.matrix[2][2] = sz;
	        return multiply(s);
	}

	public Matrix4 rotateX(float angle) {
		// TODO Rotation um X-Achse zu this hinzufuegen
		Matrix4 r = new Matrix4();
        float rad = (float)Math.toRadians(angle);
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);
        r.matrix[1][1] = cos;
        r.matrix[1][2] = -sin;
        r.matrix[2][1] = sin;
        r.matrix[2][2] = cos;
        return multiply(r);
	}

	public Matrix4 rotateY(float angle) {
		// TODO Rotation um Y-Achse zu this hinzufuegen
		Matrix4 r = new Matrix4();
        float rad = (float)Math.toRadians(angle);
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);
        r.matrix[0][0] = cos;
        r.matrix[0][2] = sin;
        r.matrix[2][0] = -sin;
        r.matrix[2][2] = cos;
        return multiply(r);
	}

	public Matrix4 rotateZ(float angle) {
		// TODO Rotation um Z-Achse zu this hinzufuegen
		Matrix4 r = new Matrix4();
        float rad = (float)Math.toRadians(angle);
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);
        r.matrix[0][0] = cos;
        r.matrix[0][1] = -sin;
        r.matrix[1][0] = sin;
        r.matrix[1][1] = cos;
        return multiply(r);
	}

	public float[] getValuesAsArray() {
		// TODO hier Werte in einem Float-Array mit 16 Elementen (spaltenweise gefuellt) herausgeben
		float[] out = new float[16];
        // col-major => [0..3] is first column, [4..7] second, etc.
        out[0]  = matrix[0][0];
        out[1]  = matrix[1][0];
        out[2]  = matrix[2][0];
        out[3]  = matrix[3][0];

        out[4]  = matrix[0][1];
        out[5]  = matrix[1][1];
        out[6]  = matrix[2][1];
        out[7]  = matrix[3][1];

        out[8]  = matrix[0][2];
        out[9]  = matrix[1][2];
        out[10] = matrix[2][2];
        out[11] = matrix[3][2];

        out[12] = matrix[0][3];
        out[13] = matrix[1][3];
        out[14] = matrix[2][3];
        out[15] = matrix[3][3];

        return out;
	}
}
