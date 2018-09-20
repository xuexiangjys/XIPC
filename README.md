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

## 注意事项

### 在接口注册方面

* 如果两个进程属于两个不同的app（分别叫App A和App B）。App A想访问App B的一个类，并且App A的接口和App B的对应实现类有`相同的包名和类名`，那么就没有必要在类和接口上加@ClassName注解。但是要注意使用ProGuard后类名和包名仍要保持一致。

* 如果接口和类里面对应的方法有相同的名字，那么也没有必要在方法上加上@MethodName注解，同样注意ProGuard的使用后接口内的方法名字必须仍然和类内的对应方法名字相同。

* 假设进程B需要访问进程A, 如果进程A使用了@ClassName注解标识的类，那么进程B也要对其对应的接口上加上相同的@ClassName注解，并且进程A在进程B访问该接口之前，必须要注册。 否则进程B使用XIPC.getService()、XIPC.getInstance()或XIPC.getUtilityClass()时，XIPC在进程A中找不到匹配的类。

总之为了防止出现各种各样不匹配或者找不到的问题，最好还是使用@ClassName和@MethodName注解，进行一一对应修饰并在Application的onCreate中进行注册。



## 混淆配置

```
# xipc
-keep @com.xuexiang.xipc.annotation.* class * {*;}
-keep class * {
    @com.xuexiang.xipc.annotation.* <fields>;
}
-keepclassmembers class * {
    @com.xuexiang.xipc.annotation.* <methods>;
}
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