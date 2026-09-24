class CalculatorA:
    def add(self, a, b):
        return a + b

class CalculatorB:
    def subtract(self, a, b):
        return a - b
    
class CalculatorC:
    def multiply(self, a, b):
        return a * b
    
class CalculatorD:
    def divide(self, a, b):
        if b != 0:
            return a / b
        else:
            return "Division by zero is not allowed"
        
# child class Calculator inherits from all four parent classes
class Calculator(CalculatorA, CalculatorB, CalculatorC, CalculatorD):
    def caculate(self, a, b):
        sum_result = self.add(a, b)
        subtract_result = self.subtract(a, b)
        multiply_result = self.multiply(a, b)
        divide_result = self.divide(a, b)
        return sum_result, subtract_result, multiply_result, divide_result
    
# create an instance of the Calculator class and use it
calculator = Calculator()
results = calculator.caculate(10, 5)
print(f"Addition: {results[0]}")
print(f"Subtraction: {results[1]}")
print(f"Multiplication: {results[2]}")
print(f"Division: {results[3]}")