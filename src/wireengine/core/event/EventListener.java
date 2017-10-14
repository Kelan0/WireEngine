package wireengine.core.event;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Kelan
 */
@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface EventListener
{
}
