package com.example.xiahao.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookMangerService extends Service {


    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mIOnNewBookArrivedListeners = new RemoteCallbackList<>();
//    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mIOnNewBookArrivedListeners = new CopyOnWriteArrayList<>();
    public static final String TAG = "BookMangerService";
    private  AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    public BookMangerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Android"));
        mBookList.add(new Book(2,"IOS"));
        new Thread(new ServiceWorker()).start();
    }

    private Binder mBinder = new IBookManager.Stub() {


        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            //此种类型 会导致解注册失败,原因是不虽然是同一个listener,但是通过Binder的时候回产生两个全新的对象
            //所以使用RemoteCallbackList是专门处理跨进程的类
//            if (!mIOnNewBookArrivedListeners.contains(listener)){
//                mIOnNewBookArrivedListeners.add(listener);
//            }else{
//                Log.d(TAG, "already exists.");
//            }
//            Log.d(TAG, "registerListener: "+mIOnNewBookArrivedListeners.size());

            mIOnNewBookArrivedListeners.register(listener);
        }

        @Override
        public void unegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
//            if (mIOnNewBookArrivedListeners.contains(listener)){
//                mIOnNewBookArrivedListeners.remove(listener);
//            }else {
//                Log.d(TAG, "already exists.");
//            }
//            Log.d(TAG, "unegisterListener: "+ mIOnNewBookArrivedListeners.size());
            mIOnNewBookArrivedListeners.unregister(listener);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onNewBookArrived(Book book) throws RemoteException{
        mBookList.add(book);
//        Log.d(TAG, "notify listener"+ mIOnNewBookArrivedListeners.size());
//        for (int i = 0; i < mIOnNewBookArrivedListeners.size(); i++) {
//            IOnNewBookArrivedListener listener = mIOnNewBookArrivedListeners.get(i);
//            Log.d(TAG, "onNewBookArrived: notify"+mIOnNewBookArrivedListeners.size());
//            listener.OnNewBokkArrived(book);
//        }

        final int N = mIOnNewBookArrivedListeners.beginBroadcast();

        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mIOnNewBookArrivedListeners.getBroadcastItem(i);
            if (l!=null){
                l.OnNewBokkArrived(book);
            }
        }
        mIOnNewBookArrivedListeners.finishBroadcast();

    }
    private class ServiceWorker implements Runnable{
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId,"new Book" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }
}
