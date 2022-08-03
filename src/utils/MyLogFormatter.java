package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Форматтер, аналогичный {@link java.util.logging.SimpleFormatter}, но дополненный именем потока
 */
public class MyLogFormatter extends Formatter {
    // немного изменённый формат для вывода миллисекунд, имени потока и без перевода на новую строку
    private static final String format = "[%1$tF %1$tT] [%2$s] [%4$s] [%5$s]: %6$s%7$s%n";
    private final Date date = new Date();

    @Override
    public String format(LogRecord record) {
        date.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += "." + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        // получение имени потока по его ID
        int threadId = record.getThreadID();
        String threadName = Utils.getThread(threadId)
                .map(Thread::getName)
                .orElse(threadId + "");
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                date,
                source,
                record.getLoggerName(),
                record.getLevel().toString(),
                threadName,
                message,
                throwable);
    }
}
