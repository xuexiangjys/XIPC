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
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.util.TypeUtils;

/**
 * @author xuexiang
 * @since 2018/9/17 下午6:51
 */
public class UtilityGettingReceiver extends Receiver {

    public UtilityGettingReceiver(ObjectWrapper objectWrapper) throws IPCException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) {

    }

    @Override
    public Object invokeMethod() {
        return null;
    }

}
