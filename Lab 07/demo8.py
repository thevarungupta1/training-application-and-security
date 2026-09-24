# Inheritance in Python
# Inheritance is a fundamental concept in object-oriented programming that allows a class to inherit attributes and methods from another class.
# the parent class known as super/base class whereas the child called deived/sub class
# this allow us to make the code more reuable and maintainable.

class FullTimeEmployee:
    empId: int
    firstName: str
    lastName: str
    email: str
    annualSalary: float

    def fullName(self):
        print(f"{self.firstName} {self.lastName}")

class PartTimeEmployee:
    empId: int
    firstName: str
    lastName: str
    email: str
    hourSalary: float

    def fullName(self):
        print(f"{self.firstName} {self.lastName}")

    
fte = FullTimeEmployee()
fte.firstName = "John"
fte.lastName = "Doe"

pte = PartTimeEmployee()
pte.firstName = "Jane"
pte.lastName = "Smith"

fte.fullName()
pte.fullName()
