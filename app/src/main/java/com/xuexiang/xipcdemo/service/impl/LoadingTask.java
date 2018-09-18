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
import com.xuexiang.xipcdemo.service.ILoadingTask;
import com.xuexiang.xipcdemo.service.LoadingCallback;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 测试接口回调、服务接口
 *
 * @author xuexiang
 * @since 2018/9/18 上午9:50
 */
@ClassId("LoadingTask")
public class LoadingTask implements ILoadingTask {

    public LoadingTask(String url) {

    }

    @MethodId("start")
    @Override
    public void start(final LoadingCallback loadingCallback) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int time = 0;
            @Override
            public void run() {
                time += 10;
                if (time > 100) {
                    time = 100;
                }
                loadingCallback.callback(time);
                if (time == 100) {
                    timer.cancel();
                }
            }
        }, 0, 100);
    }
}
