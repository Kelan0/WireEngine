package wireengine.core;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.logging.*;

/**
 * @author Kelan
 */
public class LogHelper extends ConsoleHandler
{
    private final Logger logger;
    private final Formatter formatter = new Formatter()
    {
        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        private final DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

        @Override
        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder();
            String date = this.dateFormat.format(new Date(record.getMillis()));
            String time = this.timeFormat.format(new Date(record.getMillis()));
            String level = record.getLevel().toString();
            String thread = Thread.currentThread().getName().toUpperCase();

            builder.append("[").append(date).append("]");
            builder.append("[").append(time).append("]");
            builder.append("[").append(thread).append("]");
            builder.append("[").append(level).append("]: ");
            builder.append(formatMessage(record));
            builder.append("\n");

            return builder.toString();
        }
    };

    public LogHelper()
    {
        System.out.println("Creating logger");
        this.setFormatter(this.formatter);
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(this);
        this.setOutputStream(System.out);
        System.setErr(System.out);
    }

    public LogHelper log(Level level, String str, Object... obj)
    {
        String[] lines = str.split("\n");

        for (String line : lines)
        {
            this.logger.log(level, line);
        }

        for (Object o : obj)
        {
            if (o instanceof Throwable)
            {
                this.logger.log(level, "An exception was thrown: ", (Throwable) o);
            } else
            {
                this.logger.log(level, "", o);
            }
        }

        return this;
    }

    public LogHelper severe(String str, Object... obj)
    {
        return this.log(Level.SEVERE, str, obj);
    }

    public LogHelper warning(String str, Object... obj)
    {
        return this.log(Level.WARNING, str, obj);
    }

    public LogHelper info(String str, Object... obj)
    {
        return this.log(Level.INFO, str, obj);
    }

    public LogHelper config(String str, Object... obj)
    {
        return this.log(Level.CONFIG, str, obj);
    }

    public LogHelper fine(String str, Object... obj)
    {
        return this.log(Level.FINE, str, obj);
    }

    public LogHelper finer(String str, Object... obj)
    {
        return this.log(Level.FINER, str, obj);
    }

    public LogHelper finest(String str, Object... obj)
    {
        return this.log(Level.FINEST, str, obj);
    }
}
