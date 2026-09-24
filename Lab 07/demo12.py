# Hierarchical Inheritance in Python
# In hierarchical inheritance, multiple child classes inherit from a single parent class.
# each child class can have its own unique attributes and methods, in addition to the inherited ones.

class Animal:
    def speak(self):
        print("Animal speaks")
        
class Dog(Animal):
    def bark(self):
        print("Dog barks")

class Cat(Animal):
    def meow(self):
        print("Cat meows")

cat = Cat()
cat.speak()
cat.meow()

dog = Dog()
dog.speak()
dog.bark()