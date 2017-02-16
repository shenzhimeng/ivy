package com.example.singleton;

/**
 * 双重校验锁的方式 Double CheckLock（DCL）
 * 线程安全，从懒汉式演变而来，兼顾了安全和性能，推荐使用
 * Created by km on 17/2/15 上午11:47.
 */
public class Singleton3 {
    //这里添加了 volatile 关键字，这是JDK1.5之后提供的对于volatile修饰的变量在读写操作时，不允许乱序
    public volatile static Singleton3 instance = null;

    private Singleton3() {

    }

    /**
     * 常见的写法
     */
    public static Singleton3 getInstance() {
        if (instance == null) {
            synchronized (Singleton3.class) {
                if (instance == null) {
                    instance = new Singleton3();
                }
            }
        }

        return instance;
    }

    /**
     * 这种写法能提升性能，原因跟使用了volatile有关，因为直接访问volatile开销比较大，所以减少访问次数自然可以减少开销
     */
    public static Singleton3 getInstanceBetter() {
        //定义临时变量
        Singleton3 tmp = instance;
        if (tmp == null) {
            synchronized (Singleton3.class) {
                tmp = instance;
                if (tmp == null) {
                    tmp = new Singleton3();
                    instance = tmp;
                }
            }
        }

        return tmp;
    }
}
