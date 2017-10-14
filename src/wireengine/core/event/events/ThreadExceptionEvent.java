package wireengine.core.event.events;

import wireengine.core.TickableThread;
import wireengine.core.event.Event;

/**
 * @author Kelan
 */
public class ThreadExceptionEvent extends Event<ThreadExceptionEvent>
{
    private Exception exception;
    private TickableThread thread;
    private TickableThread.ThreadState threadState;

    public ThreadExceptionEvent(Exception exception, TickableThread thread)
    {
        this.exception = exception;
        this.thread = thread;
        this.threadState = thread.getThreadState();
    }

    public Exception getException()
    {
        return exception;
    }

    public TickableThread getThread()
    {
        return thread;
    }

    public TickableThread.ThreadState getThreadState()
    {
        return threadState;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadExceptionEvent that = (ThreadExceptionEvent) o;

        if (exception != null ? !exception.equals(that.exception) : that.exception != null) return false;
        if (thread != null ? !thread.equals(that.thread) : that.thread != null) return false;
        return threadState == that.threadState;
    }

    @Override
    public int hashCode()
    {
        int result = exception != null ? exception.hashCode() : 0;
        result = 31 * result + (thread != null ? thread.hashCode() : 0);
        result = 31 * result + (threadState != null ? threadState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ThreadExceptionEvent{" + "exception=" + exception + ", thread=" + thread + ", threadState=" + threadState + '}';
    }
}
