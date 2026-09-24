from abc import ABC, abstractmethod
import math

class Shape(ABC):
    def __init__(self, length: float=0, width: float=0, height: float=0, radius: float=0):
        self.length = length
        self.width = width
        self.height = height
        self.radius = radius
        
    @abstractmethod
    def area(self) -> float:
        pass
    
    @abstractmethod
    def volume(self) -> float:
        pass
    
class Rectangle(Shape):
    def area(self) -> float:
        return self.length * self.width

    def volume(self) -> float:
        return self.length * self.width * self.height

class Circle(Shape):
    def area(self) -> float:
        return math.pi * self.radius * self.radius

    def volume(self) -> float:
        return (4/3) * math.pi * self.radius * self.radius * self.radius
    
class Cone(Shape):
    def area(self) -> float:
        return math.pi * self.radius * (self.radius + math.sqrt(self.height * self.height + self.radius * self.radius))

    def volume(self) -> float:
        return (1/3) * math.pi * self.radius * self.radius * self.height    
    

rectable = Rectangle(length=5, width=3, height=2)
print("Rectangle area:", rectable.area())
print("Rectangle volume:", rectable.volume())

circle = Circle(radius=3)
print("Circle area:", circle.area())
print("Circle volume:", circle.volume())

cone = Cone(radius=3, height=5)
print("Cone area:", cone.area())
print("Cone volume:", cone.volume())    