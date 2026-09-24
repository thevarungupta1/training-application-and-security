package com.thevarungupta;

// Simple or Single Inheritance
//class A{
//    void method1() {
//        System.out.println("Method 1");
//    }
//}
//class B extends A{
//    void method2() {
//        System.out.println("Method 2");
//    }
//}

// Multi-level inheritance
//class A{
//    void method1() {
//        System.out.println("Method 1");
//    }
//}
//class B extends A{
//    void method2() {
//        System.out.println("Method 2");
//    }
//}
//class C extends B{
//    void method3() {
//        System.out.println("Method 3");
//    }
//}

// hierarchical inheritance
//class A{
//    void method1() {
//        System.out.println("Method 1");
//    }
//}
//class B extends A{
//    void method2() {
//        System.out.println("Method 2");
//    }
//}
//class C extends A{
//    void method3() {
//        System.out.println("Method 3");
//    }
//}

// multiple inheritance is not supported in java but can be achieved using interfaces
class A{
    void method1() {
        System.out.println("Method 1");
    }
}
class B {
    void method2() {
        System.out.println("Method 2");
    }
}
//class C extends A, B{
//    void method3() {
//        System.out.println("Method 3");
//    }
//}

public class Demo6 {
    public static void main(String[] args) {
        // Single Inheritance
//        A a = new A();
//        a.method1();
//
//        B b = new B();
//        b.method1();
//        b.method2();

        // Multi-level Inheritance
//        A a = new A();
//        a.method1();
//
//        B b = new B();
//        b.method1();
//        b.method2();
//
//        C c = new C();
//        c.method1();
//        c.method2();
//        c.method3();

        // Hierarchical Inheritance
//        A a = new A();
//        a.method1();
//
//        B b = new B();
//        b.method1();
//        b.method2();
//
//        C c = new C();
//        c.method1();
//        c.method3();
    }
}
