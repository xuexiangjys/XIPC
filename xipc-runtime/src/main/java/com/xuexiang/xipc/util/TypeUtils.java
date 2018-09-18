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

import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.xuexiang.xipc.annotation.ClassName;
import com.xuexiang.xipc.annotation.Singleton;
import com.xuexiang.xipc.annotation.MethodName;
import com.xuexiang.xipc.annotation.WithinProcess;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.core.wrapper.MethodWrapper;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

/**
 * 类型工具
 *
 * @author xuexiang
 * @since 2018/9/14 下午4:22
 */
public final class TypeUtils {

    private TypeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final HashSet<Class<?>> CONTEXT_CLASSES = new HashSet<Class<?>>() {
        {
            add(Context.class);
            add(Activity.class);
            add(AppCompatActivity.class);
            add(Application.class);
            add(FragmentActivity.class);
            add(IntentService.class);
            add(Service.class);
        }
    };

    /**
     * 获取类名
     *
     * @param clazz
     * @return
     */
    public static String getClassId(Class<?> clazz) {
        ClassName className = clazz.getAnnotation(ClassName.class);
        if (className != null) {
            return className.value();
        } else {
            return clazz.getName();
        }
    }

    /**
     * 获取方法名（映射key)
     *
     * @param method
     * @return
     */
    public static String getMethodId(Method method) {
        MethodName methodName = method.getAnnotation(MethodName.class);
        if (methodName != null) {
            return methodName.value();
        } else {
            return method.getName() + '(' + getMethodParameters(method.getParameterTypes()) + ')';
        }
    }

    /**
     * 获取类的名称
     *
     * @param clazz
     * @return
     */
    //boolean, byte, char, short, int, long, float, and double void
    private static String getClassName(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return "boolean";
        } else if (clazz == Byte.class) {
            return "byte";
        } else if (clazz == Character.class) {
            return "char";
        } else if (clazz == Short.class) {
            return "short";
        } else if (clazz == Integer.class) {
            return "int";
        } else if (clazz == Long.class) {
            return "long";
        } else if (clazz == Float.class) {
            return "float";
        } else if (clazz == Double.class) {
            return "double";
        } else if (clazz == Void.class) {
            return "void";
        } else {
            return clazz.getName();
        }
    }

    /**
     * 获取执行方法的参数
     *
     * @param classes
     * @return
     */
    public static String getMethodParameters(Class<?>[] classes) {
        StringBuilder result = new StringBuilder();
        int length = classes.length;
        if (length == 0) {
            return result.toString();
        }
        result.append(getClassName(classes[0]));
        for (int i = 1; i < length; ++i) {
            result.append(",").append(getClassName(classes[i]));
        }
        return result.toString();
    }

    /**
     * 遍历类的所有方法，根据方法名、参数类型、返回值类型来找到相匹配的方法（遍历找方法）
     *
     * @param clazz          目标类
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param returnType     返回值类型
     * @return
     * @throws IPCException
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Class<?> returnType)
            throws IPCException {
        Method result = null;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && classAssignable(method.getParameterTypes(), parameterTypes)) {
                if (result == null) {
                    result = method;
                } else {
                    throw new IPCException(ErrorCodes.TOO_MANY_MATCHING_METHODS,
                            "There are more than one method named "
                                    + methodName + " of the class " + clazz.getName()
                                    + " matching the parameters!");
                }
            }
        }
        if (result == null) return null;

        if (result.getReturnType() != returnType) {
            throw new IPCException(ErrorCodes.METHOD_RETURN_TYPE_NOT_MATCHING,
                    "The method named " + methodName + " of the class " + clazz.getName()
                            + " matches the parameter types but not the return type. The return type is "
                            + result.getReturnType().getName() + " but the required type is "
                            + returnType.getName() + ". The method in the local interface must exactly "
                            + "match the method in the remote class.");
        }
        return result;
    }

    /**
     * 遍历类的所有方法，根据方法名、参数类型来找到获取单例的方法（遍历找获取单例的方法）
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws IPCException
     */
    public static Method getMethodForGettingInstance(Class<?> clazz, String methodName, Class<?>[] parameterTypes)
            throws IPCException {
        Method[] methods = clazz.getMethods();
        Method result = null;
        for (Method method : methods) {
            String tmpName = method.getName();
            if (methodName.equals("") && (tmpName.equals("getInstance")
                    || method.isAnnotationPresent(Singleton.class))
                    || !methodName.equals("") && tmpName.equals(methodName)) {
                if (classAssignable(method.getParameterTypes(), parameterTypes)) {
                    if (result == null) {
                        result = method;
                    } else {
                        throw new IPCException(ErrorCodes.TOO_MANY_MATCHING_METHODS_FOR_GETTING_INSTANCE,
                                "When getting instance, there are more than one method named "
                                        + methodName + " of the class " + clazz.getName()
                                        + " matching the parameters!");
                    }
                }
            }
        }
        if (result != null) {
            if (result.getReturnType() != clazz) {
                throw new IPCException(ErrorCodes.GETTING_INSTANCE_RETURN_TYPE_ERROR,
                        "When getting instance, the method named " + methodName + " of the class " + clazz.getName()
                                + " matches the parameter types but not the return type. The return type is "
                                + result.getReturnType().getName() + " but the required type is "
                                + clazz.getName() + ".");
            }
            return result;
        }
        throw new IPCException(ErrorCodes.GETTING_INSTANCE_METHOD_NOT_FOUND,
                "When getting instance, the method named " + methodName + " of the class "
                        + clazz.getName() + " is not found. The class must have a method for getting instance.");
    }

    /**
     * 遍历类的所有构造方法，根据参数类型来找到相匹配的构造方法（遍历找构造方法）
     *
     * @param clazz
     * @param parameterTypes
     * @return
     * @throws IPCException
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] parameterTypes) throws IPCException {
        Constructor<?> result = null;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (classAssignable(constructor.getParameterTypes(), parameterTypes)) {
                if (result != null) {
                    throw new IPCException(ErrorCodes.TOO_MANY_MATCHING_CONSTRUCTORS_FOR_CREATING_INSTANCE,
                            "The class " + clazz.getName() + " has too many constructors whose "
                                    + " parameter types match the required types.");
                } else {
                    result = constructor;
                }
            }
        }
        if (result == null) {
            throw new IPCException(ErrorCodes.CONSTRUCTOR_NOT_FOUND,
                    "The class " + clazz.getName() + " do not have a constructor whose "
                            + " parameter types match the required types.");
        }
        return result;
    }

    /**
     * 将对象转化为参数包装类进行包装
     *
     * @param objects 需要包装的对象集合
     * @return
     * @throws IPCException
     */
    public static ParameterWrapper[] objectToWrapper(Object[] objects) throws IPCException {
        if (objects == null) {
            objects = new Object[0];
        }
        int length = objects.length;
        ParameterWrapper[] parameterWrappers = new ParameterWrapper[length];
        for (int i = 0; i < length; ++i) {
            try {
                parameterWrappers[i] = new ParameterWrapper(objects[i]);
            } catch (IPCException e) {
                e.printStackTrace();
                throw new IPCException(e.getErrorCode(),
                        "Error happens at parameter encoding, and parameter index is "
                                + i + ". See the stack trace for more information.",
                        e);
            }
        }
        return parameterWrappers;
    }

    //=========================规范校验==========================//

    /**
     * 注册前需要校验注册接口类型是否符合规范<br>
     * 1.不能是被@WithinProcess注解的类<br>
     * 2.不能是匿名内部类<br>
     * 3.不能是局部内部类<br>
     * 4.不能是抽象类<br>
     *
     * @param clazz
     */
    public static void validateClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class object is null.");
        }
        if (clazz.isPrimitive() || clazz.isInterface()) {
            return;
        }
        if (clazz.isAnnotationPresent(WithinProcess.class)) {
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Class with a WithinProcess annotation presented on it cannot be accessed"
                            + " from outside the process.");
        }

        if (clazz.isAnonymousClass()) { //是否是匿名内部类
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Anonymous class cannot be accessed from outside the process.");
        }
        if (clazz.isLocalClass()) { //是否是局部内部类
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Local class cannot be accessed from outside the process.");
        }
        if (Context.class.isAssignableFrom(clazz)) {
            return;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException(
                    "Error occurs when registering class " + clazz.getName()
                            + ". Abstract class cannot be accessed from outside the process.");
        }
    }

    /**
     * 校验注册的服务的接口是否符合规范
     *
     * @param clazz
     */
    public static void validateServiceInterface(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class object is null.");
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Only interfaces can be passed as the parameters.");
        }
    }

    /**
     * 判断修饰的注解集合中有没有指定的注解修饰
     *
     * @param annotations
     * @param annotationClass 指定的注解类
     * @return
     */
    public static boolean arrayContainsAnnotation(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        if (annotations == null || annotationClass == null) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotationClass.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到最接近的Context类
     *
     * @param clazz
     * @return
     */
    public static Class<?> getContextClass(Class<?> clazz) {
        for (Class<?> tmp = clazz; tmp != Object.class; tmp = tmp.getSuperclass()) {
            if (CONTEXT_CLASSES.contains(tmp)) {
                return tmp;
            }
        }
        throw new IllegalArgumentException("can not find context class!");
    }

    /**
     * 判断类是否可访问（使用@WithinProcess修饰的类、方法都不可访问）
     *
     * @param clazz
     * @throws IPCException
     */
    public static void validateAccessible(Class<?> clazz) throws IPCException {
        if (clazz.isAnnotationPresent(WithinProcess.class)) {
            throw new IPCException(ErrorCodes.CLASS_WITH_PROCESS,
                    "Class " + clazz.getName() + " has a WithProcess annotation on it, "
                            + "so it cannot be accessed from outside the process.");
        }
    }

    /**
     * 判断类是否可访问（使用@WithinProcess修饰的类、方法都不可访问）
     *
     * @param method
     * @throws IPCException
     */
    public static void validateAccessible(Method method) throws IPCException {
        if (method.isAnnotationPresent(WithinProcess.class)) {
            throw new IPCException(ErrorCodes.METHOD_WITH_PROCESS,
                    "Method " + method.getName() + " of class " + method.getDeclaringClass().getName()
                            + " has a WithProcess annotation on it, so it cannot be accessed from "
                            + "outside the process.");
        }
    }

    /**
     * 判断类是否可访问（使用@WithinProcess修饰的类、方法都不可访问）
     *
     * @param constructor
     * @throws IPCException
     */
    public static void validateAccessible(Constructor<?> constructor) throws IPCException {
        if (constructor.isAnnotationPresent(WithinProcess.class)) {
            throw new IPCException(ErrorCodes.METHOD_WITH_PROCESS,
                    "Constructor " + constructor.getName() + " of class " + constructor.getDeclaringClass().getName()
                            + " has a WithProcess annotation on it, so it cannot be accessed from "
                            + "outside the process.");
        }
    }

    //=======================方法对比匹配========================//

    /**
     * 方法参数的类型匹配
     *
     * @param method 目标方法
     * @param methodWrapper 包装方法
     * @throws IPCException
     */
    public static void methodParameterTypeMatch(Method method, MethodWrapper methodWrapper) throws IPCException {
        Class<?>[] requiredParameterTypes = TypeCenter.getInstance().getClassTypes(methodWrapper.getParameterTypes());
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (requiredParameterTypes.length != parameterTypes.length) {  //参数不相等
            throw new IPCException(ErrorCodes.METHOD_PARAMETER_NOT_MATCHING,
                    "The number of method parameters do not match. "
                            + "Method " + method + " has " + parameterTypes.length + " parameters. "
                            + "The required method has " + requiredParameterTypes.length + " parameters.");
        }
        int length = requiredParameterTypes.length;
        for (int i = 0; i < length; ++i) {
            if (requiredParameterTypes[i].isPrimitive() || parameterTypes[i].isPrimitive()) { //两个中是基础类型
                if (!primitiveMatch(requiredParameterTypes[i], parameterTypes[i])) {
                    throw new IPCException(ErrorCodes.METHOD_PARAMETER_NOT_MATCHING,
                            "The parameter type of method " + method + " do not match at index " + i + ".");
                }
            } else if (requiredParameterTypes[i] != parameterTypes[i]) {
                if (!primitiveMatch(requiredParameterTypes[i], parameterTypes[i])) {
                    throw new IPCException(ErrorCodes.METHOD_PARAMETER_NOT_MATCHING,
                            "The parameter type of method " + method + " do not match at index " + i + ".");
                }
            }
        }
    }

    /**
     * 方法的返回值类型匹配
     *
     * @param method 目标方法
     * @param methodWrapper 包装方法
     * @throws IPCException
     */
    public static void methodReturnTypeMatch(Method method, MethodWrapper methodWrapper) throws IPCException {
        Class<?> returnType = method.getReturnType();
        Class<?> requiredReturnType = TypeCenter.getInstance().getClassType(methodWrapper.getReturnType());
        if (returnType.isPrimitive() || requiredReturnType.isPrimitive()) {
            if (!primitiveMatch(returnType, requiredReturnType)) {
                throw new IPCException(ErrorCodes.METHOD_RETURN_TYPE_NOT_MATCHING,
                        "The return type of methods do not match. "
                                + "Method " + method + " return type: " + returnType.getName()
                                + ". The required is " + requiredReturnType.getName());
            }
        } else if (requiredReturnType != returnType) {
            if (!primitiveMatch(returnType, requiredReturnType)) {
                throw new IPCException(ErrorCodes.METHOD_RETURN_TYPE_NOT_MATCHING,
                        "The return type of methods do not match. "
                                + "Method " + method + " return type: " + returnType.getName()
                                + ". The required is " + requiredReturnType.getName());
            }
        }
    }

    public static void methodMatch(Method method, MethodWrapper methodWrapper) throws IPCException {
        methodParameterTypeMatch(method, methodWrapper);
        methodReturnTypeMatch(method, methodWrapper);
    }

    /**
     * 比较两个类是否相等（包括基础类型）
     *
     * @param class1
     * @param class2
     * @return
     */
    public static boolean primitiveMatch(Class<?> class1, Class<?> class2) {
        if (!class1.isPrimitive() && !class2.isPrimitive()) {
            return false;
        } else if (class1 == class2) {
            return true;
        } else if (class1.isPrimitive()) {
            return primitiveMatch(class2, class1);
            //class2 is primitive
            //boolean, byte, char, short, int, long, float, and double void
        } else
            return class1 == Boolean.class && class2 == boolean.class
                    || class1 == Byte.class && class2 == byte.class
                    || class1 == Character.class && class2 == char.class
                    || class1 == Short.class && class2 == short.class
                    || class1 == Integer.class && class2 == int.class
                    || class1 == Long.class && class2 == long.class
                    || class1 == Float.class && class2 == float.class
                    || class1 == Double.class && class2 == double.class
                    || class1 == Void.class && class2 == void.class;
    }

    /**
     * 判断 classes1类集合是否是classes2类集合的父类或相等。(就是class2的类能否作为class1的参数类型）
     *
     * @param classes1
     * @param classes2
     * @return
     */
    public static boolean classAssignable(Class<?>[] classes1, Class<?>[] classes2) {
        if (classes1.length != classes2.length) {
            return false;
        }
        int length = classes2.length;
        for (int i = 0; i < length; ++i) {
            if (classes2[i] == null) {
                continue;
            }
            if (primitiveMatch(classes1[i], classes2[i])) {
                continue;
            }
            if (!classes1[i].isAssignableFrom(classes2[i])) {
                return false;
            }
        }
        return true;
    }

}
