package wireengine.core.rendering.renderer.gui.font;

import org.lwjgl.util.vector.Vector2f;

/**
 * @author Kelan
 */
public class CharacterData
{
    private char id;            // The ASCII ID of this character.
    private Vector2f position;  // The pixel coordinates of the character in the texture atlas.
    private Vector2f size;      // The width and height of the character in the texture atlas.
    private Vector2f offset;    // The offset from the cursor that this character starts at.
    private int advance;      // The distance that the cursor moves along the line after adding this character.
    private float fontSize;     // The font radius of the raw texture data for this character.

    CharacterData(char id, Vector2f position, Vector2f size, Vector2f offset, int advance, float fontSize)
    {
        this.id = id;
        this.position = position;
        this.size = size;
        this.offset = offset;
        this.advance = advance;
        this.fontSize = fontSize;
    }

    public char getId()
    {
        return id;
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public Vector2f getOffset()
    {
        return offset;
    }

    public int getAdvance()
    {
        return advance;
    }

    public float getFontSize()
    {
        return fontSize;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterData that = (CharacterData) o;

        if (id != that.id) return false;
        if (Float.compare(that.advance, advance) != 0) return false;
        if (Float.compare(that.fontSize, fontSize) != 0) return false;
        if (position != null ? !position.equals(that.position) : that.position != null) return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;
        return offset != null ? offset.equals(that.offset) : that.offset == null;
    }

    @Override
    public int hashCode()
    {
        int result = (int) id;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        result = 31 * result + (advance != +0.0f ? Float.floatToIntBits(advance) : 0);
        result = 31 * result + (fontSize != +0.0f ? Float.floatToIntBits(fontSize) : 0);
        return result;
    }
}
