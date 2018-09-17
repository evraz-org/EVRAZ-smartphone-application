package com.ngse.utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

public class Utils {
    public static double sumDouble(double value1, double value2) {
        double sum = 0.0;
        String value1Str = Double.toString(value1);
        int decimalIndex = value1Str.indexOf(".");
        int value1Precision = 0;
        if (decimalIndex != -1) {
            value1Precision = (value1Str.length() - 1) - decimalIndex;
        }

        String value2Str = Double.toString(value2);
        decimalIndex = value2Str.indexOf(".");
        int value2Precision = 0;
        if (decimalIndex != -1) {
            value2Precision = (value2Str.length() - 1) - decimalIndex;
        }

        int maxPrecision = value1Precision > value2Precision ? value1Precision : value2Precision;
        sum = value1 + value2;
        String s = String.format(Locale.ENGLISH, "%." + maxPrecision + "f", sum);
        sum = Double.parseDouble(s);
        return sum;
    }

    public static Double parseDouble(String x) {
        Double a;
        try {
            a = parseDecimal(x);
        } catch (Exception ParseException) {
            a = (double) 0;
        }
        return a;
    }

    public static double parseDecimal(String input) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = numberFormat.parse(input, parsePosition);

        if (parsePosition.getIndex() != input.length()) {
            throw new ParseException("Invalid input", parsePosition.getIndex());
        }

        return number.doubleValue();
    }
}
