package com.example.singleton;

/**
 * 饿汉式
 * 线程安全，instance在类装载时就实例化，在不需要考虑lazy load效果时可以使用
 * Created by km on 17/2/15 上午11:31.
 */
public class Singleton1 {
    private static Singleton1 instance = new Singleton1();

    private Singleton1() {

    }

    public static Singleton1 getInstance() {
        return instance;
    }

    //饿汉式的另一种写法，成效跟上面的写法是一样的，都没有lazy load效果
//    private static Singleton1 instance = null;
//
//    static {
//        instance = new Singleton1();
//    }
//
//    private Singleton1() {
//
//    }
//
//    public static Singleton1 getInstance() {
//        return instance;
//    }

}
