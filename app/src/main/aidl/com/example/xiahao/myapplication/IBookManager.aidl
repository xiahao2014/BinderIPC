// IBookManager.aidl
package com.example.xiahao.myapplication;

// Declare any non-default types here with import statements
import com.example.xiahao.myapplication.Book;
import com.example.xiahao.myapplication.IOnNewBookArrivedListener;

interface IBookManager {

     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(IOnNewBookArrivedListener listener);
     void unegisterListener(IOnNewBookArrivedListener listener);
}
