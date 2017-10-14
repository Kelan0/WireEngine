package wireengine.core.event;

import wireengine.core.WireEngine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kelan
 */
public final class EventHandler
{
    private static final Map<Class<?>, List<EventPoster>> eventMap = new HashMap<>();

    public <T extends Event<T>> void postEvent(Object poster, Event<T> event)
    {
        for (EventPoster eventPoster : getListeners(event.getClass()))
        {
            eventPoster.post(poster, event);
        }
    }

    public void registerListener(Object listenerObj)
    {
        if (listenerObj == null)
        {
            throw new IllegalStateException("Cannot register a null event listener.");
        }

        boolean isClass = listenerObj.getClass() == Class.class;
        Class<?> listenerClass = isClass ? (Class<?>) listenerObj : listenerObj.getClass();
        Object listenerInst = null;

        if (isClass)
        {
            try
            {
                Constructor<?> constructor = listenerClass.getConstructor();
                constructor.setAccessible(true);
                listenerInst = constructor.newInstance();
            } catch (NoSuchMethodException e)
            {
                WireEngine.getLogger().warning("Unable to initialize listener class. A default constructor is required if a class is passed.");
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e)
            {
                WireEngine.getLogger().warning("Failed to instantiate listener class. ", e);
            }
        } else
        {
            listenerInst = listenerObj;
        }

        if (listenerInst == null)
        {
            WireEngine.getLogger().warning("Failed to get an instance of the passed event listener.");
        }

        for (Method method : listenerClass.getMethods())
        {
            if (method.isAnnotationPresent(EventListener.class))
            {
                method.setAccessible(true);

                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length != 1)
                {
                    WireEngine.getLogger().warning("Cannot register method annotated with @EventHandler with " + parameterTypes.length + " parameters. EventHandler methods require only one parameter of type Event.class");
                }

                Class<?> eventClass = parameterTypes[0];

                if (Event.class.isAssignableFrom(eventClass))
                {
                    getListeners(eventClass).add(new EventPoster(listenerInst, method));
                }
            }
        }
    }

    private List<EventPoster> getListeners(Class<?> eventClass)
    {
        return eventMap.computeIfAbsent(eventClass, e -> new ArrayList<>());
    }

    private class EventPoster
    {
        private Method method;
        private Object listener;

        EventPoster(Object listener, Method method)
        {
            this.listener = listener;
            this.method = method;
        }

        <T extends Event<T>> void post(Object poster, Event<T> event)
        {
            try
            {
                this.method.setAccessible(true);
                this.method.invoke(listener, event);
            } catch (IllegalAccessException | InvocationTargetException e)
            {
                WireEngine.getLogger().warning("An exception was thrown while posting the event " + event + " from " + poster + " to " + method, e);
            }
        }
    }
}

