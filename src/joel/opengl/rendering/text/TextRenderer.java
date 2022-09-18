package joel.opengl.rendering.text;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.rendering.Camera;

import static org.lwjgl.opengl.GL40.*;

import java.util.ArrayList;

public class TextRenderer {

    private EntityHandler entityHandler;
    private TextShader shader;

    public final ArrayList<Integer> staticWorldTexts = new ArrayList<>(); // List of entities with StaticWorldTextComponent

    private int vao, indices, vertices;

    public TextRenderer(EntityHandler entityHandler, Camera camera) {
        this.entityHandler = entityHandler;
        shader = new TextShader(camera);
    }

    public TextRenderer init() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        indices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[0], GL_STATIC_DRAW);

        vertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertices);
        glBufferData(GL_ARRAY_BUFFER, new float[0], GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 5, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 5, 4 * 3);

        glBindVertexArray(0);
        return this;
    }

    public void render() {

        shader.bind();
        if (shader.camera.hasChanged()) shader.pushWorldToClipMatrix();

        glBindVertexArray(vao);
        for (int entity : staticWorldTexts) {

            Component[] components = entityHandler.getComponents(entity, TransformComponent.class, StaticWorldTextComponent.class);
            TransformComponent transform = (TransformComponent) components[0];
            StaticWorldTextComponent text = (StaticWorldTextComponent) components[1];
            StaticWorldTextComponent.TextLine line = text.line;

            shader.pushModelToWorldMatrix(transform.getModelToWorldMatrix());

            text.getFont().atlas.bind(0);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, line.getIndices(), GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, vertices);
            glBufferData(GL_ARRAY_BUFFER, line.getVertices(transform.getTranslation()), GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            glDrawElements(GL_TRIANGLES, line.getIndices().length, GL_UNSIGNED_INT, 0);

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            line.changed = false;

        }
        glBindVertexArray(0);

        shader.unbind();

    }

    public void cleanUp() {
        shader.cleanUp();
        glDeleteBuffers(vertices);
        glDeleteBuffers(indices);
        glDeleteVertexArrays(vao);
    }

}
