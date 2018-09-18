# XIPC
[![xipc][xipcsvg]][xipc]  [![api][apisvg]][api]

一个Android通用的IPC(进程通信)框架

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)


## 如何使用

1.先在项目根目录的 build.gradle 的 repositories 添加:

```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在dependencies添加:

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys:XIPC:1.0.0'
}
```

3.最后在Application中注册接口服务:

```
XIPC.init(this);
XIPC.debug(BuildConfig.DEBUG);

//本地注册实现
XIPC.register(UserManager.class);
XIPC.register(LoadingTask.class);
XIPC.register(FileUtils.class);
XIPC.register(LoadingCallback.class);
XIPC.register(ComputeService.class);

//远程注册接口
//注册包名下的所有定义的服务接口
XIPC.register("com.xuexiang.remotedemo.service");

```









## 特别感谢
https://github.com/Xiaofei-it/Hermes

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![](https://github.com/xuexiangjys/XPage/blob/master/img/qq_group.jpg)

[xipcsvg]: https://img.shields.io/badge/XIPC-v1.0.0-brightgreen.svg
[xipc]: https://github.com/xuexiangjys/XIPC
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14