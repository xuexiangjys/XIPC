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

//本地只需要注册实现，无需注册接口
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

* 假设进程B需要访问进程A, 如果进程A使用了@ClassName注解标识的类，那么进程B也要对其对应的接口上加上相同的@ClassName注解，并且进程A在进程B访问该接口之前，必须要注册。 否则进程B使用`XIPC.getService()`、`XIPC.getInstance()`或`XIPC.getUtilityClass()`访问进程A时，XIPC在进程A中找不到匹配的类。

* 所有注册的接口类不可以是匿名类和局部类。

总之为了防止出现各种各样不匹配或者找不到的问题，最好还是使用@ClassName和@MethodName注解，进行一一对应修饰并在Application的onCreate中进行注册。

### 在接口定义方面

* 如果你不想让一个类或者函数被其他进程访问，可以在上面加上@WithinProcess注解。

* 使用XIPC跨进程调用函数的时候，传入参数的类型可以是原参数类型的子类，千万注意不可以是匿名类和局部类，但是回调函数例外。

* 在接口的参数方面，如果被调用的接口函数的参数类型和返回值类型是int、double等基本类型或者String、Object这样的Java通用类型无需多余操作。但是千万注意，这里目前不支持参数的类型是数组。如果需要用到数组作为参数，可以使用自定义对象去包一下数组，再进行使用。

* 对于接口参数类型是自定义的类，并且两个进程分别属于两个不同app，那么你必须在两个app中都定义这个类，且必须保证代码混淆后，两个类仍然有相同的包名和类名。不过你可以适用@ClassName和@MethodName注解，这样包名和类名在混淆后不同也不要紧了。

* 如果被调用的函数有回调参数，那么函数定义中这个参数必须是一个接口，不能是抽象类。

### 在接口回调方面

* 需要特别注意回调函数运行的线程。如果进程A调用进程B的函数，并且传入一个回调函数供进程B在进程A进行回调操作，那么默认这个回调函数将运行在进程A的主线程（UI线程）。如果你不想让回调函数运行在主线程，那么在接口声明的函数的对应的回调参数之前加上@Background注解。

* 如果回调函数有返回值的话，请使用@Background注解让它运行在后台线程。如果运行在主线程，那么返回值始终为null。

* 在回调函数的引用方面，框架持有回调函数的强引用，这个可能会导致内存泄漏。为了解决该问题，你可以在接口声明的对应回调参数前加上@WeakRef注解，这样XIPC持有的就是回调函数的弱引用。如果进程的回调函数被回收了，而对方进程还在调用这个函数（对方进程并不会知道回调函数被回收），这个不会有任何影响，也不会造成崩溃。如果回调函数有返回值，那么就返回null。

* @Background和@WeakRef注解，必须在接口中对应的函数参数前进行添加。如果加在其他地方，将不会有任何作用。

### 其他方面

* 调用函数的时候，任何Context在另一个进程中都会变成对方进程的application context。

* 接口参数的数据传递默认是基于Json的。

* 在使用过程中，出现任何错误，都会有相关日志记录，你只需要执行`XIPC.debug`打开调试即可看见日志。


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