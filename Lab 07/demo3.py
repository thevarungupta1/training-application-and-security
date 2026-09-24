# self-parameter refers to the instance of the class itself and access its attributes and methods.
# we can use anything instead of 'self', but it is a strong convention to use 'self' as the first parameter of instance methods.

class Person:
    # this is the constructor method that is called when an object of the class is created.
    # it take two parameters: name and age.
    def __init__(self, name, age):
        self.name = name
        self.age = age
        print('constructor called')
        
    def display_info(self):
        # this is a method that print a display the information of the person.
        print(f"Name: {self.name}, Age: {self.age}")
        
# creating an object of the class
person1 = Person("John Doe", 30)
person1.display_info()

# creating another object of the class
person2 = Person("Jane Doe", 25)
person2.display_info()