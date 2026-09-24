# Creating a class in python
class Book:
    title: str
    author: str
    price: float

    def __init__(self, title, author, price):
        self.title = title
        self.author = author
        self.price = price
    
    def print_info(self):
        print(f"Title: {self.title}, Author: {self.author}, Price: ${self.price} ")
    
    
# ctreating an object of the class
book1_obj = Book("The Great Gatsby", "F. Scott Fitzgerald", 10.99)
book1_obj.print_info()

book2_obj = Book("To Kill a Mockingbird", "Harper Lee", 12.99)
book2_obj.print_info()