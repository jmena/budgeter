package me.bgx.budget.util.editors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.joda.time.format.PeriodPrinter;

public class PeriodPropertyEditor extends PropertyEditorSupport {
    PeriodFormatter PeriodFormatter = new PeriodFormatterBuilder()
            .append(new CustomPeriodPrinter(), new CustomPeriodParser())
            .toFormatter();

    static String toString(ReadablePeriod period) {
        int years = period.get(DurationFieldType.years());
        int months = period.get(DurationFieldType.months());
        int days = period.get(DurationFieldType.days());
        StringBuilder sb = new StringBuilder();

        if (years != 0) {
            sb.append(years).append("y");
        }
        if (months != 0) {
            sb.append(months).append("m");
        }
        if (days != 0) {
            sb.append(days).append("d");
        }
        return sb.toString();
    }


    private static final class CustomPeriodPrinter implements PeriodPrinter {

        String toString(ReadablePeriod period) {
            return PeriodPropertyEditor.toString(period);
        }

        @Override
        public int calculatePrintedLength(ReadablePeriod period, Locale locale) {
            return toString(period).length();
        }

        @Override
        public int countFieldsToPrint(ReadablePeriod period, int stopAt, Locale locale) {
            int fieldCount = 0;
            if (period.get(DurationFieldType.years()) != 0) {
                fieldCount++;
            }
            if (period.get(DurationFieldType.months()) != 0) {
                fieldCount++;
            }
            if (period.get(DurationFieldType.days()) != 0) {
                fieldCount++;
            }
            return fieldCount;
        }

        @Override
        public void printTo(StringBuffer buf, ReadablePeriod period, Locale locale) {
            buf.append(toString(period));
        }

        @Override
        public void printTo(Writer out, ReadablePeriod period, Locale locale) throws IOException {
            out.write(toString(period));
        }
    }

    private static final class CustomPeriodParser implements PeriodParser {
        @Override
        public int parseInto(ReadWritablePeriod period, String periodStr,
                             int position, Locale locale) {

            int tokenValue = 0;

            StringTokenizer st = new StringTokenizer(periodStr, "ydm", true);
            while (st.hasMoreElements()) {
                String token = st.nextToken();
                if ("d".equals(token)) {
                    period.set(DurationFieldType.days(), tokenValue);
                    tokenValue = 0;
                } else if ("m".equals(token)) {
                    period.set(DurationFieldType.months(), tokenValue);
                    tokenValue = 0;
                } else if ("y".equals(token)) {
                    period.set(DurationFieldType.years(), tokenValue);
                    tokenValue = 0;
                } else {
                    tokenValue = Integer.parseInt(token);
                }
            }
            return periodStr.length();
        }
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return PeriodFormatter.print((Period) getValue());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            text = "";
        }
        text = text.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Invalid period");
        }
        setValue(PeriodFormatter.parsePeriod(text));
    }
}
