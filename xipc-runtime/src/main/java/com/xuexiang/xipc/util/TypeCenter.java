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

import android.text.TextUtils;

import com.xuexiang.xipc.annotation.ClassName;
import com.xuexiang.xipc.annotation.MethodName;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.core.wrapper.BaseWrapper;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型存储仓库，存放对应类型的映射
 *
 * @author xuexiang
 * @since 2018/9/17 上午11:40
 */
public class TypeCenter {

    private static volatile TypeCenter sInstance = null;

    /**
     * 使用注解修饰的类
     */
    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses;

    /**
     * 没有使用注解修饰的类
     */
    private final ConcurrentHashMap<String, Class<?>> mRawClasses;

    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mAnnotatedMethods;

    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

    private TypeCenter() {
        mAnnotatedClasses = new ConcurrentHashMap<>();
        mRawClasses = new ConcurrentHashMap<>();
        mAnnotatedMethods = new ConcurrentHashMap<>();
        mRawMethods = new ConcurrentHashMap<>();
    }

    public static TypeCenter getInstance() {
        if (sInstance == null) {
            synchronized (TypeCenter.class) {
                if (sInstance == null) {
                    sInstance = new TypeCenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 注册类
     *
     * @param clazz
     */
    public void register(Class<?> clazz) {
        TypeUtils.validateClass(clazz); //校验类是否符合规范
        registerClass(clazz); //注册类
        registerMethod(clazz); //注册类的方法
    }

    /**
     * 注册类
     *
     * @param clazz
     */
    private void registerClass(Class<?> clazz) {
        ClassName classId = clazz.getAnnotation(ClassName.class);
        if (classId == null) {
            String className = clazz.getName();
            mRawClasses.putIfAbsent(className, clazz);
        } else {
            String className = classId.value();
            mAnnotatedClasses.putIfAbsent(className, clazz);
        }
    }

    /**
     * 注册类的方法
     *
     * @param clazz
     */
    private void registerMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            MethodName methodName = method.getAnnotation(MethodName.class);
            if (methodName == null) {
                mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
                ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);
                String key = TypeUtils.getMethodId(method);
                map.putIfAbsent(key, method);
            } else {
                mAnnotatedMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
                ConcurrentHashMap<String, Method> map = mAnnotatedMethods.get(clazz);
                String key = TypeUtils.getMethodId(method);
                map.putIfAbsent(key, method);
            }
        }
    }

    //======================根据映射、注册信息获取方法、类型=============================//

    /**
     * 根据包装方法中的参数、返回值、方法名的映射，获取注册的方法
     *
     * @param clazz         注册类
     * @param methodWrapper 包装方法
     * @return
     * @throws IPCException
     */
    public Method getMethod(Class<?> clazz, MethodWrapper methodWrapper) throws IPCException {
        String name = methodWrapper.getName();
        if (methodWrapper.isName()) { //非注解修饰的方法
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> methods = mRawMethods.get(clazz);
            Method method = methods.get(name);
            if (method != null) {
                TypeUtils.methodReturnTypeMatch(method, methodWrapper); //匹配两个方法是否相等
                return method;
            }
            int pos = name.indexOf('(');
            //遍历类中的方法，查找合适的方法
            method = TypeUtils.getMethod(clazz, name.substring(0, pos), getClassTypes(methodWrapper.getParameterTypes()), getClassType(methodWrapper.getReturnType()));
            if (method == null) {
                throw new IPCException(ErrorCodes.METHOD_NOT_FOUND,
                        "Method not found: " + name + " in class " + clazz.getName());
            }
            methods.put(name, method);
            return method;
        } else { //注解修饰的方法
            ConcurrentHashMap<String, Method> methods = mAnnotatedMethods.get(clazz);
            Method method = methods.get(name);
            if (method != null) {
                TypeUtils.methodMatch(method, methodWrapper);
                return method;
            }
            throw new IPCException(ErrorCodes.METHOD_NOT_FOUND,
                    "Method not found in class " + clazz.getName() + ". Method id = " + name + ". "
                            + "Please add the same annotation on the corresponding method in the remote process.");
        }
    }

    /**
     * 根据包装类中的类映射，获取注册类的类型
     *
     * @param wrapper
     * @return
     * @throws IPCException
     */
    public Class<?> getClassType(BaseWrapper wrapper) throws IPCException {
        String name = wrapper.getName();
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (wrapper.isName()) { //使用类名的映射
            Class<?> clazz = mRawClasses.get(name);
            if (clazz != null) {
                return clazz;
            }
            //boolean, byte, char, short, int, long, float, and double void
            switch (name) {
                case "boolean":
                    clazz = boolean.class;
                    break;
                case "byte":
                    clazz = byte.class;
                    break;
                case "char":
                    clazz = char.class;
                    break;
                case "short":
                    clazz = short.class;
                    break;
                case "int":
                    clazz = int.class;
                    break;
                case "long":
                    clazz = long.class;
                    break;
                case "float":
                    clazz = float.class;
                    break;
                case "double":
                    clazz = double.class;
                    break;
                case "void":
                    clazz = void.class;
                    break;
                default:
                    try {
                        clazz = Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new IPCException(ErrorCodes.CLASS_NOT_FOUND,
                                "Cannot find class " + name + ". Classes without ClassName annotation on it "
                                        + "should be located at the same package and have the same name, "
                                        + "EVEN IF the source code has been obfuscated by Proguard.");
                    }

                    break;
            }
            mRawClasses.putIfAbsent(name, clazz);
            return clazz;
        } else { //使用注解修饰的映射
            Class<?> clazz = mAnnotatedClasses.get(name);
            if (clazz == null) {
                throw new IPCException(ErrorCodes.CLASS_NOT_FOUND,
                        "Cannot find class with ClassName annotation on it. ClassName = " + name
                                + ". Please add the same annotation on the corresponding class in the remote process"
                                + " and register it. Have you forgotten to register the class?");
            }
            return clazz;
        }
    }

    /**
     * 根据包装类集合中的类映射，获取注册类的类型集合
     *
     * @param wrappers
     * @return
     * @throws IPCException
     */
    public Class<?>[] getClassTypes(BaseWrapper[] wrappers) throws IPCException {
        Class<?>[] classes = new Class<?>[wrappers.length];
        for (int i = 0; i < wrappers.length; ++i) {
            classes[i] = getClassType(wrappers[i]);
        }
        return classes;
    }


}
