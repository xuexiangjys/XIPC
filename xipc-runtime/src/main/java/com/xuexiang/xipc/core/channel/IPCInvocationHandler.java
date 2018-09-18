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

import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.sender.Sender;
import com.xuexiang.xipc.core.sender.impl.SenderDesignator;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.logs.IPCLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * IPC服务执行方法的动态代理
 *
 * @author xuexiang
 * @since 2018/9/17 下午5:59
 */
public class IPCInvocationHandler implements InvocationHandler {

    private Sender mSender;

    public IPCInvocationHandler(Class<? extends IPCService> service, ObjectWrapper object) {
        mSender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_INVOKE_METHOD, object);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            Reply reply = mSender.send(method, objects);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                return reply.getResult();
            } else {
                IPCLog.e("Error occurs. Error " + reply.getErrorCode() + ": " + reply.getMessage());
                return null;
            }
        } catch (IPCException e) {
            e.printStackTrace();
            IPCLog.e("Error occurs. Error " + e.getErrorCode() + ": " + e.getMessage());
            return null;
        }
    }
}
