package com.thevarungupta;


class Book2{
    String title;
    String author;
    int price;

    public Book2(String title, String author, int price){
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public void printBookInfo(){
        System.out.println("Title: " + title + ", Author: " + author + ", Price: " + price);
    }
}

public class Demo4 {
    public static void main(String[] args) {
        Book2 book1 = new Book2("Java Programming", "John Doe", 500);
        Book2 book2 = new Book2("Python Programming", "Jane Doe", 600);

        book1.printBookInfo();
        book2.printBookInfo();
    }
}
