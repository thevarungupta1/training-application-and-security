package com.thevarungupta;

/**
 * Book
 *  - title
 *  - author
 *  - price
 * **/

class Book{
    String title;
    String author;
    int price;
    public void printBookInfo(){
        System.out.println("Title: " + title + ", Author: " + author + ", Price: " + price);
    }
}

public class Demo3 {
    public static void main(String[] args) {
        Book book1 = new Book();
        book1.title = "Java Programming";
        book1.author = "John Doe";
        book1.price = 500;

        Book book2 = new Book();
        book2.title = "Python Programming";
        book2.author = "Jane Doe";
        book2.price = 600;

//        System.out.println("Book 1: " + book1.title + " by " + book1.author + " costs " + book1.price);
//        System.out.println("Book 2: " + book2.title + " by " + book2.author + " costs " + book2.price);

        book1.printBookInfo();
        book2.printBookInfo();
    }
}
