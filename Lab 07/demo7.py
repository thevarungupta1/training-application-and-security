class Student:
    def __init__(self, name = '', age = 0):
        self.name = name
        self.age = age
        
    def display(self):
        print(f"Name: {self.name}, Age: {self.age}")
        
        
# creating an object of the class
student1 = Student("John", 20)
student2 = Student("Alice")
student3 = Student()

student1.display()
student2.display()
student3.display()


        