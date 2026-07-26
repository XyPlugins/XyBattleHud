package org.xyplugin.xybattlehud.damage;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FontMapperTest {
    @Test
    public void mapsEveryDigitAndKeepsUnknownCharacters() {
        assertEquals("零壹貳參肆汙陸柒捌玖-",
                FontMapper.map("0123456789-", Arrays.asList("零", "壹", "貳", "參", "肆", "汙", "陸", "柒", "捌", "玖"),
                        Collections.<Character, String>emptyMap()));
    }

    @Test
    public void mapsConfiguredSymbols() {
        Map<Character, String> symbols = new HashMap<>();
        symbols.put('.', "點");
        assertEquals("壹點汙", FontMapper.map("1.5",
                Arrays.asList("零", "壹", "貳", "參", "肆", "汙", "陸", "柒", "捌", "玖"), symbols));
    }
}
