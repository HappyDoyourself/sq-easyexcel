package com.sq.app;

/**
 * @Author fanht
 * @Description
 * @Date 2021/9/8 7:51 下午
 * @Version 1.0
 */
public class TestA {

    {
        System.out.println("为何会在构造方法前执行？");
    }
    static{
        System.out.print("静态代码块");
    }
    public TestA(){
        System.out.print("构造方法");
    }

    public static void main(String[] args) {
        System.out.println("==============start========");
        new TestA();
        System.out.println("=======goon=====");
        new TestA();
        System.out.println("======hha======");
        new TestA();
    }
}
