package com.depromeet.breadmapbackend.domain.search.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UnicodeHandler {

    private static final int HANGEUL_BASE = 0xAC00;    // '가'
    private static final int HANGEUL_END = 0xD7AF;
    private static final int CHO_BASE = 0x1100;
    private static final int JUNG_BASE = 0x1161;
    private static final int JONG_BASE = 0x11A8 - 1;
    // 이하 ja, mo는 단독으로 입력된 자모에 대해 적용
    private static final int JA_BASE = 0x3131;
    private static final int MO_BASE = 0x314F;

    public static List<Character> splitHangulToChosung(String text) {

        List<Character> list = new ArrayList<>();

        for (char c : text.toCharArray()) {
            if (c >= HANGEUL_BASE && c <= HANGEUL_END) {
                int choInt = (c - HANGEUL_BASE) / 28 / 21;
                char cho = (char) (choInt + CHO_BASE);

                list.add(cho);
            } else {
                list.add(c);
            }

        }
        return list;

    }

    public static String splitHangulToConsonant(String text) {

        List<String> list = new ArrayList<>();

        for (char c : text.toCharArray()) {
            if (c <= 10 || c == 32) {
                list.add(String.valueOf(c));
            } else if (c >= JA_BASE && c <= JA_BASE + 36) {
                list.add(String.valueOf(c));
            } else if (c >= MO_BASE && c <= MO_BASE + 58) {
                list.add(String.valueOf((char) 0));
            } else if (c >= HANGEUL_BASE && c <= HANGEUL_END) {
                int choInt = (c - HANGEUL_BASE) / 28 / 21;
                int jungInt = ((c - HANGEUL_BASE) / 28) % 21;
                int jongInt = (c - HANGEUL_BASE) % 28;
                char cho = (char) (choInt + CHO_BASE);
                char jung = (char) (jungInt + JUNG_BASE);
                char jong = jongInt != 0 ? (char) (jongInt + JONG_BASE) : 0;

                list.add(String.valueOf(cho));
                list.add(String.valueOf(jung));
                if (jong != 0) {
                    list.add(String.valueOf(jong));
                }
            } else {
                list.add(String.valueOf(c));
            }

        }
        return String.join("", list);

    }

}
