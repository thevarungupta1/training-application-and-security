class Employee:
    empId: int
    firstName: str
    lastName: str
    email: str

    def fullName(self):
        print(f"{self.firstName} {self.lastName}")

class FullTimeEmployee(Employee):
    annualSalary: float

class PartTimeEmployee(Employee):
    hourSalary: float

    
fte = FullTimeEmployee()
fte.firstName = "John"
fte.lastName = "Doe"

pte = PartTimeEmployee()
pte.firstName = "Jane"
pte.lastName = "Smith"

fte.fullName()
pte.fullName()
