package net.mgear.gregfoodexpansion.content.lang;

import java.util.Map;

/**
 * en_us → en_ud(原版内置"颠倒英语"语言)的确定性翻转。
 * 做法与原版 en_ud.json 一致:逐字符映射为上下颠倒字形,再整串反转。
 * 未收录字符(含 CJK)原样保留。
 */
public final class EnglishUpsideDown {
    private EnglishUpsideDown() {}

    private static final Map<Character, String> FLIP = Map.ofEntries(
            Map.entry('a', "ɐ"), Map.entry('b', "q"), Map.entry('c', "ɔ"), Map.entry('d', "p"),
            Map.entry('e', "ǝ"), Map.entry('f', "ɟ"), Map.entry('g', "ƃ"), Map.entry('h', "ɥ"),
            Map.entry('i', "ᴉ"), Map.entry('j', "ɾ"), Map.entry('k', "ʞ"), Map.entry('l', "l"),
            Map.entry('m', "ɯ"), Map.entry('n', "u"), Map.entry('o', "o"), Map.entry('p', "d"),
            Map.entry('q', "b"), Map.entry('r', "ɹ"), Map.entry('s', "s"), Map.entry('t', "ʇ"),
            Map.entry('u', "n"), Map.entry('v', "ʌ"), Map.entry('w', "ʍ"), Map.entry('x', "x"),
            Map.entry('y', "ʎ"), Map.entry('z', "z"),
            Map.entry('A', "∀"), Map.entry('B', "𐐒"), Map.entry('C', "Ɔ"), Map.entry('D', "p"),
            Map.entry('E', "Ǝ"), Map.entry('F', "Ⅎ"), Map.entry('G', "⅁"), Map.entry('H', "H"),
            Map.entry('I', "I"), Map.entry('J', "ſ"), Map.entry('K', "ʞ"), Map.entry('L', "˥"),
            Map.entry('M', "W"), Map.entry('N', "N"), Map.entry('O', "O"), Map.entry('P', "d"),
            Map.entry('Q', "Օ"), Map.entry('R', "ᴚ"), Map.entry('S', "S"), Map.entry('T', "⊥"),
            Map.entry('U', "∩"), Map.entry('V', "Λ"), Map.entry('W', "M"), Map.entry('X', "X"),
            Map.entry('Y', "⅄"), Map.entry('Z', "z"),
            Map.entry('1', "Ɩ"), Map.entry('2', "ᄅ"), Map.entry('3', "Ɛ"), Map.entry('4', "ㄣ"),
            Map.entry('5', "ϛ"), Map.entry('6', "9"), Map.entry('7', "ㄥ"), Map.entry('9', "6"),
            Map.entry('.', "˙"), Map.entry(',', "'"), Map.entry('\'', ","), Map.entry('"', "„"),
            Map.entry('`', ","), Map.entry('?', "¿"), Map.entry('!', "¡"), Map.entry('(', ")"),
            Map.entry(')', "("), Map.entry('[', "]"), Map.entry(']', "["), Map.entry('{', "}"),
            Map.entry('}', "{"), Map.entry('<', ">"), Map.entry('>', "<"), Map.entry('&', "⅋"),
            Map.entry('_', "‾"), Map.entry(';', "؛"));

    /** 翻转整串:先逐字符映射,再反转顺序(等价于视觉 180° 旋转)。 */
    public static String flip(String text) {
        // Format tokens must remain syntactically intact and retain their argument identity.
        var matcher = java.util.regex.Pattern.compile("%(?:(\\d+)\\$)?s|%%").matcher(text);
        var chunks = new java.util.ArrayList<String>();
        int cursor = 0;
        int argument = 1;
        while (matcher.find()) {
            chunks.add(flipLiteral(text.substring(cursor, matcher.start())));
            chunks.add(matcher.group().equals("%%") ? "%%"
                    : "%" + (matcher.group(1) == null ? argument++ : matcher.group(1)) + "$s");
            cursor = matcher.end();
        }
        chunks.add(flipLiteral(text.substring(cursor)));
        java.util.Collections.reverse(chunks);
        return String.join("", chunks);
    }

    private static String flipLiteral(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = text.length() - 1; i >= 0; i--) {
            String mapped = FLIP.get(text.charAt(i));
            sb.append(mapped == null ? text.charAt(i) : mapped);
        }
        return sb.toString();
    }
}
