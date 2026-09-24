# Multiple inheritance in Python
# In multiple inheritance, a child class can inherit attributes and methods from more than one parent class.

class Animal:
    def speak(self):
        print("Animal speaks")

class Dog:
    def bark(self):
        print("Dog barks")

class Cat:
    def meow(self):
        print("Cat meows")

class CatDog(Animal, Dog, Cat):
    pass

catdog = CatDog()
catdog.speak()
catdog.bark()
catdog.meow()