package wireengine.core.rendering;

import wireengine.core.TickableThread;
import wireengine.core.WireEngine;
import wireengine.core.event.Event;
import wireengine.core.event.events.TickEvent;
import wireengine.core.rendering.renderer.AbstractRenderer;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kelan
 */
public class RenderEngine extends TickableThread
{
    private Window window;
    private List<AbstractRenderer> renderers;

    public RenderEngine()
    {
        super("RENDER-THREAD");
        this.window = new Window();
        this.renderers = new ArrayList<>();
    }

    @Override
    public int getMaxTickrate()
    {
        return WireEngine.engine().getGameSettings().getMaxFPS();
    }

    @Override
    public void init()
    {
        WireEngine.getLogger().info("Initializing rendering");
        this.window.init();

        for (AbstractRenderer renderer : this.renderers)
        {
            renderer.init();
        }

        DebugRenderer.getInstance().init();
        window.showWindow(true);
    }

    @Override
    public final void tick(double delta)
    {
        this.window.update(delta);
        if (!window.shouldClose())
        {
            WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.RenderTickEvent(Event.State.PRE, delta));
            for (AbstractRenderer renderer : this.renderers)
            {
                renderer.render(delta, this.getTimeSeconds());
            }
            WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.RenderTickEvent(Event.State.POST, delta));
        } else
        {
            WireEngine.engine().stop();
        }
    }

    @Override
    public void cleanup()
    {
        WireEngine.getLogger().info("Cleaning up rendering");
        this.window.cleanup();

        for (AbstractRenderer renderer : this.renderers)
        {
            renderer.cleanup();
        }
    }

    public void addRenderer(AbstractRenderer renderer)
    {
        if (this.getThreadState() != ThreadState.RUN)
        {
            if (!this.renderers.contains(renderer))
            {
                this.renderers.add(renderer);
                Collections.sort(this.renderers);
            }
        }
    }

    public Window getWindow()
    {
        return window;
    }

//    public AbstractRenderer removeRenderer(AbstractRenderer rendering)
//    {
//        int index = this.renderers.indexOf(rendering);
//
//        if (index > 0)
//        {
//            Collections.sort(this.renderers);
//            return this.renderers.remove(index);
//        }
//
//        return null;
//    }
}
