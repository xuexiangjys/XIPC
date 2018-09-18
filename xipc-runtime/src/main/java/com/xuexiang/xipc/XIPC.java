package com.xuexiang.xipc;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xuexiang.xipc.core.channel.Channel;
import com.xuexiang.xipc.core.channel.IPCInvocationHandler;
import com.xuexiang.xipc.core.channel.IPCListener;
import com.xuexiang.xipc.core.channel.IPCService;
import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.sender.Sender;
import com.xuexiang.xipc.core.sender.impl.SenderDesignator;
import com.xuexiang.xipc.core.wrapper.ObjectWrapper;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.logs.IPCLog;
import com.xuexiang.xipc.util.ClassUtils;
import com.xuexiang.xipc.util.IPCGc;
import com.xuexiang.xipc.util.TypeCenter;
import com.xuexiang.xipc.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * XIPC进程通信框架 API入口
 *
 * @author xuexiang
 * @since 2018/9/14 下午3:27
 */
public class XIPC {

    private static Context sContext;

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private static final Channel CHANNEL = Channel.getInstance();

    private static final IPCGc IPC_GC = IPCGc.getInstance();

    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    public static void init(Context context) {
        if (sContext != null) {
            return;
        }
        sContext = context.getApplicationContext();
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new IllegalStateException("XIPC has not been initialized.");
        }
    }

    //====================1.类注册===========================//

    /**
     * 注册指定包名下的所有类[只能注册服务接口，不能注册实现类，否则报错]
     *
     * @param packageName 包名
     */
    public static void register(final String packageName) {
        try {
            Set<String> classNames = ClassUtils.getClassNameByPackageName(getContext(), packageName);
            for (String className : classNames) {
                registerByClassName(className);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据类名注册
     *
     * @param className
     */
    public static void registerByClassName(String className) {
        try {
            register(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            IPCLog.e("[XIPC] register failed! className:" + className, e);
        }
    }

    /**
     * 根据对象注册
     *
     * @param object
     */
    public static void register(Object object) {
        register(object.getClass());
    }

    /**
     * 根据类注册
     * 没有必要在本地进程中注册类！
     * <p>
     * 但是，如果返回的方法类型与方法的返回类型不完全相同，则应该注册该方法。
     *
     * @param clazz
     */
    public static void register(Class<?> clazz) {
        testInitialize();
        TYPE_CENTER.register(clazz);
    }

    //====================2.建立连接===========================//

    /**
     * 本地绑定连接【应用内部不同进程】
     *
     * @param context
     */
    public static void connect(Context context) {
        connectApp(context, null, IPCService.IPCService0.class);
    }

    /**
     * 本地绑定连接【应用内部不同进程】
     *
     * @param context
     * @param service
     */
    public static void connect(Context context, Class<? extends IPCService> service) {
        connectApp(context, null, service);
    }

    /**
     * 远程绑定连接【跨应用不同进程】
     *
     * @param context
     * @param packageName
     */
    public static void connectApp(Context context, String packageName) {
        connectApp(context, packageName, IPCService.IPCService0.class);
    }

    /**
     * 远程绑定连接【跨应用不同进程】
     *
     * @param context
     * @param packageName
     * @param service
     */
    public static void connectApp(Context context, String packageName, Class<? extends IPCService> service) {
        init(context);
        CHANNEL.bind(context.getApplicationContext(), packageName, service);
    }

    public static void disconnect(Context context) {
        disconnect(context, IPCService.IPCService0.class);
    }

    public static void disconnect(Context context, Class<? extends IPCService> service) {
        CHANNEL.unbind(context.getApplicationContext(), service);
    }

    public static boolean isConnected() {
        return isConnected(IPCService.IPCService0.class);
    }

    public static boolean isConnected(Class<? extends IPCService> service) {
        return CHANNEL.isConnected(service);
    }

    //====================3.服务发现===========================//

    private static <T> T getProxy(Class<? extends IPCService> service, ObjectWrapper object) {
        Class<?> clazz = object.getObjectClass();
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new IPCInvocationHandler(service, object));
        IPC_GC.register(service, proxy, object.getTimeStamp());
        return proxy;
    }

    private static void checkBound(Class<? extends IPCService> service) {
        if (!CHANNEL.getBound(service)) {
            throw new IllegalStateException("Service Unavailable: You have not connected the service "
                    + "or the connection is not completed. You can set IPCListener to receive a callback "
                    + "when the connection is completed.");
        }
    }

    //=========新建实例，服务发现==========//

    /**
     * 服务发现【每次都是新建】
     *
     * @param clazz      注册类
     * @param parameters 构造参数
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<T> clazz, Object... parameters) {
        return getService(IPCService.IPCService0.class, clazz, parameters);
    }

    /**
     * 服务发现【每次都是新建】
     *
     * @param service
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<? extends IPCService> service, Class<T> clazz, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_NEW);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_NEW_INSTANCE, object);
        try {
            Reply reply = sender.send(null, parameters);
            if (reply != null && !reply.success()) {
                IPCLog.e("[XIPC] Error occurs during creating instance. Error code: " + reply.getErrorCode());
                IPCLog.e("[XIPC] Error message: " + reply.getMessage());
                return null;
            }
        } catch (IPCException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    //=========获取单例==========//

    public static <T> T getInstanceInService(Class<? extends IPCService> service, Class<T> clazz, Object... parameters) {
        return getInstanceWithMethodNameInService(service, clazz, "", parameters);
    }

    /**
     * 获取单例
     *
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> clazz, Object... parameters) {
        return getInstanceInService(IPCService.IPCService0.class, clazz, parameters);
    }

    public static <T> T getInstanceWithMethodName(Class<T> clazz, String methodName, Object... parameters) {
        return getInstanceWithMethodNameInService(IPCService.IPCService0.class, clazz, methodName, parameters);
    }

    public static <T> T getInstanceWithMethodNameInService(Class<? extends IPCService> service, Class<T> clazz, String methodName, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_INSTANCE, object);
        if (parameters == null) {
            parameters = new Object[0];
        }
        int length = parameters.length;
        Object[] tmp = new Object[length + 1];
        tmp[0] = methodName;
        for (int i = 0; i < length; ++i) {
            tmp[i + 1] = parameters[i];
        }
        try {
            Reply reply = sender.send(null, tmp);
            if (reply != null && !reply.success()) {
                IPCLog.e("[XIPC] Error occurs during getting instance. Error code: " + reply.getErrorCode());
                IPCLog.e("[XIPC] Error message: " + reply.getMessage());
                return null;
            }
        } catch (IPCException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    //=========获取工具类==========//

    /**
     * 获取工具类
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getUtilityClass(Class<T> clazz) {
        return getUtilityClassInService(IPCService.IPCService0.class, clazz);
    }

    public static <T> T getUtilityClassInService(Class<? extends IPCService> service, Class<T> clazz) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_CLASS_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_UTILITY_CLASS, object);
        try {
            Reply reply = sender.send(null, null);
            if (reply != null && !reply.success()) {
                IPCLog.e("[XIPC] Error occurs during getting utility class. Error code: " + reply.getErrorCode());
                IPCLog.e("[XIPC] Error message: " + reply.getMessage());
                return null;
            }
        } catch (IPCException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_CLASS);
        return getProxy(service, object);
    }


    //====================日志===========================//

    /**
     * 设置是否打开调试
     *
     * @param isDebug
     */
    public static void debug(boolean isDebug) {
        IPCLog.debug(isDebug);
    }

    /**
     * 设置调试模式
     *
     * @param tag
     */
    public static void debug(String tag) {
        IPCLog.debug(tag);
    }

    /**
     * @return 是否是调试模式
     */
    public static boolean isDebug() {
        return IPCLog.isDebug();
    }

    /**
     * 设置IPC通信监听
     *
     * @param listener
     */
    public void setIPCListener(IPCListener listener) {
        CHANNEL.setIPCListener(listener);
    }


}
