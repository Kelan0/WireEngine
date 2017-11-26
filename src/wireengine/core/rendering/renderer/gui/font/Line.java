package wireengine.core.rendering.renderer.gui.font;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class Line
{
    private List<Word> words;
    private int width;
    private int height;
    private int maxLength;
    private boolean centered;

    public Line(int maxLength, boolean centered)
    {
        this.maxLength = maxLength;
        this.centered = centered;
        this.words = new ArrayList<>(0);
    }

    public boolean addWord(Word word, int spaceSize)
    {
        int wordLength = word.getWidth() + (!words.isEmpty() ? spaceSize : 0);

        if (this.width + wordLength < this.maxLength)
        {
            this.words.add(word);
            this.width += wordLength;
            this.height = Math.max(this.height, word.getHeight());
            return true;
        }

        return false;
    }

    public List<Word> getWords()
    {
        return words;
    }

    public int getWidth()
    {
        return width;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public boolean isCentered()
    {
        return centered;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Word word : words)
        {
            sb.append(word.toString()).append(" ");
        }

        return sb.append("\b").toString();
    }
}
