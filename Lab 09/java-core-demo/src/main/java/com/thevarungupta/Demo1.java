package com.thevarungupta;

/**
 * instance variable - it is created inside the class but outside any method so scope of the
 * variable through the class
 *
 * static variable - it is created inside the class but outside any method with static
 * keyword so scope of the variable
 *
 * local variable - it is created inside the method so scope of the variable is only
 * inside the method
 * */

class Demo{
    // instance variable
    int x = 10;
    // static variable
    static int y = 20;
    public void addNumber(){
        // local variable
        int a = 100;
        int b = 200;
        System.out.println(x);
    }
}
class Demo1 {
    public static void main(String[] args) {
        Demo demo = new Demo();
        System.out.println(demo.x);
        demo.addNumber();
        System.out.println(Demo.y);
    }
}
