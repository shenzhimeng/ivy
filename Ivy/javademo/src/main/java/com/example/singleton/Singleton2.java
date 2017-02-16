package com.example.singleton;

/**
 * 懒汉式
 * 这里细分的话，可以分为两种
 * 一种是最基础的，线程不安全，在单线程的时候可以使用
 * 另一种是线程安全的，但是效率低
 * 之所以写在一块，是因为这细分的两种写法都不推荐使用
 * Created by km on 17/2/15 上午11:39.
 */
public class Singleton2 {

    private static Singleton2 instance = null;

    private Singleton2() {

    }

    /**
     * 线程不安全
     */
    public static Singleton2 getInstance() {
        if (instance == null) {
            instance = new Singleton2();
        }
        return instance;
    }

    /**
     * 线程安全，但是效率低
     */
    public synchronized static Singleton2 getSafeInstance() {
        if (instance == null) {
            instance = new Singleton2();
        }
        return instance;
    }
}
