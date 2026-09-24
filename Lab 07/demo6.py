# Can we have more then one constructor in a python class?
# No, we cannot have more than one constructor in a python class. Python does not support method overloading like some other languages. If we define multiple constructors, the last one defined will override the previous ones.

class Student:
    def __init__(self, rollNumber):
        print('first constructor')
    
    def __init__(self, rollNumber, name):
        print('second constructor')
        
        
# creating an object of the class
student1 = Student(1, "John")  # This will call the second constructor
student2 = Student(2)  # This will also call the second constructor, and we will get the error