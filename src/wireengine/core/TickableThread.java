package wireengine.core;

import wireengine.core.event.events.ThreadExceptionEvent;

import static wireengine.core.TickableThread.ThreadState.*;

/**
 * @author Kelan
 */
public abstract class TickableThread implements ITickable, Runnable
{
    protected final Thread thread;

    private final Object tickLock = new Object();
    private final Object stateLock = new Object();
    private ThreadState threadState = IDLE;
    private boolean initialized = false;
    private boolean stopped = false;
    private long lastTime;
    private int scheduledTicks = 0;
    private double lastDelta = 0.0;
    private double timeSeconds = -1.0;
    private double partialTicks = 0.0;

    public TickableThread(String threadName)
    {
        this.thread = new Thread(this, threadName);
    }

    public abstract void init();

    public abstract void tick(double delta);

    public abstract void cleanup();

    public final void startThread()
    {
        if (this.thread == null)
        {
            throw new NullPointerException("Started null thread.");
        }

        if (this.thread.isAlive())
        {
            throw new IllegalStateException("Thread \"" + thread.getName() + "\" was already started.");
        }

        if (this.thread.getId() == Thread.currentThread().getId())
        {
            throw new IllegalStateException("Thread \"" + thread.getName() + "\" cannot be started from within itself.");
        }

        WireEngine.getLogger().info("Starting thread \"" + thread.getName() + "\" from thread \"" + Thread.currentThread().getName() + "\"");
        this.thread.start();
    }

    @Override
    public final void scheduleTick(double delta)
    {
        synchronized (tickLock)
        {
            scheduledTicks++;
        }
    }

    private Exception doInitialize()
    {
        try
        {
            WireEngine.getLogger().info("Initializing " + Thread.currentThread().getName());
            this.init();
            this.initialized = true;
        } catch (Exception e)
        {
            return e;
        }
        WireEngine.getLogger().info("Finished initializing");

        synchronized (this.stateLock)
        {
            this.threadState = IDLE;
            this.stateLock.notifyAll();
        }

        return null;
    }

    private Exception doStop()
    {
        try
        {
            WireEngine.getLogger().info("Stopping and cleaning up " + Thread.currentThread().getName());
            this.cleanup();
            this.stopped = true;
        } catch (Exception e)
        {
            return e;
        }

        synchronized (this.stateLock)
        {
            this.threadState = IDLE;
            this.stateLock.notifyAll();
        }

        return null;
    }

    private Exception doTick()
    {
//        if (WireEngine.isRenderThread())
//        {
//            System.out.println(this.getScheduledTicks() + " thcks scheduled for renderer");
//        }
        try
        {
            Thread.sleep(2); //CPU usage is high without this because of the synchronized blocks.

//            if (timeSeconds < 0.0)
//            { //Handle first tick, as lastTime starts at 0 ns, and this causes timeSeconds to start at about 2 seconds rather than 0 seconds. This can mess up some time-sensitive animations.
//                lastTime = currentTime;
//                timeSeconds = 0.0;
//            }



            for (int i = 0; i < this.getScheduledTicks(); i++)
            {
                long currentTime = System.nanoTime();
                double elapsedTime = (double) (currentTime - lastTime);
                double delta = elapsedTime / 1000000000.0;

                this.lastTime = currentTime;
                this.scheduledTicks--;
                this.timeSeconds += delta;

                this.tick(delta);
                this.lastDelta = delta;

                double nsPerTick = 1000000000.0 * this.lastDelta;
                this.partialTicks = (currentTime - lastTime) / nsPerTick;
            }

        } catch (Exception e)
        {
            return e;
        }

        return null;
    }

    private double getAverageTickrate(float seconds)
    {
        return 0;
    }

    @Override
    public final void run()
    {
        WireEngine.getLogger().info("Starting " + Thread.currentThread().getName());
        lastTime = System.nanoTime();

        while (!stopped)
        {
            ThreadState tempState = getThreadState();

            if (tempState == RUN && initialized)
            {
                handleException(doTick());
            }

            if (tempState == INITIALISE)
            {
                handleException(doInitialize());
            }

            if (tempState == STOP)
            {
                handleException(doStop());
            }
        }

        WireEngine.getLogger().info("Stopping " + Thread.currentThread().getName());
    }

    public final boolean handleException(Exception exc)
    {
        if (exc != null)
        {
            WireEngine.getLogger().warning("An exception was thrown. The thread will be idle until this exception is handled.");
            exc.printStackTrace();

            ThreadExceptionEvent event = new ThreadExceptionEvent(exc, this); //create the event before the thread state changed.
            setThreadState(IDLE);
            WireEngine.engine().getEventHandler().postEvent(this, event);

            return true;
        }
        return false;
    }

    /**
     * Get the state of this thread.
     * 0 = not running
     * 1 = running but not initialized.
     * 2 = running and initialized.
     * 3 = idle
     *
     * @return The thread state.
     */
    public final ThreadState getThreadState()
    {
        synchronized (stateLock)
        {
            return this.threadState;
        }
    }

    public final void setThreadState(ThreadState state)
    {
        synchronized (stateLock)
        {
            this.threadState = state;
            this.scheduledTicks = 0;

            while (threadState.needsWait())
            {
                try
                {
                    this.stateLock.wait();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public double getPartialTicks()
    {
        return partialTicks;
    }

    public double getTimeSeconds()
    {
        return timeSeconds;
    }

    public final int getScheduledTicks()
    {
        synchronized (tickLock)
        {
            return scheduledTicks;
        }
    }

    public final String getThreadName()
    {
        return this.thread.getName();
    }

    @Override
    public String toString()
    {
        return "TickableThread{" + "thread=" + thread + ", threadState=" + threadState + '}';
    }

    public enum ThreadState
    {
        /**
         * The thread is not doing anything currently, waiting for its state to be changed.
         */
        IDLE(false),

        /**
         * The thread is currently being initialized. i.e. bind thread loading textures or sounds etc.
         */
        INITIALISE(true),

        /**
         * The thread is currently running.
         */
        RUN(false),

        /**
         * The thread is stopping.
         */
        STOP(false);

        private boolean needsWait;

        ThreadState(boolean needsWait)
        {
            this.needsWait = needsWait;
        }

        /**
         * @return True if the thread needs to wait for this state to change.
         */
        public boolean needsWait()
        {
            return needsWait;
        }
    }
}
