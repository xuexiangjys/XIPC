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

package com.xuexiang.xipc.core.receiver;

import android.content.Context;

import com.xuexiang.xipc.XIPC;
import com.xuexiang.xipc.core.channel.IIPCServiceCallback;
import com.xuexiang.xipc.core.channel.IPCCallbackInvocationHandler;
import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.IPCCallbackGc;
import com.xuexiang.xipc.util.ObjectCenter;
import com.xuexiang.xipc.util.SerializeUtils;
import com.xuexiang.xipc.util.TypeCenter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 接收注册的IPC请求
 *
 * @author xuexiang
 * @since 2018/9/17 下午5:15
 */
public abstract class Receiver implements IReceiver {

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    protected static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    protected static final IPCCallbackGc IPC_CALLBACK_GC = IPCCallbackGc.getInstance();

    private long mObjectTimeStamp;

    private Object[] mParameters;

    private IIPCServiceCallback mCallback;

    public Receiver(ObjectWrapper objectWrapper) {
        mObjectTimeStamp = objectWrapper.getTimeStamp();
    }

    protected long getObjectTimeStamp() {
        return mObjectTimeStamp;
    }

    protected Object[] getParameters() {
        return mParameters;
    }

    /**
     * 设置IPC服务回调接口
     *
     * @param callback
     */
    public void setIPCServiceCallback(IIPCServiceCallback callback) {
        mCallback = callback;
    }

    private Object getProxy(Class<?> clazz, int index, long methodInvocationTimeStamp) {
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new IPCCallbackInvocationHandler(methodInvocationTimeStamp, index, mCallback));
    }

    /**
     * 注册回调接口的返回值类型
     *
     * @param clazz
     */
    private static void registerCallbackReturnTypes(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            TYPE_CENTER.register(method.getReturnType());
        }
    }

    /**
     * 设置请求的方法参数
     *
     * @param methodInvocationTimeStamp
     * @param parameterWrappers
     * @throws IPCException
     */
    private void setParameters(long methodInvocationTimeStamp, ParameterWrapper[] parameterWrappers) throws IPCException {
        if (parameterWrappers == null) {
            mParameters = null;
        } else {
            int length = parameterWrappers.length;
            mParameters = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    mParameters[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
                    if (clazz != null && clazz.isInterface()) { //接口，识别为回调接口
                        registerCallbackReturnTypes(clazz);
                        mParameters[i] = getProxy(clazz, i, methodInvocationTimeStamp);
                        IPC_CALLBACK_GC.register(mCallback, mParameters[i], methodInvocationTimeStamp, i);
                    } else if (clazz != null && Context.class.isAssignableFrom(clazz)) {
                        mParameters[i] = XIPC.getContext(); //所有的context统一切换为注册应用的ApplicationContext
                    } else {
                        String data = parameterWrapper.getData();
                        if (data == null) {
                            mParameters[i] = null;
                        } else {
                            mParameters[i] = SerializeUtils.decode(data, clazz);
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行请求的方法，返回结果
     *
     * @param methodInvocationTimeStamp 方法执行的序号
     * @param methodWrapper             请求的方法
     * @param parameterWrappers         请求方法的参数
     * @return
     * @throws IPCException
     */
    public final Reply action(long methodInvocationTimeStamp, MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) throws IPCException {
        setMethod(methodWrapper, parameterWrappers);
        setParameters(methodInvocationTimeStamp, parameterWrappers);
        Object result = invokeMethod();
        if (result == null) {
            return null;
        } else {
            return new Reply(new ParameterWrapper(result));
        }
    }

}
