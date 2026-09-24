# method overloading in python using default arguments
def addNumber(a=0, b=0, c=0):
    return a + b + c

# calling the function with different number of arguments
print(addNumber())
print(addNumber(2))
print(addNumber(2, 3))
print(addNumber(2, 3, 4))  