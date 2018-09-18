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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 处理实例的创建
 *
 * @author xuexiang
 * @since 2018/9/18 下午3:22
 */
public class InstanceCreatingReceiver extends Receiver {

    private Class<?> mObjectClass;

    private Constructor<?> mConstructor;

    public InstanceCreatingReceiver(ObjectWrapper object) throws IPCException {
        super(object);
        Class<?> clazz = TYPE_CENTER.getClassType(object);
        TypeUtils.validateAccessible(clazz);
        mObjectClass = clazz;
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws IPCException {
        Constructor<?> constructor = TypeUtils.getConstructor(mObjectClass, TYPE_CENTER.getClassTypes(parameterWrappers));
        TypeUtils.validateAccessible(constructor);
        mConstructor = constructor;
    }

    @Override
    public Object invokeMethod() throws IPCException {
        Exception exception;
        try {
            Object object;
            Object[] parameters = getParameters();
            if (parameters == null) {
                object = mConstructor.newInstance();
            } else {
                object = mConstructor.newInstance(parameters);
            }
            OBJECT_CENTER.putObject(getObjectTimeStamp(), object);
            return null;
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new IPCException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking constructor to create an instance of "
                        + mObjectClass.getName(), exception);
    }
}
