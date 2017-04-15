package com.example.xiahao.myapplication.query.binder;

import android.os.RemoteException;

import com.example.xiahao.myapplication.ISecurityCenter;

/**
 * Created by xiahao on 2017/2/23.
 */

public class ISecurityCenterImpl extends ISecurityCenter.Stub {
    public static final char SECRET_CODE= '^';
    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
