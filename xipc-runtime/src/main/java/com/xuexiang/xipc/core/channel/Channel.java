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

package com.xuexiang.xipc.core.channel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.xuexiang.xipc.core.entity.CallbackMail;
import com.xuexiang.xipc.core.entity.Mail;
import com.xuexiang.xipc.core.entity.Reply;
import com.xuexiang.xipc.core.wrapper.ParameterWrapper;
import com.xuexiang.xipc.exception.ErrorCodes;
import com.xuexiang.xipc.exception.IPCException;
import com.xuexiang.xipc.logs.IPCLog;
import com.xuexiang.xipc.util.CallbackManager;
import com.xuexiang.xipc.util.SerializeUtils;
import com.xuexiang.xipc.util.TypeCenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC通信通道，持有服务的连接
 *
 * @author xuexiang
 * @since 2018/9/17 下午6:16
 */
public class Channel {

    private static volatile Channel sInstance = null;

    /**
     * IPC通信服务
     */
    private final ConcurrentHashMap<Class<? extends IPCService>, IIPCService> mIPCServices = new ConcurrentHashMap<>();
    /**
     * IPC通信服务连接池
     */
    private final ConcurrentHashMap<Class<? extends IPCService>, IPCServiceConnection> mIPCServiceConnections = new ConcurrentHashMap<>();
    /**
     * 绑定中的服务
     */
    private final ConcurrentHashMap<Class<? extends IPCService>, Boolean> mBindings = new ConcurrentHashMap<>();
    /**
     * 已绑定的服务
     */
    private final ConcurrentHashMap<Class<? extends IPCService>, Boolean> mBounds = new ConcurrentHashMap<>();
    /**
     * IPC通信监听
     */
    private IPCListener mListener = null;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private static final CallbackManager CALLBACK_MANAGER = CallbackManager.getInstance();

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private IIPCServiceCallback mIPCServiceCallback = new IIPCServiceCallback.Stub() {

        private Object[] getParameters(ParameterWrapper[] parameterWrappers) throws IPCException {
            if (parameterWrappers == null) {
                parameterWrappers = new ParameterWrapper[0];
            }
            int length = parameterWrappers.length;
            Object[] result = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    result[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);

                    String data = parameterWrapper.getData();
                    if (data == null) {
                        result[i] = null;
                    } else {
                        result[i] = SerializeUtils.decode(data, clazz);
                    }
                }
            }
            return result;
        }

        public Reply callback(CallbackMail mail) {
            final Pair<Boolean, Object> pair = CALLBACK_MANAGER.getCallback(mail.getTimeStamp(), mail.getIndex());
            if (pair == null) {
                return null;
            }
            final Object callback = pair.second;
            if (callback == null) {
                return new Reply(ErrorCodes.CALLBACK_NOT_ALIVE, "");
            }
            boolean uiThread = pair.first;
            try {
                // TODO Currently, the callback should not be annotated!
                final Method method = TYPE_CENTER.getMethod(callback.getClass(), mail.getMethod());
                final Object[] parameters = getParameters(mail.getParameters());
                Object result = null;
                Exception exception = null;
                if (uiThread) {
                    boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                    if (isMainThread) {
                        try {
                            result = method.invoke(callback, parameters);
                        } catch (IllegalAccessException e) {
                            exception = e;
                        } catch (InvocationTargetException e) {
                            exception = e;
                        }
                    } else {
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.invoke(callback, parameters);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return null;
                    }
                } else {
                    try {
                        result = method.invoke(callback, parameters);
                    } catch (IllegalAccessException e) {
                        exception = e;
                    } catch (InvocationTargetException e) {
                        exception = e;
                    }
                }
                if (exception != null) {
                    exception.printStackTrace();
                    throw new IPCException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                            "Error occurs when invoking method " + method + " on " + callback, exception);
                }
                if (result == null) {
                    return null;
                }
                return new Reply(new ParameterWrapper(result));
            } catch (IPCException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getMessage());
            }
        }

        @Override
        public void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException {
            int size = timeStamps.size();
            for (int i = 0; i < size; ++i) {
                CALLBACK_MANAGER.removeCallback(timeStamps.get(i), indexes.get(i));
            }
        }
    };

    private Channel() {

    }

    public static Channel getInstance() {
        if (sInstance == null) {
            synchronized (Channel.class) {
                if (sInstance == null) {
                    sInstance = new Channel();
                }
            }
        }
        return sInstance;
    }

    /**
     * 绑定IPC通信服务
     *
     * @param context
     * @param packageName
     * @param service
     */
    public void bind(Context context, String packageName, Class<? extends IPCService> service) {
        IPCServiceConnection connection;
        synchronized (this) {
            if (getBound(service)) {
                return;
            }
            Boolean binding = mBindings.get(service);
            if (binding != null && binding) {
                return;
            }
            mBindings.put(service, true);
            connection = new IPCServiceConnection(service);
            mIPCServiceConnections.put(service, connection);
        }
        Intent intent;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑IPC通信服务
     *
     * @param context
     * @param service
     */
    public void unbind(Context context, Class<? extends IPCService> service) {
        synchronized (this) {
            Boolean bound = mBounds.get(service);
            if (bound != null && bound) {
                IPCServiceConnection connection = mIPCServiceConnections.get(service);
                if (connection != null) {
                    context.unbindService(connection);
                }
                mBounds.put(service, false);
            }
        }
    }

    /**
     * 执行IPC通信请求
     *
     * @param service
     * @param mail
     * @return
     */
    public Reply send(Class<? extends IPCService> service, Mail mail) {
        IIPCService iipcService = mIPCServices.get(service);
        try {
            if (iipcService == null) {
                return new Reply(ErrorCodes.SERVICE_UNAVAILABLE,
                        "Service Unavailable: Check whether you have connected XIPC.");
            }
            return iipcService.send(mail);
        } catch (RemoteException e) {
            return new Reply(ErrorCodes.REMOTE_EXCEPTION, "Remote Exception: Check whether "
                    + "the process you are communicating with is still alive.");
        }
    }

    /**
     * 资源回收
     *
     * @param service
     * @param timeStamps
     */
    public void gc(Class<? extends IPCService> service, List<Long> timeStamps) {
        IIPCService iipcService = mIPCServices.get(service);
        if (iipcService == null) {
            IPCLog.e("[Channel] Service Unavailable: Check whether you have disconnected the service before a process dies.");
        } else {
            try {
                iipcService.gc(timeStamps);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getBound(Class<? extends IPCService> service) {
        Boolean bound = mBounds.get(service);
        return bound != null && bound;
    }

    /**
     * 设置IPC通信监听
     * @param listener
     */
    public void setIPCListener(IPCListener listener) {
        mListener = listener;
    }

    public boolean isConnected(Class<? extends IPCService> service) {
        IIPCService iipcService = mIPCServices.get(service);
        return iipcService != null && iipcService.asBinder().pingBinder();
    }

    /**
     * IPC通信连接
     */
    private class IPCServiceConnection implements ServiceConnection {

        private Class<? extends IPCService> mClass;

        IPCServiceConnection(Class<? extends IPCService> service) {
            mClass = service;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            synchronized (Channel.this) {
                mBounds.put(mClass, true);
                mBindings.put(mClass, false);
                IIPCService iipcService = IIPCService.Stub.asInterface(service);
                mIPCServices.put(mClass, iipcService);
                try {
                    iipcService.register(mIPCServiceCallback, Process.myPid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    IPCLog.e("[Channel] Remote Exception: Check whether "
                            + "the process you are communicating with is still alive.");
                    return;
                }
            }
            if (mListener != null) {
                mListener.onIPCConnected(mClass);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (Channel.this) {
                mIPCServices.remove(mClass);
                mBounds.put(mClass, false);
                mBindings.put(mClass, false);
            }
            if (mListener != null) {
                mListener.onIPCDisconnected(mClass);
            }
        }
    }
}
