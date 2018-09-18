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

/**
 *
 *
 * @author xuexiang
 * @since 2018/9/17 下午6:49
 */
public class UtilityReceiver extends Receiver {

    private Method mMethod;

    private Class<?> mClass;

    public UtilityReceiver(ObjectWrapper objectWrapper) throws IPCException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
        mClass = clazz;
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws IPCException {
        Method method = TYPE_CENTER.getMethod(mClass, methodWrapper);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IPCException(ErrorCodes.ACCESS_DENIED,
                    "Only static methods can be invoked on the utility class " + mClass.getName()
                            + ". Please modify the method: " + mMethod);
        }
        TypeUtils.validateAccessible(method);
        mMethod = method;
    }

    @Override
    public Object invokeMethod() throws IPCException {
        Exception exception;
        try {
            return mMethod.invoke(null, getParameters());
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new IPCException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking method " + mMethod + ".", exception);
    }

}
