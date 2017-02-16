package com.example;


public class MyClass {
    public static void main(String[] arg) {
        testKMP();

        testStaticMethodExtend();

    }

    /**
     * KMP算法测试
     */
    private static void testKMP() {
        String s = "abacababc";
        String p = "abab";
        System.out.println("KMP匹配结果：" + KMP.indexOfString(s, p));
    }

    /**
     * 测试静态方法的继承
     */
    private static void testStaticMethodExtend() {
        StaticMethodExtend.Parent parent = new StaticMethodExtend.Child();
        parent.method();
        parent.staticMethod();
    }
}

