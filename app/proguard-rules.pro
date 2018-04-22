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
-keep class * extends android.support.v4.app.FragmentManager{ *; }
-verbose # 混淆时是否记录日志
-ignorewarnings # 忽略警告，避免打包时某些警告出现
-optimizationpasses 5 # 指定代码的压缩级别
-dontshrink #不压缩输入的类文件
-dontoptimize #不优化输入的类文件
-dontusemixedcaseclassnames # 混淆时不使用大小写混合，混淆后的类名为小写
-dontskipnonpubliclibraryclasses # 是否混淆第三方jar  指定不去忽略非公共的库的类
-dontpreverify # 不做预校验，preverify是proguard的4个步骤之一,Android不需要preverify，去掉这一步可加快混淆速度
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法
# 保护给定的可选属性
-keepattributes Exceptions,InnerClasses,Signature,*Annotation*,SourceFile,LineNumberTable
-dontwarn android.webkit.WebView,com.umeng.**,com.tencent.weibo.sdk.**,com.alibaba.**,com.alipay.**,butterknife.internal.**,com.makeramen.**,org.apache.http.**,com.android.volley.**,android.support.v4.**,com.pili.**,tv.danmaku.**,android.support.**,com.orhanobut.**,org.eclipse.jdt.annotation.**
-dontwarn okio.**

-keep public class android.net.http.SslError

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn Android.webkit.WebViewClient

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类，都有可能被外部调用
# 比如说，第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}
# 保持类成员
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
# 保持 Parcelable 不被混淆
-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class android.support.v4.** {
    <fields>;
    <methods>;
}
-keep public class * extends android.support.v4.**
-keep public class * extends android.webkit.**

# 保持枚举 enum 类不被混淆
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 native 方法不被混淆
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class android.webkit.**

-keepnames class org.apache.** {*;}
-keep public class org.apache.** {*;}

# 对于R（资源）下的所有类及其方法，都不能被混淆
-keep public class com.insurchain.insur_wallet.R$* {
    *;
}
# 对于带有回调函数onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
# 保持bean不被不被混淆
#-keep class com.deji.xiaobao.bean.**{*;}

# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Keep all the native methods
-keepclassmembers class * {
   private native <methods>;
   public native <methods>;
   protected native <methods>;
   public static native <methods>;
   private static native <methods>;
   static native <methods>;
   native <methods>;
}

-keepattributes *Annotation*
-dontwarn android.support.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
#-keeppackagenames com.iyoudang.matrix
#-keeppackagenames com.tencent.weibo.sdk.android.*

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# fastjson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# okhttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# retrofit
#-dontwarn okio.**
#-dontwarn javax.annotation.**

# weixin
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

# Eventbus
-keep class org.greenrobot.**  {*;}
-keepclassmembers class ** { public void onEvent*(**); }


-keepattributes EnclosingMethod

-keepclassmembers class * extends android.webkit.WebChromeClient{
    public void openFileChooser(...);
}