package com.thevarungupta;

/**
 * Method Overriding
 * - same method name and same parameters in parent and child class
 * **/

class Parent{
    public void greeting(String name){
        System.out.println("Hello " + name);
    }
}
class Child extends Parent{
    @Override
    public void greeting(String name){
        System.out.println("Hi " + name);
    }
}
public class Demo8 {
    public static void main(String[] args) {
        Child child = new Child();
        child.greeting("John");
    }
}
