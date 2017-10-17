package wireengine.core.util;

/**
 * @author Kelan
 */
public class ObjPointer<T>
{
    public T value;

    public ObjPointer(T value)
    {
        this.value = value;
    }

    public ObjPointer()
    {
        this.value = null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjPointer<?> that = (ObjPointer<?>) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return value == null ? "null" : value.toString();
    }
}
