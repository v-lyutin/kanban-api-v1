package utils;

import exceptions.DateTimeFormatException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatHandler {
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
    private static final String[] patterns = {
            "yyyy-MM-dd, HH:mm",
            "yyyy.MM.dd, HH:mm",
            "dd-MM-yyyy, HH:mm",
            "dd.MM.yyyy, HH:mm",
            "MM/dd/yyyy, HH:mm",
            "dd.MM.yyyy - HH:mm"
    };

    private static String getPatterns() {
        StringBuilder builder = new StringBuilder();

        for (String pattern : patterns) {
            builder.append(pattern).append("\n");
        }

        return builder.toString();
    }

    public static LocalDateTime parseDateFromString(String date) {
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(date, formatter);
            } catch (DateTimeParseException e) {
                // Продолжаем итерацию в случае ошибки
            }
        }

        throw new DateTimeFormatException("\n[!] Ввод даты должен придерживаться следующих паттернов:\n" +
                getPatterns());
    }
}
