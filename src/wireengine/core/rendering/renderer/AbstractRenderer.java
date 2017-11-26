package wireengine.core.rendering.renderer;

/**
 * @author Kelan
 */
public abstract class AbstractRenderer implements Comparable<AbstractRenderer>
{
    private int priority;
    protected int width;
    protected int height;
    protected ShaderProgram shader;

    public AbstractRenderer(int priority, int width, int height)
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
    public final int compareTo(AbstractRenderer renderer)
    {
        return Integer.compare(renderer.priority, priority);
    }
}
