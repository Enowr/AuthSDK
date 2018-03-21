# Android 第三方登录、分享、支付、签约集成方案

  目前很多 APP 都添加了第三方功能，包括登录、分享、支付、签约等功能。其中集成较多的平台是微信、QQ、支付宝、微博、银联。这里主要介绍这几个平台的集成方案。

  由于这些代码都是固定写法，所以最后抽取成了一个第三方集成库 [AuthSDK](https://github.com/Jieger/AuthSDK)。

  目前支持 微信、微博、QQ 的登录和分享功能，微信、支付宝、银联的支付功能，微信、支付宝的签约功能。SDK 不支持并发操作， 也就是说不能同时做多个请求，只能串行请求。

### 当前 SDK 集成为最新的第三方SDK：  
  - 微信 : com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.1.4  
  - 微博 : com.sina.weibo.sdk:core:4.1.4:openDefaultRelease@aar  
  - QQ : open_sdk_r5990_lite  
  - 支付宝 : alipaySdk-20170922  
  - 银联: 手机支付控件接入指南: 3.4.1

# 集成方法
1. 在 project 目录下的 build.gradle 文件中添加微博的 maven 地址：
    使用微博功能的需要添加
    ```aidl
    allprojects {
        repositories {
            google()
            jcenter()
    
            maven { url "https://dl.bintray.com/thelasterstar/maven/" }     // 微博 aar
        }
    }
    ```
    
2. 在 app module 的 build.gradle 中添加引用:  
    ```aidl
    dependencies {
        compile 'tech.jianyue.auth:auth:1.1.4'
    }
    ```

3. 在 app module 的清单文件中添加 QQ 和微信的配置:  
    其中 QQ_SCHEME 为配置项，值为: tencent 加 QQ 的 AppID；  
    支付宝的 scheme 为签约回调的标记，需要与支付宝确定；  
    用到哪个第三方就配置对应的项目就可以，其他第三方不需配置；
    ```aidl
        <!-- 微信 -->
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:label="@string/app_name"
            android:exported="true"/>

        <activity 
            android:name=".wxapi.WXPayEntryActivity"
            android:label="@string/app_name"
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
    ```

4. 在 app module 的包名下添加 wxapi 包, 并创建 WXEntryActivity 类和 WXPayEntryActivity 类, 继承自库内 AuthActivity 类；
    其中 WXEntryActivity 为登录、分享回调；WXPayEntryActivity 为支付回调；
    ```aidl
    public class WXPayEntryActivity extends AuthActivity {
    }
    public class WXEntryActivity extends AuthActivity {
    }
    ```

5. 初始化 Auth 库，其中的 ID、KYE 等需要通过第三方网站进行注册申请  
    ```aidl
    Auth.init().setQQAppID(QQAPPID)
            .setWXAppID(WECHAT_APPID)
            .setWXSecret(WECHAT_SECRET)
            .setWBAppKey(WEIBO_APPKEY)
            .setWBDedirectUrl(WEIBO_REDIRECT_URL)
            .setWBScope(WEIBO_SCOPE)
            .build();
    ```

6. 添加权限
    ```aidl
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="android.permission.NFC" />
    <uses-permission android:name="org.simalliance.openmobileapi.SMARTCARD" />
    <uses-feature android:name="android.hardware.nfc.hce" />
    ```

7. 添加混淆:
    ```aidl
    # Auth
    -keep class tech.jianyue.auth.** {*;}
    # 微博
    -keep class com.sina.weibo.sdk.** { *; }
    # 微信
    -keep class com.tencent.mm.opensdk.** {
       *;
    }
    -keep class com.tencent.wxop.** {
       *;
    }
    -keep class com.tencent.mm.sdk.** {
       *;
    }
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
    ```

8. 调用方式:  

    - 登录
    ```aidl
    Auth.withWX(context)
            .setAction(Auth.LOGIN)
            .build(mCallback);

    Auth.withWB(context)
             .setAction(Auth.LOGIN)
             .build(mCallback);

    Auth.withQQ(context)
             .setAction(Auth.LOGIN)
             .build(mCallback);
    ```

    - 签约, rouseWeb 数据由服务器根据第三方协议生成
    ```aidl
    Auth.withWX(context)
            .setAction(Auth.RouseWeb)
            .rouseWeb("www.qq.com")
            .build(mCallback);

    Auth.withZFB(context)
            .setAction(Auth.RouseWeb)
            .rouseWeb("")
            .build(mCallback);
    ```

    - 支付, 数据由服务器根据第三方协议生成
    ```aidl
    Auth.withWX(context)
            .setAction(Auth.Pay)
            .payNonceStr("1")
            .payPackageValue("1")
            .payPartnerId("1")
            .payPrepayId("1")
            .paySign("1")
            .payTimestamp("1")
            .build(mCallback);

    Auth.withZFB(context)
            .setAction(Auth.Pay)
            .payOrderInfo("1")
            .payIsShowLoading(true)      // 支付宝提供, 是否显示加载动画
            .build(mCallback);

    Auth.withYL(context)
            .setAction(Auth.Pay)
            .payOrderInfo("111")
            .build(mCallback);
    ```

    - 分享  
    微信中 shareToSession shareToTimeline shareToFavorite 互斥, 只能使用其中一个;
    ``` aidl
    // 微信分享文本、图片、链接、视频、音乐、小程序
    Auth.withWX(context)
            .setAction(Auth.SHARE_TEXT)
            .shareToSession()      // 分享到对话
            .shareToTimeline()     // 分享到朋友圈
            .shareToFavorite()     // 分享到收藏, 三个分享方式如果共存, 则只取最后一个.
            .shareText("Text")                     // 必填
            .shareTextDescription("Description")   // 必填
            .shareTextTitle("Title")
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareToTimeline()
            .shareImageTitle("Title")
            .shareImage(getBitmap())      // 必填
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.SHARE_LINK)
            .shareToTimeline()
            .shareLinkTitle("Title")               // 必填
            .shareLinkDescription("Description")
            .shareLinkImage(getBitmap())           // 必填
            .shareLinkUrl(LinkUrl)                 // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.SHARE_VIDEO)
            .shareToTimeline()
            .shareVideoTitle("Title")              // 必填
            .shareVideoDescription("Description")
            .shareVideoImage(getBitmap())          // 必填
            .shareVideoUrl(VideoUrl)               // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.SHARE_MUSIC)
            .shareToTimeline()
            .shareMusicTitle("Title")             // 必填
            .shareMusicDescription("Description")
            .shareMusicImage(getBitmap())         // 必填
            .shareMusicUrl(MusicUrl)              // 必填, 网络链接
            .build(mCallback);

    Auth.withWX(context)
            .setAction(Auth.SHARE_PROGRAM)
            .shareToTimeline()
            .shareProgramTitle("Title")
            .shareProgramDescription("Description")
            .shareProgramId("")
            .shareProgramImage(getBitmap())
            .shareProgramPath("")
            .shareProgramUrl("")         // 低版本微信打开的网络链接
            .build(mCallback);

    // 微博分享文本、图片、链接、视频
    Auth.withWB(context)
            .setAction(Auth.SHARE_TEXT)
            .shareText("Text")
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareToStory()                // 分享到微博故事, 仅支持单图和视频, 需要设置 shareImageUri(uri)
            .shareImageUri(getImageUri())  // 分享图片到微博故事时调用, Uri 为本地图片
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareImageText("Text")
            .shareImage(getBitmap())
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareImageText("Text")
            .shareImageMultiImage(getImageUriList())  // 分享多张图片, 本地图片 Uri 集合
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_LINK)
            .shareLinkTitle("Title")             // 必填
            .shareLinkImage(getBitmap())         // 必填
            .shareLinkUrl(LinkUrl)               // 必填, 网络链接
            .shareLinkDescription("Description")
            .shareLinkText("Text")
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_VIDEO)
            .shareToStory()
            .shareVideoUri(getVideoUri())      // 必填, 本地 Uri
            .build(mCallback);

    Auth.withWB(context)
            .setAction(Auth.SHARE_VIDEO)
            .shareVideoTitle("Title")
            .shareVideoDescription("Description")
            .shareVideoText("Text")
            .shareVideoUri(getVideoUri())      // 必填, 本地 Uri
            .build(mCallback);

    // QQ 分享
    Auth.withQQ(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareToQzone(false)              // 单图和图文有效; 三种状态: 1. 不调用默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框 2. true 直接打开QZone的对话框, 3. false 隐藏分享到QZone
            .shareImageUrl(getImagePath())    // 单图只支持本地路径
            .shareImageName("Name")           // 单图有效, 设置后无明显效果
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareImageTitle("Title")         // 图文分享与多图分享时必传, 不传为单图分享
            .shareImageUrl(getImagePath())    // 图文支持分享图片的URL或者本地路径
            .shareImageTargetUrl(LinkUrl)     // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
            .shareImageName("Name")           // 图文有效, 设置后无明显效果
            .shareImageArk("{\"ark\":\"a\"}") // 可选, 分享携带ARK JSON串. 仅支持图文方式
            .shareImageDescription("Description") // 多图\图文有效
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareImageTitle("Title")                 // 图文分享与多图分享时必传, 不传为单图分享
            .shareImageMultiImage(getImagePathList()) // 默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃); shareImageToMood 模式下只支持本地图片, 不调用 shareImageToMood 同时支持网络和本地 // TODO QZone 接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
            .shareImageTargetUrl(LinkUrl)             // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
            .shareImageDescription("Description")     // 多图\图文有效
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.SHARE_IMAGE)
            .shareImageToMood()               // 分享图文到说说, 会过滤掉 shareImageTitle 信息, 图片以 shareImageMultiImage 传入, 以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
            .shareImageScene("Scene")         // 调用 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
            .shareImageBack("Back")           // 调用 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
            .shareImageMultiImage(getImagePathList())
            .build(mCallback);

    Auth.withQQ(context)       // 由于 Video 只能分享到 QQ 空间, 不受 shareToQzone() 状态影响;
            .setAction(Auth.SHARE_VIDEO)
            .shareVideoUrl(getVideoPath())    // 仅支持本地路径
            .shareVideoScene("Scene")
            .shareVideoBack("Back")
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.SHARE_MUSIC)
            .shareToQzone(false)
            .shareMusicTitle("Title")
            .shareMusicDescription("Description")
            .shareMusicImage(getImageUrl())   // 分享图片的URL或者本地路径
            .shareMusicName("Name")
            .shareMusicTargetUrl(LinkUrl)     // 这条分享消息被好友点击后的跳转URL, 网络链接
            .shareMusicUrl(MusicUrl)          // 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
            .build(mCallback);

    Auth.withQQ(context)
            .setAction(Auth.SHARE_PROGRAM)
            .shareToQzone(true)
            .shareProgramTitle("Title")
            .shareProgramDescription("Description")
            .shareProgramImage(getImageUrl())  // 分享图片的URL或者本地路径
            .shareProgramName("Name")
            .build(mCallback);
    ```

9. 注意事项: 
      使用中如出现异常, 可以查看项目源码, 其中有第三方 SDK 的一些使用限制注释;  
      支付时, 参照官方文档获取服务器返回信息.  
      项目中的 UserInfoForThird 类为第三方登录后返回的数据, 其中 userInfo 字段包含了第三方返回的原始用户信息数据 Json

## 项目结构
1. Auth 类是对外开放, 用于实际使用的类, 包含了 Action 类型, 和不同第三方功能的调用.
2. AuthActivity 类是用于获取第三方回调的类.
3. AuthBuildFor** 用于实现第三方 SDK 功能.
4. AuthCallback 用于对事件结果的反馈.
5. UserInfoForThird 第三方登录返回的用户信息.
6. Utils 为了避免引用不必要的库, 实现了原生的网络请求等功能.