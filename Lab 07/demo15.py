# Polymorphism in Python
# the ability to change the behavior of a function or method based on the context, are there are two ways to achieve polymorphism: method overloading and method overriding.
# method overloading: defining multiple methods with the same name but different parameters within the same class.
# python does not support traditional method overloading like some other languages, but it can be achieved using default arguments or variable-length arguments.

def addNumber(a, b,c):
    return a + b + c

def addNumber(a, b):
    return a + b

# calling the function
print(addNumber(2, 3))   # 5
print(addNumber(2, 3, 4))  # error: TypeError: addNumber() takes 2 positional arguments but 3 were given