package wireengine.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelan
 */
public class TickScheduler implements Runnable
{
    private final Map<ITickable, TickState> tickList = new HashMap<>();

    private boolean running = true;

    public void addTickable(ITickable tickable)
    {
        synchronized (tickList)
        {
            if (!tickList.containsKey(tickable))
            {
                this.tickList.put(tickable, new TickState());
            }
        }
    }

    public void removeTickable(ITickable tickable)
    {
        synchronized (tickList)
        {
            if (!tickList.containsKey(tickable))
            {
                this.tickList.remove(tickable);
            }
        }
    }

    public void stop()
    {
        synchronized (this)
        {
            running = false;
        }
    }

    public boolean isRunning()
    {
        synchronized (this)
        {
            return running;
        }
    }

    @Override
    public void run()
    {
        while (this.isRunning())
        {
            for (Map.Entry<ITickable, TickState> entry : this.tickList.entrySet())
            {
                ITickable tickable = entry.getKey();
                TickState state = entry.getValue();

                long currentTime = System.nanoTime();
                long elapsedTime = (currentTime - state.lastTime);

                state.partialTicks = (elapsedTime * (double) tickable.getMaxTickrate()) / 1000000000.0; //a / (b / c) = (ac) / b

                if (state.partialTicks >= 1.0)
                {
                    state.partialTicks--;
                    state.lastTime = currentTime;
                    tickable.scheduleTick(elapsedTime / 1000000000.0);
                }
            }
        }
    }

    private static class TickState
    {
        private long lastTime;
        private double partialTicks;

        private TickState()
        {
            lastTime = System.nanoTime();
        }
    }
}
