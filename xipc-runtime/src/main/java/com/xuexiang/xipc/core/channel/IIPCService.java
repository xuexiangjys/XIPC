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

import com.xuexiang.xipc.core.entity.Mail;
import com.xuexiang.xipc.core.entity.Reply;

import java.util.List;

/**
 * IPC通信服务实现接口
 *
 * @author xuexiang
 * @since 2018/9/17 下午3:58
 */
public interface IIPCService extends IInterface {

    /**
     * 发送请求
     *
     * @param mail
     * @return
     * @throws RemoteException
     */
    Reply send(Mail mail) throws RemoteException;

    /**
     * 注册回调
     *
     * @param callback
     * @param pid
     * @throws RemoteException
     */
    void register(IIPCServiceCallback callback, int pid) throws RemoteException;

    /**
     * 资源回收
     *
     * @param timeStamps
     * @throws RemoteException
     */
    void gc(List<Long> timeStamps) throws RemoteException;


    abstract class Stub extends Binder implements IIPCService {

        private static final String DESCRIPTOR = "com.xuexiang.xipc.core.channel.IIPCService";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IIPCService asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IIPCService))) {
                return ((IIPCService) iin);
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
                case TRANSACTION_SEND:
                    data.enforceInterface(DESCRIPTOR);
                    Mail _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = Mail.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Reply _result = this.send(_arg0); //进行请求
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_REGISTER:
                    data.enforceInterface(DESCRIPTOR);
                    IIPCServiceCallback _arg1;
                    IBinder iBinder = data.readStrongBinder();
                    _arg1 = IIPCServiceCallback.Stub.asInterface(iBinder);
                    int pid = data.readInt();
                    this.register(_arg1, pid);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_GC:
                    data.enforceInterface(DESCRIPTOR);
                    ClassLoader cl = this.getClass().getClassLoader();
                    List list = data.readArrayList(cl);
                    this.gc(list);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IIPCService {

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
            public Reply send(Mail mail) throws RemoteException {
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
                    mRemote.transact(Stub.TRANSACTION_SEND, _data, _reply, 0);
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
            public void register(IIPCServiceCallback callback, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    _data.writeInt(pid);
                    mRemote.transact(Stub.TRANSACTION_REGISTER, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void gc(List<Long> timeStamps) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeList(timeStamps);
                    mRemote.transact(Stub.TRANSACTION_GC, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_SEND = IBinder.FIRST_CALL_TRANSACTION;

        static final int TRANSACTION_REGISTER = IBinder.FIRST_CALL_TRANSACTION + 1;

        static final int TRANSACTION_GC = IBinder.FIRST_CALL_TRANSACTION + 2;
    }
}
