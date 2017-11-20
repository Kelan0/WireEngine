package wireengine.core.util;

import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Implementation of some string functions available in C and C++
 *
 * @author Kelan
 */
public class Utils
{
    /**
     * Finds the index of the first different character in the two strings.
     * @param a A string to iterate through
     * @param b A string to iterate through
     * @return The index of the first different character in both strings.
     */
    public static int stringSpan(String a, String b)
    {
        int i = 0;

        while (i < a.length() && i < b.length())
        {
            if (a.charAt(i) != b.charAt(i))
            {
                break;
            }
            i++;
        }

        return i;
    }

    /**
     * Compares two strings up to the first {@code maxChars}
     * @param a The string to be compared
     * @param b The string to compare to.
     * @param maxChars The number of characers from the start of the string to compare.
     * @return 0 if the strings are equal up until {@code maxChars}, -1 if a is less than b, and +1 if b is less than a
     */
    public static int stringCompare(String a, String b, int maxChars)
    {
        char[] chars1 = (a + "\0").toCharArray();
        char[] chars2 = (b + "\0").toCharArray();

        for (int i = 0; i < maxChars; i++)
        {
            if (chars1[i] < chars2[i])
            {
                return -1;
            } else if (chars1[i] > chars2[i])
            {
                return +1;
            } else if (chars1[i] == '\0' || chars2[i] == '\0')
            {
                break;
            }
        }
        return 0;
    }

    public static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e)
        {
            return false;
        }
    }

    public static boolean isFloat(String str)
    {
        try
        {
            Float.parseFloat(str);
            return true;
        } catch(NumberFormatException e)
        {
            return false;
        }
    }

    public static String getFileExtension(String filePath, String separator)
    {
        char extensionSeparator = '.';
        if (filePath.contains(Character.toString(extensionSeparator)))
        {
            int directoryIndex = filePath.lastIndexOf(separator);

            if (directoryIndex >= 0 && directoryIndex < filePath.length())
            {
                String filename = filePath.substring(directoryIndex);

                int extensionIndex = filename.indexOf(extensionSeparator) + 1;

                if (extensionIndex >= 0 && extensionIndex < filename.length())
                {
                    return filename.substring(extensionIndex).toUpperCase();
                }
            }
        }
        return "";
    }

    /**
     * Checks if the specified list contains a Vector3f that is within a specified distance of the specified vector.
     * @param list The list to check.
     * @param vector The vector to find.
     * @param epsilon The maximum distance to find the vector in.
     * @return The index of the first vector contained in the list that was closer than {@code epsilon} to {@code vector}, -1 if none exists.
     */
    public static <T extends Vector3f> int getCloseIndex(List<T> list, Vector3f vector, float epsilon)
    {
        if (list == null || vector == null || Float.isNaN(epsilon) || Float.isInfinite(epsilon))
        {
            return -1;
        } else
        {
            epsilon = Math.abs(epsilon);

            if (epsilon <= 0.0F)
            {
                return list.indexOf(vector);
            } else
            {
                for (int i = 0; i < list.size(); i++)
                {
                    Vector3f v = list.get(i);

                    if (v != null && Vector3f.sub(v, vector, null).lengthSquared() <= epsilon * epsilon)
                    {
                        return i;
                    }
                }

                return -1;
            }
        }
    }
}
