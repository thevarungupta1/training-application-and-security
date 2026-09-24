# Protected access modifier example in Python
# it is a convention to indicate that an attribute or method is intended for internal use within the class and its subclasses.
# protected - accessible within the class and its subclasses
class Animal:
    def __init__(self, name, species):
        self._name = name          # protected attribute
        self._species = species   # protected attribute

    def _make_sound(self):       # protected method
        print(f"{self._name} makes a sound.")
        
dog = Animal("Buddy", "Dog")

print(dog._name)      # accessing protected attribute
print(dog._species)   # accessing protected attribute

dog._make_sound()  # accessing protected method