package com.globalSoftwareSupport;
class Runner1 implements Runnable{

    @Override
    public void run() {
        for(int i=0;i<10;i++){
            System.out.println("Runner1: "+i);
        }
    }
}
class Runner2 implements Runnable{

    @Override
    public void run() {
        for(int i=0;i<10;i++){
            System.out.println("Runner2: "+i);
        }
    }
}
public class App {
    public static void main(String[] args) {
        // Functional Interfaces can be defined with lambda functions having only one function
//        Runnable r1=()->{
//            for(int i=0;i<10;i++){
//                System.out.println("Runner1: "+i);
//            }
//        };
//        Runnable r2=()->{
//            for(int i=0;i<10;i++){
//                System.out.println("Runner2: "+i);
//            }
//        };

        Thread t1=new Thread(new Runner1());
        Thread t2=new Thread(new Runner2());
        t1.start();
        t2.start();
    }
}
