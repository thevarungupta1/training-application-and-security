# Creating a class in python
class Book:
    title: str
    author: str
    price: float
    
    def print_info(self):
        print(f"Title: {self.title}, Author: {self.author}, Price: ${self.price} ")
    
    
# ctreating an object of the class
book1_obj = Book()
book1_obj.title = "The Great Gatsby"
book1_obj.author = "F. Scott Fitzgerald"
book1_obj.price = 10.99

book1_obj.print_info()

book2_obj = Book()
book2_obj.title = "To Kill a Mockingbird"
book2_obj.author = "Harper Lee"
book2_obj.price = 12.99

book2_obj.print_info()