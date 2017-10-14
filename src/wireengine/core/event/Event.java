package wireengine.core.event;

/**
 * @author Kelan
 */
public abstract class Event<T extends Event<T>>
{
    protected State state;

    public Event(State state)
    {
        this.state = state;
    }

    public Event()
    {
        this(State.NONE);
    }

    public Class<T> getEventClass()
    {
        return (Class<T>) this.getClass();
    }

    public State getState()
    {
        return state;
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + "." + this.state.toString().toLowerCase();
    }

    public enum State
    {
        PRE, POST, NONE;
    }
}
