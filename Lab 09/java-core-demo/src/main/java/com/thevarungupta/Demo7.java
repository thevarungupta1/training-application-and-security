package com.thevarungupta;

/**
 * Method Overloading
 *  - same method name but different number of parameters
 *  - same method name but different type of parameters
 *  - same method name but different order of parameters
 * **/

class Calculator{
    public void test(){
        System.out.println("1st method");
    }
    public void test(int a){
        System.out.println("2nd method");
    }
    public void test(String a){
        System.out.println("3rd method");
    }
    public void test(int a, String b){
        System.out.println("4th method");
    }
    public void test(String a, int b){
        System.out.println("5th method");
    }

}

public class Demo7 {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.test();
        calculator.test(10);
        calculator.test("Hello");
        calculator.test(10, "Hello");
        calculator.test("Hello", 10);
    }
}
