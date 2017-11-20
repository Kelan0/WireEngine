package wireengine.core.rendering;

/**
 * @author Kelan
 */
public interface IRenderable
{
    void initRenderable();

    void render(double delta, ShaderProgram shaderProgram);
}
