package org.xyplugin.xybattlehud.damage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class DamageNumberFormatter {
    private DamageNumberFormatter() {
    }

    public static String format(double value, int decimalPlaces, String thousandsSeparator) {
        int places = Math.max(0, Math.min(6, decimalPlaces));
        StringBuilder pattern = new StringBuilder(thousandsSeparator.isEmpty() ? "0" : "#,##0");
        if (places > 0) {
            pattern.append('.');
            for (int i = 0; i < places; i++) pattern.append('0');
        }
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        if (!thousandsSeparator.isEmpty()) symbols.setGroupingSeparator(thousandsSeparator.charAt(0));
        DecimalFormat format = new DecimalFormat(pattern.toString(), symbols);
        format.setGroupingUsed(!thousandsSeparator.isEmpty());
        return format.format(value);
    }
}

