// IOnNewBookArrivedListener.aidl
package com.example.xiahao.myapplication;

// Declare any non-default types here with import statements
import com.example.xiahao.myapplication.Book;
interface IOnNewBookArrivedListener {

   void OnNewBokkArrived(in Book book);
}
