package com.example.xiahao.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.xiahao.myapplication.query.binder.BinderPool;
import com.example.xiahao.myapplication.query.binder.IComputeImpl;
import com.example.xiahao.myapplication.query.binder.ISecurityCenterImpl;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager mRemoteBookManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.manBut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
//        Intent intent = new Intent(this, BinderPoolService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();//为什么要放在线程中执行,CountDownLatch将BindeService这一异步操作转化成同步操作
                //它有可能是耗时的操作,同样biner方法的耗时也有可能是耗时的
            }
        }).start();

    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder iBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter iSecurityCenter = (ISecurityCenter) ISecurityCenterImpl.asInterface(iBinder);
        String msg = "hello-word android";

        try {
            String passowrd = iSecurityCenter.encrypt(msg);
            System.out.println("encry:"+passowrd);
            System.out.println(iSecurityCenter.decrypt(passowrd));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        IBinder iBinder1 = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute iCompute = IComputeImpl.asInterface(iBinder1);
        try {
            System.out.println(iCompute.add(1,2));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
                mRemoteBookManger = bookManager;
                List<Book> list = bookManager.getBookList();
                Log.i(TAG, list.toString());
                Book book = new Book(3, "Android进阶");
                bookManager.addBook(book);
                List<Book> bookList = bookManager.getBookList();
                Log.i(TAG, bookList.toString());

                bookManager.registerListener(onNewBook);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManger = null;
        }
    };

    private IOnNewBookArrivedListener onNewBook = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void OnNewBokkArrived(Book book) throws RemoteException {
            mHadler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };


    @Override
    protected void onDestroy() {
        if (mRemoteBookManger != null && mRemoteBookManger.asBinder().isBinderAlive()) {

            try {
                Log.i(TAG, "onDestroy: ungister" + onNewBook);
                mRemoteBookManger.unegisterListener(onNewBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
