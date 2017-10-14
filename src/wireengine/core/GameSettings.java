package wireengine.core;

/**
 * @author Kelan
 */
public class GameSettings
{
    private Number maxfps = 60;
    private Number maxtps = 64;
    private Number windowwidth = 1600;
    private Number windowheight = 900;
    private String playername;

    public void parse(String[] rawArgs)
    {
        if (rawArgs == null || rawArgs.length <= 0)
        {
            return;
        } else
        {
            StringBuilder sb = new StringBuilder();

            for (String rawArg : rawArgs)
            {
                if (!rawArg.startsWith("-"))
                {
                    sb.append(" ");
                }
                sb.append(rawArg);
            }

            for (String arg : sb.toString().substring(1).split("-"))
            {
                if (arg != null && arg.length() > 3)
                {
                    String[] components = arg.split(" ");

                    if (components.length > 1)
                    {
                        String argname = components[0];
                        String argval = components[1];

                        try
                        {
                            Object value = getValue(argval);

                            if (value != null)
                            {
                                this.getClass().getDeclaredField(argname).set(this, value);
                            }
                        } catch (IllegalAccessException | NoSuchFieldException e)
                        {
                            e.printStackTrace();
                        }

                        continue;
                    }
                }

                System.out.println("\"" + arg + "\" is not a valid program argument.");
            }
        }
    }

    public Number getNumber(String argname)
    {
        try
        {
            return (Number) this.getClass().getDeclaredField(argname).get(this);
        } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e)
        {
            return null;
        }
    }

    public String getString(String argname)
    {
        try
        {
            return (String) this.getClass().getDeclaredField(argname).get(this);
        } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e)
        {
            return null;
        }
    }

    private Object getValue(String argval)
    {
        //Add more special cases for any other possible program args, like file paths or something.

        try
        {
            return Double.parseDouble(argval);
        } catch (NumberFormatException e)
        {
            return argval;
        }
    }

    public int getMaxFPS()
    {
        return maxfps.intValue();
    }

    public int getMaxTPS()
    {
        return maxtps.intValue();
    }

    public int getWindowWidth()
    {
        return windowwidth.intValue();
    }

    public int getWindowHeight()
    {
        return windowheight.intValue();
    }

    public String getPlayerName()
    {
        if (playername == null || playername.length() <= 0)
        {
             playername = "Player" + WireEngine.engine().getRandom().nextInt(1000);
        }

        return playername;
    }
}
