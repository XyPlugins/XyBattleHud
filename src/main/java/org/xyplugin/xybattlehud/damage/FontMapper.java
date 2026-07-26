package org.xyplugin.xybattlehud.damage;

import java.util.List;
import java.util.Map;

public final class FontMapper {
    private FontMapper() {
    }

    public static String map(String input, List<String> digits, Map<Character, String> symbols) {
        StringBuilder output = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (current >= '0' && current <= '9' && digits.size() == 10) {
                output.append(digits.get(current - '0'));
            } else if (symbols.containsKey(current)) {
                output.append(symbols.get(current));
            } else {
                output.append(current);
            }
        }
        return output.toString();
    }
}

