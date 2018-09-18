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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.xuexiang.xipc.core.entity.Mail;
import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.receiver.Receiver;
import com.xuexiang.xipc.core.receiver.ReceiverDesignator;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.ObjectCenter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC通信服务
 *
 * @author xuexiang
 * @since 2018/9/17 下午4:15
 */
public abstract class IPCService extends Service {

    private static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    private ConcurrentHashMap<Integer, IIPCServiceCallback> mCallbacks = new ConcurrentHashMap<Integer, IIPCServiceCallback>();

    private final IIPCService.Stub mBinder = new IIPCService.Stub() {
        @Override
        public Reply send(Mail mail) {
            try {
                Receiver receiver = ReceiverDesignator.getReceiver(mail.getObject());
                int pid = mail.getPid();
                IIPCServiceCallback callback = mCallbacks.get(pid);
                if (callback != null) {
                    receiver.setIPCServiceCallback(callback);
                }
                return receiver.action(mail.getTimeStamp(), mail.getMethod(), mail.getParameters());
            } catch (IPCException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getMessage());
            }
        }

        @Override
        public void register(IIPCServiceCallback callback, int pid) throws RemoteException {
            mCallbacks.put(pid, callback);
        }

        @Override
        public void gc(List<Long> timeStamps) throws RemoteException {
            OBJECT_CENTER.deleteObjects(timeStamps);
        }
    };

    public IPCService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static class IPCService0 extends IPCService {}

    public static class IPCService1 extends IPCService {}

    public static class IPCService2 extends IPCService {}

    public static class IPCService3 extends IPCService {}

    public static class IPCService4 extends IPCService {}

    public static class IPCService5 extends IPCService {}

    public static class IPCService6 extends IPCService {}

    public static class IPCService7 extends IPCService {}

    public static class IPCService8 extends IPCService {}

    public static class IPCService9 extends IPCService {}

}
