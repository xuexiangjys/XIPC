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

package com.xuexiang.xipc.core.sender.impl;

import com.xuexiang.xipc.core.channel.IPCService;
import com.xuexiang.xipc.core.sender.Sender;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.SerializeUtils;

import java.lang.reflect.Method;

/**
 * 请求获取单例
 *
 * @author xuexiang
 * @since 2018/9/18 下午3:38
 */
public class InstanceGettingSender extends Sender {

    public InstanceGettingSender(Class<? extends IPCService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    protected void setParameterWrappers(ParameterWrapper[] parameterWrappers) {
        int length = parameterWrappers.length;
        ParameterWrapper[] tmp = new ParameterWrapper[length - 1];
        System.arraycopy(parameterWrappers, 1, tmp, 0, length - 1);
        super.setParameterWrappers(tmp);
    }

    @Override
    public MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws IPCException {
        ParameterWrapper parameterWrapper = parameterWrappers[0];
        String methodName;
        try {
            methodName = SerializeUtils.decode(parameterWrapper.getData(), String.class);
        } catch (IPCException e) {
            e.printStackTrace();
            throw new IPCException(ErrorCodes.GSON_DECODE_EXCEPTION,
                    "Error occurs when decoding the method name.");
        }
        int length = parameterWrappers.length;
        Class<?>[] parameterTypes = new Class[length - 1];
        for (int i = 1; i < length; ++i) {
            parameterWrapper = parameterWrappers[i];
            parameterTypes[i - 1] = parameterWrapper == null ? null : parameterWrapper.getClassType();
        }
        return new MethodWrapper(methodName, parameterTypes);
    }
}
