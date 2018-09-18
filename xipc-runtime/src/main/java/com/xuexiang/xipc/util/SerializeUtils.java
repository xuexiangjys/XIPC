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

package com.xuexiang.xipc.util;

import com.google.gson.Gson;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;

/**
 * 序列化工具
 *
 * @author xuexiang
 * @since 2018/9/14 下午6:02
 */
public final class SerializeUtils {

    private static final Gson GSON = new Gson();

    private SerializeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 序列化编码
     *
     * @param object
     * @return
     * @throws IPCException
     */
    public static String encode(Object object) throws IPCException {
        if (object == null) {
            return null;
        } else {
            try {
                return GSON.toJson(object);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IPCException(ErrorCodes.GSON_ENCODE_EXCEPTION,
                    "Error occurs when Gson encodes Object "
                            + object + " to Json.");
        }
    }

    /**
     * 反序列化解码
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IPCException
     */
    public static <T> T decode(String data, Class<T> clazz) throws IPCException {
        try {
            return GSON.fromJson(data, clazz);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IPCException(ErrorCodes.GSON_DECODE_EXCEPTION,
                "Error occurs when Gson decodes data of the Class "
                        + clazz.getName());

    }

}
