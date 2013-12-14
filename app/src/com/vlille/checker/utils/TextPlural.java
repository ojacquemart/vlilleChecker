package com.vlille.checker.utils;

public  final class TextPlural {

    private TextPlural() {}

    /**
     * Add a "s" to a text to format according to a given value.
     * <pre>
     * toPlural(0, "my %d cent") = my 0 cent
     * toPlural(1, "my %d cent") = my 1 cent
     * toPlural(2, "my %d cent") = my 2 cents
     * </pre>
     *
     * @param value the value to format in text
     * @param text the text to format with value.
     *
     * @return the text in plural form.
     */
    public static String toPlural(long value, String text) {
        text = String.format(text, value);
        if (value > 1) {
            text += "s";
        }

        return text;
    }

}