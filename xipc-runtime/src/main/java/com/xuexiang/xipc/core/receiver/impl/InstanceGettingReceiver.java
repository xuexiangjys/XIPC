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
import java.lang.reflect.Modifier;

public class InstanceGettingReceiver extends Receiver {

    private Method mMethod;

    private Class<?> mObjectClass;

    public InstanceGettingReceiver(ObjectWrapper objectWrapper) throws IPCException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
        mObjectClass = clazz;
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws IPCException {
        int length = parameterWrappers.length;
        Class<?>[] parameterTypes = new Class<?>[length];
        for (int i = 0; i < length; ++i) {
            parameterTypes[i] = TYPE_CENTER.getClassType(parameterWrappers[i]);
        }
        String methodName = methodWrapper.getName();
        Method method = TypeUtils.getMethodForGettingInstance(mObjectClass, methodName, parameterTypes);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IPCException(ErrorCodes.METHOD_GET_INSTANCE_NOT_STATIC,
                    "Method " + method.getName() + " of class " + mObjectClass.getName() + " is not static. "
                            + "Only the static method can be invoked to get an instance.");
        }
        TypeUtils.validateAccessible(method);
        mMethod = method;
    }

    @Override
    public Object invokeMethod() throws IPCException {
        Exception exception;
        try {
            Object object = mMethod.invoke(null, getParameters());
            OBJECT_CENTER.putObject(getObjectTimeStamp(), object);
            return null;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new IPCException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking method " + mMethod + " to get an instance of "
                        + mObjectClass.getName(), exception);
    }
}
