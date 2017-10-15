package wireengine.core.util;

import wireengine.core.WireEngine;

import java.io.*;

/**
 * @author Kelan
 */
public class FileUtils
{
    public static boolean readFile(String file, StringBuilder dest) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
        {
            while (true)
            {
                String line = reader.readLine();

                if (line != null)
                {
                    dest.append(line).append("\n");
                } else
                {
                    break;
                }
            }
            return true;
        } catch (FileNotFoundException e)
        {
            WireEngine.getLogger().warning("File \"" + file + "\" not found.", e);
            return false;
        }
    }
}
