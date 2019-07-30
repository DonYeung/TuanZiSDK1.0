# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 魔蝎sdk
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
#-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn dalvik.**

# 保护注解
-keepattributes *Annotation*
# 避免混淆泛型
-keepattributes Signature
# 避免混淆反射
-keepattributes EnclosingMethod
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 不混淆内部类
-keepattributes InnerClasses

#TONGDUN 同盾
-dontwarn android.os.**
-dontwarn com.android.internal.**
-keep class cn.tongdun.android.**{*;}

## Retrofit and Rxjava
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keep class retrofit2.**{*;}
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keep class com.google.gson.stream.**{*;}
-keepattributes EnclosingMethod
-keep class com.loanhome.lib.bean.**{*;}

# 关于FaceID 活体、身份证、银⾏卡SDK 代码混淆规则
-keep public class com.megvii.**{*;}

#魔蝎SDK混淆代码
