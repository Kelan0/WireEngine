package wireengine.core;

/**
 * @author Kelan
 */
public interface ITickable
{
    /**
     * Schedule a tick for this {@code ITickable} to run. This method should not run the
     * tick if this is a separate thread.
     *
     * @param delta The delta of this tick.
     */
    void scheduleTick(double delta);

    int getMaxTickrate();
}
