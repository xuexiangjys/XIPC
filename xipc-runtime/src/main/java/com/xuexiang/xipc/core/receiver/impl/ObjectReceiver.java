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

package com.xuexiang.xipc.core.receiver.impl;

import com.xuexiang.xipc.core.receiver.Receiver;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 处理对象方法的执行
 *
 * @author xuexiang
 * @since 2018/9/18 下午3:25
 */
public class ObjectReceiver extends Receiver {

    private Method mMethod;

    private Object mObject;

    public ObjectReceiver(ObjectWrapper objectWrapper) {
        super(objectWrapper);
        mObject = OBJECT_CENTER.getObject(getObjectTimeStamp());
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws IPCException {
        Method method = TYPE_CENTER.getMethod(mObject.getClass(), methodWrapper);
        TypeUtils.validateAccessible(method);
        mMethod = method;
    }

    @Override
    public Object invokeMethod() throws IPCException {
        Exception exception;
        try {
            return mMethod.invoke(mObject, getParameters());
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new IPCException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking method " + mMethod + " on " + mObject, exception);
    }
}
