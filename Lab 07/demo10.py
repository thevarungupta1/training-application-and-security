# Simple / Single Inheritance in Python
# In simple inheritance, a child class inherits attributes and methods from a single parent class.
# the child class can override or extend the functionality of the parent class as needed. this is the most basic form of inheritence in object-oriented programming.

class Animal:
    def speak(self):
        print("Animal speaks")
        
class Dog(Animal):
    def bark(self):
        print("Dog barks")

dog = Dog()
dog.speak()
dog.bark()