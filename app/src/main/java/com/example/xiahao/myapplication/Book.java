package com.example.xiahao.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiahao on 2017/2/16.
 */

public class Book implements Parcelable{
    int id;
    String type;

    public Book(int id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
    }

    protected Book(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
