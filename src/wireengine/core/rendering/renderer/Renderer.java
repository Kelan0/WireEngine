package wireengine.core.rendering.renderer;

import org.lwjgl.opengl.GL;
import wireengine.core.rendering.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public abstract class Renderer implements Comparable<Renderer>
{
    private int priority;
    protected int width;
    protected int height;
    protected ShaderProgram shader;

    public Renderer(int priority, int width, int height)
    {
        this.priority = priority;
        this.width = width;
        this.height = height;
        this.shader = new ShaderProgram();
    }

    public abstract void init();

    public abstract void render(double delta, double time);

    public abstract void cleanup();

    public abstract void createProjection(boolean immediate);

    public void initImmediateMode()
    {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        createProjection(true);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();

        glLoadIdentity();
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getPriority()
    {
        return priority;
    }

    /**
     * The priority of this rendering determines the order in which they are initialized and rendered.
     * Higher priority renderers are rendered later. For example, renderers for the 3D scene may be
     * rendered before 2D GUI renderers and so would have a lower priority.
     *
     * @param renderer The rendering to compare this against.
     * @return -1 if this rendering should be rendered before the other rendering.
     */
    @Override
    public final int compareTo(Renderer renderer)
    {
        return Integer.compare(renderer.priority, priority);
    }
}
