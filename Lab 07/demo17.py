# Method Overriding
# Method overriding occurs when a subclass provides a specific implementation of a method that is already defined in its superclass.
# when a subclass provides its own implementation of a method that exists in the superclass, it overrides the superclass method.

class Animal:
    def speak(self):
        print("Animal speaks")
        
class Dog(Animal):
    def speak(self):
        print("Dog barks")
        
class Cat(Animal):
    def speak(self):
        print("Cat meows")
        
# creating objects and calling the speak method
a = Animal()
d = Dog()
c = Cat()

a.speak()  # Animal speaks
d.speak()  # Dog barks
c.speak()  # Cat meows