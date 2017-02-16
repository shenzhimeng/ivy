package com.example.singleton;

/**
 * 静态内部类的方式
 * 线程安全，推荐使用
 * Created by km on 17/2/15 上午11:47.
 */
public class Singleton4 {

    private static class SingletonHolder {
        private static final Singleton4 INSTANCE = new Singleton4();
    }

    private Singleton4() {

    }

    public static Singleton4 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
