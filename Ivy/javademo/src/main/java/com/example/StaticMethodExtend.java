package com.example;

/**
 * Created by km on 17/2/16 上午10:47.
 */

public class StaticMethodExtend {

    static class Parent {
        public void method() {
            System.out.println("parent method");
        }

        public static void staticMethod() {
            System.out.println("parent static method");
        }
    }

    static class Child extends Parent {
        @Override
        public void method() {
            System.out.println("child method");
        }

        //@Override  error:Method does not override method from its superclass
        public static void staticMethod() {
            System.out.println("child static method");
        }
    }
}
