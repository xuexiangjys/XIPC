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

package com.xuexiang.xipcdemo.service.impl;

import com.xuexiang.xipc.annotation.ClassId;
import com.xuexiang.xipc.annotation.MethodId;
import com.xuexiang.xipcdemo.service.IComputeService;

/**
 * 服务接口测试
 * @author xuexiang
 * @since 2018/9/18 上午10:33
 */
@ClassId("ComputeService")
public class ComputeService implements IComputeService {

    @Override
    @MethodId("calculate")
    public float calculate(float value1, String symbol, float value2) {
        float result;
        switch(symbol) {
            case "+":
                result = value1 + value2;
                break;
            case "-":
                result = value1 - value2;
                break;
            case "*":
                result = value1 * value2;
                break;
            case "/":
                result = value1 / value2;
                break;
            default:
                result = value1 + value2;
                break;
        }
        return result;
    }
}
