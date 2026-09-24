# Private access modifier example in Python
# it is a convention to indicate that an attribute or method is intended for internal use within the class and its subclasses.
# private - accessible only within the class itself
class Animal:
    def __init__(self, name, species):
        self.__name = name          # private attribute
        self.__species = species   # private attribute

    def __make_sound(self):       # private method
        print(f"{self.__name} makes a sound.")
        
dog = Animal("Buddy", "Dog")

print(dog.__name)      # accessing private attribute (will raise an AttributeError)
print(dog.__species)   # accessing private attribute (will raise an AttributeError)

dog.__make_sound()  # accessing private method (will raise an AttributeError)