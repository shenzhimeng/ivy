package com.example;

/**
 * 字符串匹配
 * Knuth-Morris-Pratt算法
 * 以三个发明者命名，起头的那个K就是著名科学家Donald Knuth。
 * Created by km on 17/2/16 上午10:36.
 */

public class KMP {
    private static int[] genNext(String p) {
        char[] chars = p.toCharArray();
        int length = p.length();
        int[] next = new int[length];

        int k = -1;
        int j = 0;
        next[0] = k;

        while (j < length - 1) {
            if (k == -1 || chars[j] == chars[k]) {
                k++;
                j++;
                if (chars[j] != chars[k]) {//对abab,abcabc等类似字符串计算next时的优化
                    next[j] = k;
                } else {
                    next[j] = next[k];
                }
            } else {//运用递归思想
                k = next[k];
            }
        }

        return next;
    }

    static int indexOfString(String s, String p) {
        int[] next = genNext(p);
        char[] sChars = s.toCharArray();
        char[] pChars = p.toCharArray();
        int sLen = s.length();
        int pLen = p.length();
        int i = 0, j = 0;

        while (i < sLen && j < pLen) {
            if (j == -1 || sChars[i] == pChars[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }

        return j == pLen ? i - j : -1;
    }
}
