package com.thevarungupta;

class Employee{
    int empId;
    String firstName;
    String lastName;
    String email;
    public void getFullName(){
        System.out.println(firstName + " " + lastName);
    }
}
class FullTimeEmployee extends Employee{
//    int empId;
//    String firstName;
//    String lastName;
//    String email;
    int annualSalary;
//    public void getFullName(){
//        System.out.println(firstName + " " + lastName);
//    }
}

class PartTimeEmployee extends Employee{
//    int empId;
//    String firstName;
//    String lastName;
//    String email;
    int hourSalary;
//    public void getFullName(){
//        System.out.println(firstName + " " + lastName);
//    }
}
public class Demo5 {
    public static void main(String[] args) {
        FullTimeEmployee fte = new FullTimeEmployee();
        PartTimeEmployee pte = new PartTimeEmployee();

        fte.firstName = "John";
        fte.lastName = "Doe";

        pte.firstName = "Jane";
        pte.lastName = "Smith";

        fte.getFullName();
        pte.getFullName();
    }
}
