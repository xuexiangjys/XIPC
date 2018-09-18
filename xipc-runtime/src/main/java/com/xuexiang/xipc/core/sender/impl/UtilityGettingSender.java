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

import java.lang.reflect.Method;

public class UtilityGettingSender extends Sender {

    public UtilityGettingSender(Class<? extends IPCService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    public MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) {
        return null;
    }
}
