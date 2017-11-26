package wireengine.core.rendering;

import wireengine.core.rendering.renderer.ShaderProgram;

/**
 * @author Kelan
 */
public interface IRenderable
{
    void initRenderable();

    void render(double delta, ShaderProgram shaderProgram);
}
