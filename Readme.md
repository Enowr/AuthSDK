# AuthSDK

[项目地址](https://github.com/Jieger/AuthSDK)

- 用于第三方登录\分享\支付\唤起 等服务. 由于都是通用代码, 所以整合后抽取公共方法, 让使用时更加简便.  

- 目前支持 微信\微博\QQ 的登录和分享功能, 微信\支付宝\银联 支付功能, 微信的唤起功能.

- SDK 版本:  
    微信 : com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.1.4  
    微博 : com.sina.weibo.sdk:core:4.1.4:openDefaultRelease@aar  
    QQ : open_sdk_r5990_lite  
    支付宝 : alipaySdk-20170922  
    银联: 手机支付控件接入指南: 3.4.1

## 1. 使用方式

1. 首先在 project 目录下的 build.gradle 文件中添加微博的 maven 地址:  
    ```aidl
    allprojects {
        repositories {
            google()
            jcenter()
    
            maven { url "https://dl.bintray.com/thelasterstar/maven/" }     // 微博 aar
        }
    }
    ```
    
2. 在 app module 中添加引用:  
    ```aidl
    dependencies {
        compile 'tech.jianyue.auth:auth:1.0.6'
    }
    ```

3. 在 app module 的清单文件中添加 QQ Activity 和 微信 Activity 的配置:  
   其中 QQ_SCHEME 为配置项, 值为: tencent 加 QQ 的 AppID  
    ```aidl
        <!-- 微信 -->
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:label="@string/app_name"
            android:exported="true"/>

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
    ```

4. 在 app module 的包名下添加 wxapi 包, 并创建 WXEntryActivity 类, 该类继承自库内 AuthActivity 类
    ```aidl

    public class WXEntryActivity extends AuthActivity {
       ...
    }
    ```

5. 初始化 Auth 库:  
   其中的ID\KYE等需要通过第三方网站进行注册申请  
    ```aidl
        Auth.init().setQQAppID(QQAPPID)
                .setWXAppID(WECHAT_APPID)
                .setWXSecret(WECHAT_SECRET)
                .setWBAppKey(WEIBO_APPKEY)
                .setWBDedirectUrl(WEIBO_REDIRECT_URL)
                .setWBScope(WEIBO_SCOPE)
                .build();
    
    ```

6. 调用方式:  
   代码中注释已经解释的很清楚, 使用时注意互斥的关系;  
   例如: 微信中 shareToSession shareToTimeline shareToFavorite 互斥, 只能使用其中一个;
   
    ```aidl
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_yl:
                Auth.withYL(this)
                        .setAction(Auth.Pay)
                        .payOrderInfo("123324")
                        .build(mCallback);
                     break;
            case R.id.pay_wx:
                Auth.withWX(this)
                        .setAction(Auth.Pay)
                        .payNonceStr("1")
                        .payPackageValue("1")
                        .payPartnerId("1")
                        .payPrepayId("1")
                        .paySign("1")
                        .payTimestamp("1")
                        .build(mCallback);
                break;
            case R.id.pay_zfb:
                Auth.withZFB(this)
                        .setAction(Auth.Pay)
                        .payOrderInfo("1")
                        .payIsShowLoading(true)                         // 是否显示加载动画
                        .build(mCallback);
                break;
            case R.id.login_wx:
                Auth.withWX(this)
                        .setAction(Auth.LOGIN)
                        .build(mCallback);
                break;
            case R.id.login_wb:
                Auth.withWB(this)
                        .setAction(Auth.LOGIN)
                        .build(mCallback);
                break;
            case R.id.login_qq:
                Auth.withQQ(this)
                        .setAction(Auth.LOGIN)
                        .build(mCallback);
                break;
            case R.id.rouse_web:                                        // 微信唤起Web, 可用于唤起微信的自动续订服务
                Auth.withWX(this)
                        .setAction(Auth.RouseWeb)
                        .rouseWeb("www.qq.com")
                        .build(mCallback);
                break;
            case R.id.share_wx_text:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_TEXT)
                        .shareToSession()                               // 分享到对话
                        .shareToTimeline()                              // 分享到朋友圈
                        .shareToFavorite()                              // 分享到收藏, 三个分享方式如果共存, 则只取最后一个.
                        .shareText("Text")                              // 必填
                        .shareTextDescription("Description")            // 必填
                        .shareTextTitle("Title")
                        .build(mCallback);
                break;
            case R.id.share_wx_image:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_IMAGE)
                        .shareToTimeline()
                        .shareImageTitle("Title")
                        .shareImage(getBitmap())                        // 必填
                        .build(mCallback);
                break;
            case R.id.share_wx_link:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_LINK)
                        .shareToTimeline()
                        .shareLinkTitle("Title")                        // 必填
                        .shareLinkDescription("Description")
                        .shareLinkImage(getBitmap())                    // 必填
                        .shareLinkUrl(LinkUrl)                          // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_video:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_VIDEO)
                        .shareToTimeline()
                        .shareVideoTitle("Title")                       // 必填
                        .shareVideoDescription("Description")
                        .shareVideoImage(getBitmap())                   // 必填
                        .shareVideoUrl(VideoUrl)                        // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_music:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_MUSIC)
                        .shareToTimeline()
                        .shareMusicTitle("Title")                       // 必填
                        .shareMusicDescription("Description")
                        .shareMusicImage(getBitmap())                   // 必填
                        .shareMusicUrl(MusicUrl)                        // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_program:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_PROGRAM)
                        .shareToTimeline()
                        .shareProgramTitle("Title")
                        .shareProgramDescription("Description")
                        .shareProgramId("")
                        .shareProgramImage(getBitmap())
                        .shareProgramPath("")
                        .shareProgramUrl("")                            // 低版本微信打开的网络链接
                        .build(mCallback);
                break;

            case R.id.share_wb_text:
                Auth.withWB(this)
                        .setAction(Auth.SHARE_TEXT)
                        .shareText("Text")
                        .build(mCallback);
                break;
            case R.id.share_wb_image:
                Auth.withWB(this)
                        .setAction(Auth.SHARE_IMAGE)
                        .shareToStory()                                 // 分享到微博故事, 仅支持单图和视频, 需要设置 shareImageUri(uri)
                        .shareImageUri(getImageUri())                   // 分享图片到微博故事时调用, shareImage shareImageText shareImageMultiImage 将失效, 只使用 uri 内容, Uri 为本地图片
                        .shareImageText("Text")
                        .shareImageMultiImage(getImageUriList())        // 分享多张图片, 本地图片 Uri 集合, shareBitmap 失效, 多图\单图 互斥
                        .shareImage(getBitmap())                        // 不调用 shareToStory shareImageMultiImage 将分享单张图片
                        .build(mCallback);
                break;
            case R.id.share_wb_link:
                Auth.withWB(this)
                        .setAction(Auth.SHARE_LINK)
                        .shareLinkTitle("Title")                        // 必填
                        .shareLinkImage(getBitmap())                    // 必填
                        .shareLinkUrl(LinkUrl)                          // 必填, 网络链接
                        .shareLinkDescription("Description")
                        .shareLinkText("Text")
                        .build(mCallback);
                break;
            case R.id.share_wb_video:
                Auth.withWB(this)
                        .setAction(Auth.SHARE_VIDEO)
                        .shareToStory()
                        .shareVideoTitle("Title")
                        .shareVideoDescription("Description")
                        .shareVideoText("Text")
                        .shareVideoUri(getVideoUri())                    // 必填, 本地 Uri
                        .build(mCallback);
                break;

            case R.id.share_qq_image:
                Auth.withQQ(this)
                        .setAction(Auth.SHARE_IMAGE)
                        .shareToQzone(false)                            // 多图\分享到说说时, 设置无效果; 三种状态: 1. 不调用默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框 2. true 直接打开QZone的对话框, 3. false 隐藏分享到QZone
                        .shareImageToMood()                             // 分享图文到说说, 会过滤掉 shareImageTitle 信息, 图片以 shareImageMultiImage 传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
                        .shareImageScene("Scene")                       // 调用 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
                        .shareImageBack("Back")                         // 调用 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
                        .shareImageMultiImage(getImagePathList())       // 调用后 shareImageUrl 失效, 且默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃); shareImageToMood 模式下只支持本地图片, 不调用 shareImageToMood 同时支持网络和本地 // TODO QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
                        .shareImageUrl(getImagePath())                  // 单图只支持本地路径, 图文支持分享图片的URL或者本地路径, 不设置 Title 为单图, 否则为多图或图文
                        .shareImageTitle("Title")                       // 图文分享与多图分享时必传, 不传为单图分享
                        .shareImageTargetUrl(LinkUrl)                   // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
                        .shareImageArk("{\"ark\":\"ark\"}")             // 可选, 分享携带ARK JSON串. 仅支持图文方式
                        .shareImageName("Name")                         // 单图\图文有效, 设置后无明显效果
                        .shareImageDescription("Description")           // 多图\图文有效
                        .build(mCallback);
                break;
            case R.id.share_qq_video:
                Auth.withQQ(this)                                       // 由于 Video 只能分享到 QQ 空间, 不受 shareToQzone() 状态影响;
                        .setAction(Auth.SHARE_VIDEO)
                        .shareVideoUrl(getVideoPath())                  // 仅支持本地路径
                        .shareVideoScene("Scene")
                        .shareVideoBack("Back")
                        .build(mCallback);
                break;
            case R.id.share_qq_music:
                Auth.withQQ(this)
                        .setAction(Auth.SHARE_MUSIC)
                        .shareToQzone(false)
                        .shareMusicTitle("Title")
                        .shareMusicDescription("Description")
                        .shareMusicImage(getImageUrl())                 // 分享图片的URL或者本地路径
                        .shareMusicName("Name")
                        .shareMusicTargetUrl(LinkUrl)                   // 这条分享消息被好友点击后的跳转URL, 网络链接
                        .shareMusicUrl(MusicUrl)                        // 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
                        .build(mCallback);
                break;
            case R.id.share_qq_program:
                Auth.withQQ(this)
                        .setAction(Auth.SHARE_PROGRAM)
                        .shareToQzone(false)
                        .shareProgramTitle("Title")
                        .shareProgramDescription("Description")
                        .shareProgramImage(getImageUrl())               // 分享图片的URL或者本地路径
                        .shareProgramName("Name")
                        .build(mCallback);
                break;
        }
    }
    ```

7. 混淆配置
    ```aidl
    #Auth
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
    
    #QQ
    -keep class com.tencent.open.TDialog$*
    -keep class com.tencent.open.TDialog$* {*;}
    -keep class com.tencent.open.PKDialog
    -keep class com.tencent.open.PKDialog {*;}
    -keep class com.tencent.open.PKDialog$*
    -keep class com.tencent.open.PKDialog$* {*;}
    
    #支付宝
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
 
    #银联
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

8. 注意事项: 
      使用中如出现异常, 可以查看项目源码, 其中有第三方 SDK 的一些使用限制注释;  
      支付时, 参照官方文档获取服务器返回信息.  
      项目中的 UserInfoForThird 类为第三方登录后返回的数据, 其中 userInfo 字段包含了第三方返回的原始用户信息数据 Json
   
## 2. 项目结构
1. Auth 类是对外开放, 用于实际使用的类, 包含了 Action 类型, 和不同第三方功能的调用.
2. AuthActivity 类是用于获取第三方回调的类.
3. AuthBuildFor** 用于实现第三方 SDK 功能.
4. AuthCallback 用于对事件结果的反馈.
5. UserInfoForThird 第三方登录返回的用户信息.
6. Utils 为了避免引用不必要的库, 实现了原生的网络请求等功能.