package wireengine.core.rendering.renderer.gui.font;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class StaticText
{
    private List<Line> lines = new ArrayList<>();
    private FontData font;

    public StaticText(String text, FontData font, int maxLineLength, boolean centered)
    {
        this.font = font;
        this.addText(text, maxLineLength, centered);
    }

    public StaticText(List<Line> lines)
    {
        this.lines.addAll(lines);
    }

    public List<Line> getLines()
    {
        return lines;
    }

    private void addText(String text, int maxLineLength, boolean centered)
    {
        int spaceSize = this.font.getSpaceAdvance();
        char[] chars = (text + "\0").toCharArray(); // Add trailing null character to complete the word in the current line.

        Line currentLine = new Line(maxLineLength, centered);
        Word currentWord = new Word();

        for (char c : chars)
        {
            if (c == ' ' || c == '\n' || c == '\0') // Add the current word to the current line, and create a new line if the word overflows.
            {
                currentLine = addWord(currentLine, currentWord, centered, maxLineLength, spaceSize);
                currentWord = new Word();

                if (c == '\n' || c == '\0')
                {
                    lines.add(currentLine);
                    currentLine = new Line(maxLineLength, centered);
                }
            } else
            {
                currentWord.addCharacter(new Character(font.getCharacter(c), font.getDefaultSize()));
            }
        }
    }

    private Line addWord(Line currentLine, Word word, boolean nextCentered, int maxLineLength, int spaceSize)
    {
        if (!currentLine.addWord(word, spaceSize))
        {
            lines.add(currentLine);
            currentLine = new Line(maxLineLength, nextCentered);
            currentLine.addWord(word, spaceSize);
        }

        return currentLine;
    }
}
