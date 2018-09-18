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
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;

public class SenderDesignator {

    public static final int TYPE_NEW_INSTANCE = 0;

    public static final int TYPE_GET_INSTANCE = 1;

    public static final int TYPE_GET_UTILITY_CLASS = 2;

    public static final int TYPE_INVOKE_METHOD = 3;

    public static Sender getPostOffice(Class<? extends IPCService> service, int type, ObjectWrapper object) {
        switch (type) {
            case TYPE_NEW_INSTANCE:
                return new InstanceCreatingSender(service, object);
            case TYPE_GET_INSTANCE:
                return new InstanceGettingSender(service, object);
            case TYPE_GET_UTILITY_CLASS:
                return new UtilityGettingSender(service, object);
            case TYPE_INVOKE_METHOD:
                return new ObjectSender(service, object);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

}
