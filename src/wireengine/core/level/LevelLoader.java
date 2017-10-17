package wireengine.core.level;

import wireengine.core.WireEngine;
import wireengine.core.util.FileUtils;

import java.io.IOException;

/**
 * @author Kelan
 */
public class LevelLoader
{
    private int indent = 0;
    private int currentLine = 0;

    private String line = null;
    private String[] lines;

    public Level loadLevel(String file) throws IOException
    {
        Level level = new Level();

        WireEngine.getLogger().info("Loading level " + file);

        StringBuilder source = new StringBuilder();

        if (FileUtils.readFile(file, source))
        {
            lines = source.toString().replace(" ", "").replace("\t", "").split("\n");

            while (true)
            {
                nextLine();
                if (line == null)
                {
                    break;
                }

                if (line.length() > 0)
                {
                    processLine();
                }
            }

            if (indent != 0)
            {
                System.out.println("Level filePath has mismatched parentheses. This may cause errors.");
            }
        }

        return level;
    }

    private void processLine()
    {
        System.out.println("Processing indent " + indent);
        while (true)
        {
            if (line == null)
            {
                break;
            }
        }
        System.out.println("Indent ended");
    }

    public boolean saveLevel(Level level)
    {
        return false;
    }

    private int nextLine()
    {
        if (currentLine < lines.length)
        {
            line = lines[currentLine++];
            int indentChange = 0;

            for (int i = 0; i < line.length(); i++)
            {
                char chr = line.charAt(i);
                if (chr == '{')
                {
                    indentChange++;
                } else if (chr == '}')
                {
                    indentChange--;
                }
            }


            indent += indentChange;
            return indentChange;
        } else
        {
            line = null;
            return 0;
        }
    }
}
