# Creating a class in python
class Book:
    title: str
    author: str
    price: float
    
    
# ctreating an object of the class
book1_obj = Book()
book1_obj.title = "The Great Gatsby"
book1_obj.author = "F. Scott Fitzgerald"
book1_obj.price = 10.99

print(f"Title: {book1_obj.title}, Author: {book1_obj.author}, Price: ${book1_obj.price} ")

book2_obj = Book()
book2_obj.title = "To Kill a Mockingbird"
book2_obj.author = "Harper Lee"
book2_obj.price = 12.99

print(f"Title: {book2_obj.title}, Author: {book2_obj.author}, Price: ${book2_obj.price} ")