package com.thevarungupta;

interface ICalculator{
    void add(int a, int b);
    void subtract(int a, int b);
    void multiply(int a, int b);
    void divide(int a, int b);
}

class Output2 implements ICalculator{

    @Override
    public void add(int a, int b) {
        System.out.println(a + b);
    }

    @Override
    public void subtract(int a, int b) {
        System.out.println(a - b);
    }

    @Override
    public void multiply(int a, int b) {
        System.out.println(a * b);
    }

    @Override
    public void divide(int a, int b) {
        System.out.println(a / b);
    }
}

public class Demo10 {
    public static void main(String[] args) {
        ICalculator calculator = new Output2();
        calculator.add(10, 5);
        calculator.subtract(10, 5);
        calculator.multiply(10, 5);
        calculator.divide(10, 5);
    }
}
