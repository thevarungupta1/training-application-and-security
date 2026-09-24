package com.thevarungupta;

/**
 * Encapsulation is one of the four fundamental OOP concepts.
 * The other three are inheritance, polymorphism, and abstraction.
 * we want to hide the data of a class from other classes.
 * - public: The member is accessible from any other class.
 * - protected: The member is accessible within its own package and by subclasses.
 * - default (no modifier): The member is accessible only within its own package.
 * - private: The member is accessible only within its own class.
 * **/

class Student{
    private int rollNo;
    private String name;
    private int passMark = 40;

    // getter and setter methods
    public int getRollNo() {
        return rollNo;
    }
    public void setRollNo(int rollNo) {
        if(rollNo < 0){
            throw new IllegalArgumentException("Roll number must be positive.");
        }
        this.rollNo = rollNo;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
    }
    public int getPassMark() {
        return passMark;
    }
}
public class Demo11 {
    public static void main(String[] args) {
        Student student = new Student();
//        student.rollNo = -11;
//        student.name = null;
//        student.passMark = 10;
//
//        System.out.println("Student Roll No: " + student.rollNo + ", Name: " + student.name + ", Pass Mark: " + student.passMark + "");
        student.setRollNo(10);
        student.setName("John Doe");

        System.out.println("Student Roll No: " + student.getRollNo() + ", Name: " + student.getName() + ", Pass Mark: " + student.getPassMark() + "");
    }
}
