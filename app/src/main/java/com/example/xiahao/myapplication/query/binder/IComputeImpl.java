package com.example.xiahao.myapplication.query.binder;

import android.os.RemoteException;

import com.example.xiahao.myapplication.ICompute;

/**
 * Created by xiahao on 2017/2/23.
 */

public class IComputeImpl extends ICompute.Stub{
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
