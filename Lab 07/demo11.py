# Multi-level Inheritance in Python
# In multi-level inheritance, a child class inherits from a parent class, which in turn inherits from another parent class, forming a chain of inheritance.

class Animal:
    def speak(self):
        print("Animal speaks")
        
class Dog(Animal):
    def bark(self):
        print("Dog barks")

class Puppy(Dog):
    def weep(self):
        print("Puppy weeps")

dog = Dog()
dog.speak()
dog.bark()

puppy = Puppy()
puppy.speak()
puppy.bark()
puppy.weep()