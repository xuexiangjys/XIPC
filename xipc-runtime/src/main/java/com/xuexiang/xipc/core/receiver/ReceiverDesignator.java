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

package com.xuexiang.xipc.core.receiver;

import com.xuexiang.xipc.core.receiver.impl.InstanceCreatingReceiver;
import com.xuexiang.xipc.core.receiver.impl.InstanceGettingReceiver;
import com.xuexiang.xipc.core.receiver.impl.ObjectReceiver;
import com.xuexiang.xipc.core.receiver.impl.UtilityGettingReceiver;
import com.xuexiang.xipc.core.receiver.impl.UtilityReceiver;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;

public class ReceiverDesignator {

    public static Receiver getReceiver(ObjectWrapper objectWrapper) throws IPCException {
        int type = objectWrapper.getType();
        switch (type) {
            case ObjectWrapper.TYPE_OBJECT_TO_NEW:
                return new InstanceCreatingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT_TO_GET:
                return new InstanceGettingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS:
                return new UtilityReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT:
                return new ObjectReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS_TO_GET:
                return new UtilityGettingReceiver(objectWrapper);
            default:
                throw new IPCException(ErrorCodes.ILLEGAL_PARAMETER_EXCEPTION,
                        "Type " + type + " is not supported.");
        }
    }
}
