package wireengine.core.rendering.renderer.gui.font;

import org.lwjgl.util.vector.Vector4f;

/**
 * @author Kelan
 */
public class Character
{
    private CharacterData characterData; // The character data for this character.
    private float fontSize; // The font radius of this character.
    private Vector4f colour; // The colour of this character.

    public Character(CharacterData characterData, float fontSize)
    {
        this.characterData = characterData;
        this.fontSize = fontSize;
    }

    public CharacterData getCharacterData()
    {
        return characterData;
    }

    public float getFontSize()
    {
        return fontSize;
    }

    @Override
    public String toString()
    {
        return String.valueOf(characterData.getId());
    }
}
