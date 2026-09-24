# Java

# What is Java?
- it is a programming language
- it is high level, robust, object-oriented and secured programming language



# type of app
- desktop app such as notepad
- web application
- enterprise application such as banking, insurence app
- console app
- mobile app
- embedded system
- games



# Java edition
- Java SE (standard edition)
- Java EE (enterprise edition)
- Java ME (micro edition)



# features of java
- simple
- object oriented
- platform independent
- secured
- robust
- portable
- high performace
- multi-threading


# different between JDK, JRE and JVM
JDK - Java Developer Kit
JRE - Java Runtime Envirnment
JVM - Java Virtual Machine


# JVM
- it is called a virtual machine because it does not exist physically
- it provide specification in which java bytecode can be executed



what are the main task perform by JVM
1. load code
2. verify code
3. execute code
4. provide runtime enviornment



# JRE
the java runtime envirnment is a set of softeware tools which are used for development java application
it contains a set of libraries + other fiels that JVM uses at runtime

# JDK
java development kit
it is a software development kit which is used to develop java application
JDK contain ap rive JVM and few other resources such as compiler, loader etc




# Software and Setup
1. Java 17/21 SDK - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. IDE - IntelliJ IDEA - https://www.jetbrains.com/idea/download/?section=windows





# Write our first java program

class Hello{
  public static void main(String[] args){
     System.out.println("Hello World");
 }
}



- class keyword is used to declare a class in java
- Hello is the name of the class
- public keyword is an access modifier which decide the visibility
- static is keyword used to create a static method which can access directly by the class name
- void is return type it means this method does not return any value
- main() represent the starting / entry point of the program
- String[] args is used for command line argument
- System.out.println() it is used to print statement




Note:
1. java is case sensative - "A"  "a"
2. in the entire app we can have only one main method
3. main method is the starting point of the program
4. all statement in java should end with semicolon(;)





# OOPs
- inheritance
	- simple
  	- multi-level
 	- hierachical
- polymorphsim 
 	- compile time / static polymorphism - method overloading
	- runtime / dynamic polymorphism - method overriding 
- abstraction
 	- abstract class (partial abstraction)
	- interface (full abstraction)
- encapsulation
	- public
	- private
	- protected
	- default




