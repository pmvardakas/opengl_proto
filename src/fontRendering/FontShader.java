package fontRendering;

import lwjglUtil.vector.Vector2f;
import lwjglUtil.vector.Vector3f;
import shaders.ShaderProgram;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/fontRendering/fontVertex.txt";
    private static final String FRAGMENT_FILE = "src/fontRendering/fontFragment.txt";

    private int locationColour;
    private int locationTranslation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationColour = super.getUniformLocation("colour");
        locationTranslation = super.getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    protected void loadColour(Vector3f colour) {
        super.loadVector(locationColour, colour);
    }

    protected void loadTranslation(Vector2f translation) {
        super.load2DVector(locationTranslation, translation);
    }
}
