package wireengine.core.rendering.renderer.gui.font;

import org.lwjgl.util.vector.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelan
 */
public class FontData
{
    public static final String FONT_PATH = "res/fonts/";

    private int[] padding;
    private int lineHeight;
    private int spaceAdvance;
    private int fontSize;
    private int atlasWidth;
    private int atlasHeight;

    private Map<java.lang.Character, CharacterData> characters = new HashMap<>();

    private FontData()
    {

    }

    public int[] getPadding()
    {
        return padding;
    }

    public int getLineHeight()
    {
        return lineHeight;
    }

    public int getSpaceAdvance()
    {
        return spaceAdvance;
    }

    public int getDefaultSize()
    {
        return fontSize;
    }

    public int getAtlasWidth()
    {
        return atlasWidth;
    }

    public int getAtlasHeight()
    {
        return atlasHeight;
    }

    public CharacterData getCharacter(char c)
    {
        return this.characters.get(c);
    }

    public float getFontRatio(int pixels)
    {
        return (float) pixels / (float) this.getDefaultSize();
    }

    @Override
    public String toString()
    {
        return "FontData{" + "padding=" + Arrays.toString(padding) + ", lineHeight=" + lineHeight + ", spaceAdvance=" + spaceAdvance + ", fontSize=" + fontSize + ", atlasWidth=" + atlasWidth + ", atlasHeight=" + atlasHeight + ", characters=" + characters + '}';
    }

    private static String readLine(Map<String, String> values, BufferedReader reader)
    {
        values.clear();
        String line = null;

        try
        {
            line = reader.readLine();
        } catch (IOException e)
        {
        }

        if (line != null)
        {
            String[] lineData = line.split(" ");

            if (lineData.length > 0)
            {
                String lineType = lineData[0];

                for (String data : lineData)
                {
                    String[] valueData = data.split("=");

                    if (valueData.length == 2)
                    {
                        values.put(valueData[0], valueData[1]);
                    }
                }

                return lineType;
            }
        }

        return null;
    }

    private static int[] getValues(String name, Map<String, String> values)
    {
        int[] ints = new int[0];

        if (name != null && !name.isEmpty())
        {
            String value = values.get(name);
            if (value != null && !value.isEmpty())
            {
                String[] numbers = value.split(",");

                if (numbers.length > 0)
                {
                    ints = new int[numbers.length];

                    for (int i = 0; i < ints.length; i++)
                    {
                        ints[i] = Integer.valueOf(numbers[i]);
                    }

                    return ints;
                } else
                {
                    ints = new int[]{Integer.valueOf(value)};
                }
            }
        }

        return ints;
    }

    public static FontData loadFont(String name) throws IOException
    {
        return loadFont(new File(FONT_PATH + name + ".fnt"));
    }

    public static FontData loadFont(File font) throws IOException
    {
        if (font != null && font.exists())
        {
            FontData fontData = new FontData();
            BufferedReader reader = new BufferedReader(new FileReader(font));

            Map<String, String> currentValues = new HashMap<>();

            while (true)
            {
                String lineType = readLine(currentValues, reader);

                if (lineType == null)
                {
                    break;
                } else if (lineType.equals("info"))
                {
                    fontData.padding = getValues("padding", currentValues);
                    fontData.fontSize = getValues("radius", currentValues)[0];
                } else if (lineType.equals("common"))
                {
                    fontData.lineHeight = getValues("lineHeight", currentValues)[0];
                    fontData.atlasWidth = getValues("scaleW", currentValues)[0];
                    fontData.atlasHeight = getValues("scaleH", currentValues)[0];
                } else if (lineType.equals("char"))
                {
                    char id = (char) getValues("id", currentValues)[0];
                    int x = getValues("x", currentValues)[0];
                    int y = getValues("y", currentValues)[0];
                    int width = getValues("width", currentValues)[0];
                    int height = getValues("height", currentValues)[0];
                    int xOffset = getValues("xoffset", currentValues)[0];
                    int yOffset = getValues("yoffset", currentValues)[0];
                    int xAdvance = getValues("xadvance", currentValues)[0];
                    int fontSize = fontData.fontSize;

                    if (id == ' ')
                    {
                        fontData.spaceAdvance = xAdvance;
                        continue;
                    }

                    fontData.characters.put(id, new CharacterData(id, new Vector2f(x, y), new Vector2f(width, height), new Vector2f(xOffset, yOffset), xAdvance, fontSize));

                }
            }

            reader.close();
            return fontData;
        } else
        {
            return null;
        }
    }
}
