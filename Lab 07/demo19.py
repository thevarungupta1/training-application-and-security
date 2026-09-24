# Abstraction in python
# Abstraction allows you to define methods in a base class that must be implemented by derived classes, without providing a full implementation in the base class itself.
# abstraction is achieved in Python using abstract base classes (ABCs) and the abc module.

from abc import ABC, abstractmethod

class Car(ABC):
    def milage(self):
        pass
    
class Tesla(Car):
    def milage(self):
        print("Tesla milage is 24 kmph")
        
class Audi(Car):
    def milage(self):
        print("Audi milage is 20 kmph")
        
tesla = Tesla()
audi = Audi()

tesla.milage()  # Tesla milage is 24 kmph
audi.milage()   # Audi milage is 20 kmph