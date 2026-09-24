# Encapsulation example in Python
# it is a mechanism to restrict access to certain attributes and methods of a class, promoting data hiding and protecting the internal state of an object.
# public - accessible from anywhere
# protected - accessible within the class and its subclasses
# private - accessible only within the class itself

class Animal:
    def __init__(self, name, species):
        self.name = name          # public attribute
        self.species = species   # public attribute

    def make_sound(self):       # public method
        print(f"{self.name} makes a sound.")
        
dog = Animal("Buddy", "Dog")

print(dog.name)      # accessing public attribute
print(dog.species)   # accessing public attribute

dog.make_sound()  # accessing public method