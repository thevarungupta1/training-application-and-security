package com.thevarungupta;

/**
 * Abstraction: it is a process of hiding the implementation details and showing only
 * functionality to the user. In java, abstraction is achieved using abstract classes and interfaces.
 **/

abstract class Calculation {
    // non-abstract method
    public void add(int a, int b) {
        System.out.println("The sum is: " + (a + b));
    }

    public void subtract(int a, int b) {
        System.out.println("The difference is: " + (a - b));
    }

    // abstract method
    public abstract void multiply(int a, int b);
    public abstract void divide(int a, int b);
}
class Output extends Calculation{

    @Override
    public void multiply(int a, int b) {
        System.out.println("The product is: " + (a * b));
    }

    @Override
    public void divide(int a, int b) {
        System.out.println("The quotient is: " + (a / b));
    }
}

public class Demo9 {
    public static void main(String[] args) {
        Output output = new Output();
        output.add(10, 5);
        output.subtract(10, 5);
        output.multiply(10, 5);
        output.divide(10, 5);
    }
}
