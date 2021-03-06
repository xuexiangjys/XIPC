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

package com.xuexiang.xipc.core.sender;

import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;

import java.lang.reflect.Method;

/**
 * 注册方法的请求
 *
 * @author xuexiang
 * @since 2018/9/17 下午2:58
 */
public interface ISender {

    /**
     * 获取请求方法的包装器
     *
     * @param method
     * @param parameterWrappers
     * @return
     * @throws IPCException
     */
    MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws IPCException;
}
