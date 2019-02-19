# Android 第三方登录、分享、支付、签约集成方案

- 目前很多 APP 都添加了第三方授权功能，包括登录、分享、支付、签约等等。其中集成较多的平台是微信、QQ、支付宝、微博、银联、华为。
- 由于这些代码都是固定写法，所以最后抽取成了一个库 [AuthSDK](https://github.com/WJ-G/AuthSDK)。
- 目前支持微信、微博、QQ、华为的登录和分享（不包括华为）功能，微信、支付宝、银联、华为的支付功能，微信、支付宝、华为的签约功能。
- 可根据需求引用不同第三方集成库。
- SDK 不支持同时做多个请求。

### 集成第三方 SDK 版本：  
  - 微信 : com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.1.4  
  - 微博 : com.sina.weibo.sdk:core:4.2.7:openDefaultRelease@aar  
  - QQ : open_sdk_r5990_lite  
  - 支付宝 : alipaySdk-20170922  
  - 银联: 手机支付控件接入指南: 3.4.1
  - 华为: 2.6.1.301

# 集成方法
1. 根据需求选择是否添加, 在 project 目录下的 build.gradle 文件中添加微博和华为的 maven 地址： 
 
	```
    allprojects {
        repositories {
            google()
            jcenter()
    
            maven { url "https://dl.bintray.com/thelasterstar/maven/" }     // 微博 aar
            maven { url 'http://developer.huawei.com/repo/' }               // 华为仓库
        }
    }
	```
    
2. 根据需求选择是否添加, 在 app module 的 build.gradle 中添加引用:  

	```aidl
    dependencies {
        compile 'tech.jianyue.auth:auth:1.4.9'
        compile 'tech.jianyue.auth:auth_huawei:1.4.9'
        compile 'tech.jianyue.auth:auth_qq:1.4.9'
        compile 'tech.jianyue.auth:auth_weibo:1.4.9'
        compile 'tech.jianyue.auth:auth_weixin:1.4.9'
        compile 'tech.jianyue.auth:auth_yinlian:1.4.9'
        compile 'tech.jianyue.auth:auth_zhifubao:1.4.9'
    }
    ```

3. 在 app module 的清单文件中添加 QQ、微信、支付宝、华为的配置:  
	> 其中 QQ_SCHEME 为配置项，值为: tencent 加 QQ 的 AppID；  
	> 支付宝的 scheme 为签约回调的标记，需要与支付宝确定；   
	> 用到哪个第三方就配置对应的项目就可以；

    ``` xml
        <!-- 微信 -->
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:label="@string/app_name"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:exported="true"/>

        <activity 
            android:name=".wxapi.WXPayEntryActivity"
            android:label="@string/app_name"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:exported="true" />

        <!-- QQ -->
        <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:launchMode="singleTask"
            android:noHistory="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="${QQ_SCHEME}" />
            </intent-filter>
        </activity>
        
        <!-- 支付宝签约 -->
        <activity
            android:name="tech.jianyue.auth.AliRouseActivity"
            android:allowTaskReparenting="true">
            <!--支付宝免密支付完成时走此filter，必须匹配scheme-->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="123" />
            </intent-filter>
        </activity>

        <!--华为-->
        <!--在application节点下增加APPID value的值“xxx”用实际申请的应用ID替换，来源于开发者联盟网站应用的服务详情。-->
        <meta-data 
            android:name="com.huawei.hms.client.appid"
            android:value="appid=xxx">
        </meta-data>
             
        <!--在application节点下增加provider，UpdateProvider用于HMS-SDK引导升级HMS，提供给系统安装器读取升级文件。UpdateSdkFileProvider用于应用自升级。-->
        <!--“xxx.xxx.xxx”用实际的应用包名替换-->
        <meta-data
            android:name="com.huawei.hms.version"
            android:value="2.6.3.300">
        </meta-data>
        <provider 
            android:name="com.huawei.hms.update.provider.UpdateProvider"
            android:authorities="xxx.xxx.xxx.hms.update.provider"
            android:exported="false"
            android:grantUriPermissions="true" >
        </provider>
        <provider 
            android:name="com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider" 
            android:authorities="xxx.xxx.xxx.updateSdk.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
        </provider>
    ```

4. 在 app module 的包名下添加 wxapi 包, 并创建 WXEntryActivity 类和 WXPayEntryActivity 类, 继承自库内 AuthActivity 类；  
	> 其中 WXEntryActivity 为登录、分享回调；WXPayEntryActivity 为支付回调；

    ``` java
    public class WXPayEntryActivity extends AuthActivity {
    }
    public class WXEntryActivity extends AuthActivity {
    }
    ```

5. 初始化 Auth 库，其中的 ID、KYE 等需要通过第三方网站进行注册申请  
    
    ``` java 
    Auth.init().setQQAppID("")
            .setWXAppID("")
            .setWXSecret("")
            .setWBAppKey("")
            .setWBRedirectUrl("")
            .setWBScope("")
            .setHWAppID("")
            .setHWKey("")
            .setHWMerchantID("")
            .addFactoryForHW(AuthBuildForHW.getFactory())
            .addFactoryForQQ(AuthBuildForQQ.getFactory())
            .addFactoryForWB(AuthBuildForWB.getFactory())
            .addFactoryForWX(AuthBuildForWX.getFactory())
            .addFactoryForYL(AuthBuildForYL.getFactory())
            .addFactoryForZFB(AuthBuildForZFB.getFactory())
            .build();
         
    // 如果使用华为支付功能, 还需要在 MainActivity 里初始化华为相关控件(华为接口做的真是没有其他第三方友好, 有待提升啊)
    Auth.withHW(context).initHW(activity);
    ```

6. 添加权限（已经在对应库的Manifest中添加，无需再次手动添加）

    ``` xml
    <!-- QQ -->
    <!--<uses-permission android:name="android.permission.INTERNET" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>-->
 
    <!-- 微博 -->
    <!--<uses-permission android:name="android.permission.INTERNET" />-->
    <!--<uses-permission android:name="android.permission.READ_PHONE_STATE" />&lt;!&ndash; 用于调用 JNI &ndash;&gt;-->
    <!--<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />-->
    <!--<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />-->
 
    <!-- 微信 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 
    <!--银联-->
    <!--<uses-permission android:name="android.permission.INTERNET" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />-->
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <!--<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />-->
    <!--<uses-permission android:name="android.permission.READ_PHONE_STATE" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />-->
    <uses-permission android:name="android.permission.NFC" />
    <uses-permission android:name="org.simalliance.openmobileapi.SMARTCARD" />
    <uses-feature android:name="android.hardware.nfc.hce" />
 
    <!--支付宝-->
    <!--<uses-permission android:name="android.permission.INTERNET" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />-->
    <!--<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />-->
    <!--<uses-permission android:name="android.permission.READ_PHONE_STATE" />-->
    <!--<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />-->
 
    <!--华为-->
    <!--HMS-SDK引导升级HMS功能，访问OTA服务器需要网络权限-->
    <!--<uses-permission android:name="android.permission.INTERNET" />-->
    <!--HMS-SDK引导升级HMS功能，保存下载的升级包需要SD卡写权限-->
    <!--<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />-->
    <!--检测网络状态-->
    <!--<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>-->
    <!--检测wifi状态-->
    <!--<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>-->
    <!--为了获取用户手机的IMEI，用来唯一的标识用户。-->
    <!--<uses-permission android:name="android.permission.READ_PHONE_STATE"/>-->
    <!--如果是安卓8.0，应用编译配置的targetSdkVersion>=26，请务必添加以下权限 -->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
 
    ```

7. 添加混淆:

    ``` pro
	# Auth
	-keep class tech.jianyue.auth.** {*;}

	# 微博
	-keep class com.sina.weibo.sdk.** { *; }

	# 微信
	-keep class com.tencent.mm.opensdk.** { *; }
	-keep class com.tencent.wxop.** { *; }
	-keep class com.tencent.mm.sdk.** { *; }

	# QQ
	-keep class com.tencent.open.TDialog$*
	-keep class com.tencent.open.TDialog$* {*;}
	-keep class com.tencent.open.PKDialog
	-keep class com.tencent.open.PKDialog {*;}
	-keep class com.tencent.open.PKDialog$*
	-keep class com.tencent.open.PKDialog$* {*;}

	# 支付宝
	-keep class com.alipay.android.app.IAlixPay{*;}
	-keep class com.alipay.android.app.IAlixPay$Stub{*;}
	-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
	-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
	-keep class com.alipay.sdk.app.PayTask{ public *;}
	-keep class com.alipay.sdk.app.AuthTask{ public *;}
	-keep class com.alipay.sdk.app.H5PayCallback {
    	<fields>;
    	<methods>;
	}
	-keep class com.alipay.android.phone.mrpc.core.** { *; }
	-keep class com.alipay.apmobilesecuritysdk.** { *; }
	-keep class com.alipay.mobile.framework.service.annotation.** { *; }
	-keep class com.alipay.mobilesecuritysdk.face.** { *; }
	-keep class com.alipay.tscenter.biz.rpc.** { *; }
	-keep class org.json.alipay.** { *; }
	-keep class com.alipay.tscenter.** { *; }
	-keep class com.ta.utdid2.** { *;}
	-keep class com.ut.device.** { *;}

	# 银联
	-keep  public class com.unionpay.uppay.net.HttpConnection {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.net.HttpParameters {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.model.BankCardInfo {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.model.PAAInfo {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.model.ResponseInfo {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.model.PurchaseInfo {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.util.DeviceInfo {
		public <methods>;
	}
	-keep  public class com.unionpay.uppay.util.PayEngine {
		public <methods>;
		native <methods>;
	}
	-keep  public class com.unionpay.utils.UPUtils {
		native <methods>;
	}

	# 华为
	-ignorewarning
	-keepattributes *Annotation*
	-keepattributes Exceptions
	-keepattributes InnerClasses
	-keepattributes Signature
	-keepattributes SourceFile,LineNumberTable
	-keep class com.hianalytics.android.**{*;}
	-keep class com.huawei.updatesdk.**{*;}
	-keep class com.huawei.hms.**{*;}
	-keep class com.huawei.gamebox.plugin.gameservice.**{*;}
	-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
	-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
	-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;} 
    ```

8. 调用方式:  
    
    - 登录
    
    ``` java
    Auth.withWX(context)
            .setAction(Auth.Login)
            .build(mCallback);

    Auth.withWB(context)
             .setAction(Auth.Login)
             .build(mCallback);

    Auth.withQQ(context)
             .setAction(Auth.Login)
             .build(mCallback);
          
    Auth.withHW(context)
             .setAction(Auth.Login)
             .build(mCallback);
    ```

    - 签约, rouse 数据由服务器根据第三方协议生成
    
    ``` java
    Auth.withWX(context)				// 微信唤起Web, 可用于唤起微信的自动续订服务，目前没有回调结果
            .setAction(Auth.Rouse)
            .rouseWeb("")
            .build(mCallback);

    Auth.withZFB(context)
            .setAction(Auth.Rouse)
            .rouseWeb("")
            .build(mCallback);
    
    Auth.withHW(context)              // 参数按文档配置全
            .setAction(Auth.Rouse)
            .payTradeType("")
            .payPublicKey("")
            .build(mCallback);
    ```

    - 支付, 数据由服务器根据第三方协议生成

    ``` java
    Auth.withWX(context)
            .setAction(Auth.Pay)
            .payNonceStr("")
            .payPackageValue("")
            .payPartnerId("")
            .payPrepayId("")
            .paySign("")
            .payTimestamp("")
            .build(mCallback);

    Auth.withZFB(context)
            .setAction(Auth.Pay)
            .payOrderInfo("")
            .payIsShowLoading(true)      // 支付宝提供, 是否显示加载动画
            .build(mCallback);

    Auth.withYL(context)
            .setAction(Auth.Pay)
            .payOrderInfo("")
            .build(mCallback);
         
    Auth.withHW(this)
            .setAction(Auth.Pay)
            .payAmount("")
            ...
            .build(mCallback);
    ```

    - 分享  
    微信中 shareToSession shareToTimeline shareToFavorite 互斥, 只能使用其中一个;
    
    ``` java
    // 微信分享文本、图片、链接、视频、音乐、小程序
    Auth.withWX(context)
            .setAction(Auth.ShareText)
            .shareToSession()      // 分享到对话
            .shareToTimeline()     // 分享到朋友圈
            .shareToFavorite()     // 分享到收藏, 三个分享方式如果共存, 则只取最后一个.
            .shareText("Text")                     // 必填
            .shareTextDescription("Description")   // 必填
            .shareTextTitle("Title")
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.ShareImage)
            .shareToTimeline()
            .shareImageTitle("Title")
            .shareImage(getBitmap())   	           // 必填
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.ShareLink)
            .shareToTimeline()
            .shareLinkTitle("Title")               // 必填
            .shareLinkDescription("Description")
            .shareLinkImage(getBitmap())           // 必填
            .shareLinkUrl(LinkUrl)                 // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.ShareVideo)
            .shareToTimeline()
            .shareVideoTitle("Title")              // 必填
            .shareVideoDescription("Description")
            .shareVideoImage(getBitmap())          // 必填
            .shareVideoUrl(VideoUrl)               // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.ShareMusic)
            .shareToTimeline()
            .shareMusicTitle("Title")             // 必填
            .shareMusicDescription("Description")
            .shareMusicImage(getBitmap())         // 必填
            .shareMusicUrl(MusicUrl)              // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.ShareProgram)
            .shareToTimeline()
            .shareProgramTitle("Title")
            .shareProgramDescription("Description")
            .shareProgramId("")
            .shareProgramImage(getBitmap())
            .shareProgramPath("")
            .shareProgramUrl("")                  // 低版本微信打开的网络链接
            .build(mCallback);

    // 微博分享文本、图片、链接、视频
    Auth.withWB(context)
            .setAction(Auth.ShareText)
            .shareText("Text")
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareImage)
            .shareToStory()                       // 分享到微博故事, 仅支持单图和视频, 需要设置 shareImageUri(uri)
            .shareImageUri(getImageUri())         // 分享图片到微博故事时调用, Uri 为本地图片
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareImage)
            .shareImageText("Text")
            .shareImage(getBitmap())
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareImage)
            .shareImageText("Text")
            .shareImageMultiImage(getImageUriList())  // 分享多张图片, 本地图片 Uri 集合
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareLink)
            .shareLinkTitle("Title")             // 必填
            .shareLinkImage(getBitmap())         // 必填
            .shareLinkUrl(LinkUrl)               // 必填, 网络链接
            .shareLinkDescription("Description")
            .shareLinkText("Text")
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareVideo)
            .shareToStory()
            .shareVideoUri(getVideoUri())        // 必填, 本地 Uri
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.ShareVideo)
            .shareVideoTitle("Title")
            .shareVideoDescription("Description")
            .shareVideoText("Text")
            .shareVideoUri(getVideoUri())        // 必填, 本地 Uri
            .build(mCallback);

    // QQ 分享
    Auth.withQQ(context)
            .setAction(Auth.ShareImage)
            .shareToQzone(false)                // 单图和图文有效; 三种状态: 1. 不调用默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框 2. true 直接打开QZone的对话框, 3. false 隐藏分享到QZone
            .shareImageUrl(getImagePath())      // 单图只支持本地路径
            .shareImageName("Name")             // 单图有效, 设置后无明显效果
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.ShareImage)
            .shareImageTitle("Title")           // 图文分享与多图分享时必传, 不传为单图分享
            .shareImageUrl(getImagePath())      // 图文支持分享图片的URL或者本地路径
            .shareImageTargetUrl(LinkUrl)       // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
            .shareImageName("Name")             // 图文有效, 设置后无明显效果
            .shareImageArk("{\"ark\":\"a\"}")   // 可选, 分享携带ARK JSON串. 仅支持图文方式
            .shareImageDescription("Description") // 多图\图文有效
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.ShareImage)
            .shareImageTitle("Title")                 // 图文分享与多图分享时必传, 不传为单图分享
            .shareImageMultiImage(getImagePathList()) // 默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃); shareImageToMood 模式下只支持本地图片, 不调用 shareImageToMood 同时支持网络和本地 // TODO QZone 接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
            .shareImageTargetUrl(LinkUrl)             // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
            .shareImageDescription("Description")     // 多图\图文有效
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.ShareImage)
            .shareImageToMood()               // 分享图文到说说, 会过滤掉 shareImageTitle 信息, 图片以 shareImageMultiImage 传入, 以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
            .shareImageScene("Scene")         // 调用 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
            .shareImageBack("Back")           // 调用 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
            .shareImageMultiImage(getImagePathList())
            .build(mCallback);

    Auth.withQQ(context)                      // 由于 Video 只能分享到 QQ 空间, 不受 shareToQzone() 状态影响;
            .setAction(Auth.ShareVideo)
            .shareVideoUrl(getVideoPath())    // 仅支持本地路径
            .shareVideoScene("Scene")
            .shareVideoBack("Back")
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.ShareMusic)
            .shareToQzone(false)
            .shareMusicTitle("Title")
            .shareMusicDescription("Description")
            .shareMusicImage(getImageUrl())   // 分享图片的URL或者本地路径
            .shareMusicName("Name")
            .shareMusicTargetUrl(LinkUrl)     // 这条分享消息被好友点击后的跳转URL, 网络链接
            .shareMusicUrl(MusicUrl)          // 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.ShareProgram)
            .shareToQzone(true)
            .shareProgramTitle("Title")
            .shareProgramDescription("Description")
            .shareProgramImage(getImageUrl())  // 分享图片的URL或者本地路径
            .shareProgramName("Name")
            .build(mCallback);
    ```

9. 注意事项: 
	> 使用中如出现异常, 可以查看项目源码, 其中有第三方 SDK 的一些使用限制注释;  
	> 支付时, 参照官方文档获取服务器返回信息.  
	> 项目中的 UserInfoForThird 类为第三方登录后返回的数据, 其中 userInfo 字段包含了第三方返回的原始用户信息数据 Json.  
	> 回调函数里添加了一些返回Code，主要由第三方返回，用于调试或自己处理某些特殊处理的情况，比如支付时支付宝会返回 8000，表示支付待确认状态，需要服务器端做轮询操作，可通过这个返回code做相应处理；

## 项目结构
1. Auth 类是对外开放, 用于实际使用的类, 包含了 Action 类型, 和不同第三方功能的调用.
2. AuthActivity 类是用于获取第三方回调的类.
3. AuthBuildFor** 用于实现第三方 SDK 功能.
4. AuthCallback 用于对事件结果的反馈.
5. UserInfoForThird 第三方登录返回的用户信息.
6. Utils 为了避免引用不必要的库, 实现了原生的网络请求等功能.
