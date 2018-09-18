/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xipc.core.channel;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.xuexiang.xipc.core.entity.CallbackMail;
import com.xuexiang.xipc.core.entity.Reply;

import java.util.List;

/**
 * IPC服务接口回调
 *
 * @author xuexiang
 * @since 2018/9/17 下午5:24
 */
public interface IIPCServiceCallback extends IInterface {

    /**
     * 请求回调
     * @param mail
     * @return
     * @throws RemoteException
     */
    Reply callback(CallbackMail mail) throws RemoteException;

    /**
     * http://business.nasdaq.com/marketinsite/2016/Indexes-or-Indices-Whats-the-deal.html
     * <p>
     * This article says something about the plural form of "index".
     */
    void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException;

    abstract class Stub extends Binder implements IIPCServiceCallback {

        /**
         * 描述符
         */
        private static final String DESCRIPTOR = "com.xuexiang.xipc.core.channel.IIPCServiceCallback";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IIPCServiceCallback asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IIPCServiceCallback))) {
                return ((IIPCServiceCallback) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, @NonNull Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                case TRANSACTION_callback:
                    data.enforceInterface(DESCRIPTOR);
                    CallbackMail _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = CallbackMail.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Reply _result = this.callback(_arg0);
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_gc:
                    data.enforceInterface(DESCRIPTOR);
                    List list1, list2;
                    ClassLoader cl = this.getClass().getClassLoader();
                    list1 = data.readArrayList(cl);
                    list2 = data.readArrayList(cl);
                    this.gc(list1, list2);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IIPCServiceCallback {

            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public Reply callback(CallbackMail mail) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                Reply _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((mail != null)) {
                        _data.writeInt(1);
                        mail.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_callback, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = Reply.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeList(timeStamps);
                    _data.writeList(indexes);
                    mRemote.transact(Stub.TRANSACTION_gc, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_callback = IBinder.FIRST_CALL_TRANSACTION;

        static final int TRANSACTION_gc = IBinder.FIRST_CALL_TRANSACTION + 1;
    }



}