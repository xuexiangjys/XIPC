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

import android.os.RemoteException;

import com.xuexiang.xipc.core.entity.CallbackMail;
import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.logs.IPCLog;
import com.xuexiang.xipc.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * IPC回调执行的动态代理
 *
 * @author xuexiang
 * @since 2018/9/17 下午5:26
 */
public class IPCCallbackInvocationHandler implements InvocationHandler {

    private long mTimeStamp;

    private int mIndex;

    private IIPCServiceCallback mCallback;

    public IPCCallbackInvocationHandler(long timeStamp, int index, IIPCServiceCallback callback) {
        mTimeStamp = timeStamp;
        mIndex = index;
        mCallback = callback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            MethodWrapper methodWrapper = new MethodWrapper(method);
            ParameterWrapper[] parameterWrappers = TypeUtils.objectToWrapper(objects);
            CallbackMail callbackMail = new CallbackMail(mTimeStamp, mIndex, methodWrapper, parameterWrappers);
            Reply reply = mCallback.callback(callbackMail);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                /**
                 * Note that the returned type should be registered in the remote process.
                 */
                return reply.getResult();
            } else {
                IPCLog.e("Error occurs: " + reply.getMessage());
                return null;
            }
        } catch (IPCException e) {
            IPCLog.e("Error occurs but does not crash the app.", e);
        } catch (RemoteException e) {
            IPCLog.e("Error occurs but does not crash the app.", e);
        }
        return null;
    }
}
