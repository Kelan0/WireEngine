package wireengine.core.rendering.renderer.gui.font;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class Word
{
    private List<Character> characters;
    private int width; // The width of this word in pixels.
    private int height; // The maximum height of this word in pixels.

    public Word()
    {
        this.characters = new ArrayList<>(0);
    }

    public void addCharacter(Character character)
    {
        this.characters.add(character);
        this.width += character.getCharacterData().getAdvance();
        this.height = (int) Math.max(character.getCharacterData().getSize().y, this.height);
    }

    public char getCharacter(int index)
    {
        if (index < 0 || index >= this.characters.size())
        {
            return (char) (-1);
        }

        return this.characters.get(index).getCharacterData().getId();
    }

    public List<Character> getCharacters()
    {
        return characters;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Character character : characters)
        {
            sb.append(character.toString());
        }

        return sb.toString();
    }
}
