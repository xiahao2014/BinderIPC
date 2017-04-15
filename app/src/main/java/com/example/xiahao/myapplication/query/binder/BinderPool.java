package com.example.xiahao.myapplication.query.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.xiahao.myapplication.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xiahao on 2017/2/23.
 */

public class BinderPool {
    public static final String TAG = "BinderPool";
    public static final int BINDER_NODE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;
    private Context mContext;
    private IBinderPool mIBinderPool;
    private static volatile BinderPool sInstance;
    private CountDownLatch mCountDownLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mIBinderPool != null) {
                binder = mIBinderPool.queryBindery(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;

    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIBinderPool = IBinderPool.Stub.asInterface(iBinder);
            try {
                mIBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

    };

    //销毁代理类,重启服务
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w(TAG, "binder: deed");
            mIBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mIBinderPool = null;
            connectBinderPoolService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        public  BinderPoolImpl(){
            super();
        }
        @Override
        public IBinder queryBindery(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {

                case BINDER_SECURITY_CENTER:{
                    binder = new ISecurityCenterImpl();
                    break;}
                case BINDER_COMPUTE:{
                    binder = new IComputeImpl();
                    break;}
                default:
                    break;
            }
            return binder;
        }
    }
}
