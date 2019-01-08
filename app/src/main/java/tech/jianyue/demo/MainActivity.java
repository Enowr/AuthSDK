package tech.jianyue.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import tech.jianyue.auth.Auth;
import tech.jianyue.auth.AuthBuildForHW;
import tech.jianyue.auth.AuthBuildForQQ;
import tech.jianyue.auth.AuthBuildForWB;
import tech.jianyue.auth.AuthBuildForWX;
import tech.jianyue.auth.AuthBuildForYL;
import tech.jianyue.auth.AuthBuildForZFB;
import tech.jianyue.auth.AuthCallback;
import tech.jianyue.auth.UserInfoForThird;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private AuthCallback mCallback = new AuthCallback() {
        @Override
        public void onSuccessForPay(String code, String result) {
            Toast.makeText(MainActivity.this, "支付成功: " + result, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessForRouse(String code, String result) {
            Toast.makeText(MainActivity.this, "唤起签约成功: " + result, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessForLogin(UserInfoForThird info) {
            Toast.makeText(MainActivity.this, "登录成功: " + info.userInfo, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessForShare() {
            Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(String code, String msg) {
            String with = "";
            String action;
            switch (getWith()) {
                case Auth.WithWB:
                    with = "微博";
                    break;
                case Auth.WithQQ:
                    with = "QQ";
                    break;
                case Auth.WithWX:
                    with = "微信";
                    break;
                case Auth.WithZFB:
                    with = "支付宝";
                    break;
                case Auth.WithYL:
                    with = "银联";
                    break;
            }
            switch (getAction()) {
                case Auth.Pay:
                    action = "支付";
                    break;
                case Auth.Login:
                    action = "登录";
                    break;
                case Auth.Rouse:
                    action = "唤起";
                    break;
                case Auth.ErrorNotAction:
                    action = "未设置Action ";
                    break;
                default:
                    action = "分享";
                    break;
            }
            Toast.makeText(MainActivity.this, with + action + "失败: " + msg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Auth.init().setQQAppID("QQ_APPID")
                .setWXAppID("wx1")
                .setWXSecret("wx1")
                .setWBAppKey("WEIBO_APPKEY")
                .setWBRedirectUrl("WEIBO_REDIRECT_URL")
                .setWBScope("WEIBO_SCOPE")
                .setHWAppID("111")
                .setHWKey("111")
                .setHWMerchantID("111")
                .addFactoryForHW(AuthBuildForHW.getFactory())
                .addFactoryForQQ(AuthBuildForQQ.getFactory())
                .addFactoryForWB(AuthBuildForWB.getFactory())
                .addFactoryForWX(AuthBuildForWX.getFactory())
                .addFactoryForYL(AuthBuildForYL.getFactory())
                .addFactoryForZFB(AuthBuildForZFB.getFactory())
                .build();

        // 华为需要在 MainActivity 初始化
        Auth.withHW(this).initHW(this);
    }

    private void initView() {
        findViewById(R.id.pay_yl).setOnClickListener(this);
        findViewById(R.id.pay_wx).setOnClickListener(this);
        findViewById(R.id.pay_zfb).setOnClickListener(this);
        findViewById(R.id.pay_hw).setOnClickListener(this);

        findViewById(R.id.login_wx).setOnClickListener(this);
        findViewById(R.id.login_wb).setOnClickListener(this);
        findViewById(R.id.login_qq).setOnClickListener(this);
        findViewById(R.id.login_hw).setOnClickListener(this);

        findViewById(R.id.rouse_wx).setOnClickListener(this);
        findViewById(R.id.rouse_zfb).setOnClickListener(this);
        findViewById(R.id.rouse_hw).setOnClickListener(this);

        findViewById(R.id.share_wx_text).setOnClickListener(this);
        findViewById(R.id.share_wx_image).setOnClickListener(this);
        findViewById(R.id.share_wx_link).setOnClickListener(this);
        findViewById(R.id.share_wx_video).setOnClickListener(this);
        findViewById(R.id.share_wx_music).setOnClickListener(this);
        findViewById(R.id.share_wx_program).setOnClickListener(this);

        findViewById(R.id.share_wb_text).setOnClickListener(this);
        findViewById(R.id.share_wb_image).setOnClickListener(this);
        findViewById(R.id.share_wb_link).setOnClickListener(this);
        findViewById(R.id.share_wb_video).setOnClickListener(this);

        findViewById(R.id.share_qq_image).setOnClickListener(this);
        findViewById(R.id.share_qq_video).setOnClickListener(this);
        findViewById(R.id.share_qq_music).setOnClickListener(this);
        findViewById(R.id.share_qq_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_yl:
                Auth.withYL(this)
                        .setAction(Auth.Pay)
                        .payOrderInfo("")
                        .build(mCallback);
                break;
            case R.id.pay_wx:
                Auth.withWX(this)
                        .setAction(Auth.Pay)
                        .payNonceStr("")
                        .payPackageValue("")
                        .payPartnerId("")
                        .payPrepayId("")
                        .paySign("")
                        .payTimestamp("")
                        .build(mCallback);
                break;
            case R.id.pay_zfb:
                Auth.withZFB(this)
                        .setAction(Auth.Pay)
                        .payOrderInfo("")
                        .build(mCallback);
                break;
            case R.id.pay_hw:
                Auth.withHW(this)                               // 参数按文档要求配全
                        .setAction(Auth.Pay)
                        .payPublicKey("")
                        .build(mCallback);
                break;
            case R.id.login_wx:
                Auth.withWX(this)
                        .setAction(Auth.Login)
                        .build(mCallback);
                break;
            case R.id.login_wb:
                Auth.withWB(this)
                        .setAction(Auth.Login)
                        .build(mCallback);
                break;
            case R.id.login_qq:
                Auth.withQQ(this)
                        .setAction(Auth.Login)
                        .build(mCallback);
                break;
            case R.id.login_hw:
                Auth.withHW(this)
                        .setAction(Auth.Login)
                        .build(mCallback);
                break;
            case R.id.rouse_wx:                                        // 微信唤起Web, 可用于唤起微信的自动续订服务，目前没有回调结果
                Auth.withWX(this)
                        .setAction(Auth.Rouse)
                        .rouseWeb("")
                        .build(mCallback);
                break;
            case R.id.rouse_zfb:
                Auth.withZFB(this)
                        .setAction(Auth.Rouse)
                        .rouseWeb("")
                        .build(mCallback);
                break;
            case R.id.rouse_hw:
                Auth.withHW(this)                               // 参数按文档配置
                        .setAction(Auth.Rouse)
                        .payTradeType("")
                        .payPublicKey("")
                        .build(mCallback);
                break;
            case R.id.share_wx_text:
                Auth.withWX(this)
                        .setAction(Auth.ShareText)
//                        .shareToSession()                             // 分享到对话
//                        .shareToTimeline()                            // 分享到朋友圈
                        .shareToFavorite()                              // 分享到收藏, 三个分享方式如果共存, 则只取最后一个.
                        .shareText("Text")                              // 必填
                        .shareTextDescription("Description")            // 必填
                        .shareTextTitle("Title")
                        .build(mCallback);
                break;
            case R.id.share_wx_image:
                Auth.withWX(this)
                        .setAction(Auth.ShareImage)
                        .shareToTimeline()
                        .shareImageTitle("Title")
                        .shareImage(null)                               // 必填
                        .build(mCallback);
                break;
            case R.id.share_wx_link:
                Auth.withWX(this)
                        .setAction(Auth.ShareLink)
                        .shareToTimeline()
                        .shareLinkTitle("Title")                        // 必填
                        .shareLinkDescription("Description")
                        .shareLinkImage(null)                           // 必填
                        .shareLinkUrl("")                               // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_video:
                Auth.withWX(this)
                        .setAction(Auth.ShareVideo)
                        .shareToTimeline()
                        .shareVideoTitle("Title")                       // 必填
                        .shareVideoDescription("Description")
                        .shareVideoImage(null)                          // 必填
                        .shareVideoUrl("")                              // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_music:
                Auth.withWX(this)
                        .setAction(Auth.ShareMusic)
                        .shareToTimeline()
                        .shareMusicTitle("Title")                       // 必填
                        .shareMusicDescription("Description")
                        .shareMusicImage(null)                          // 必填
                        .shareMusicUrl("")                              // 必填, 网络链接
                        .build(mCallback);
                break;
            case R.id.share_wx_program:                                 // TODO 未测试
                Auth.withWX(this)
                        .setAction(Auth.ShareProgram)
                        .shareToTimeline()
                        .shareProgramTitle("Title")
                        .shareProgramDescription("Description")
                        .shareProgramId("")
                        .shareProgramImage(null)
                        .shareProgramPath("")
                        .shareProgramUrl("")                            // 低版本微信打开的网络链接
                        .build(mCallback);
                break;

            case R.id.share_wb_text:
                Auth.withWB(this)
                        .setAction(Auth.ShareText)
                        .shareText("Text")
                        .build(mCallback);
                break;
            case R.id.share_wb_image:
                Auth.withWB(this)
                        .setAction(Auth.ShareImage)
//                        .shareToStory()                                 // 分享到微博故事, 仅支持单图和视频, 需要设置 shareImageUri(uri)
//                        .shareImageUri(getImageUri())                   // 分享图片到微博故事时调用, shareImage shareImageText shareImageMultiImage 将失效, 只使用 uri 内容, Uri 为本地图片
                        .shareImageText("Text")
//                        .shareImageMultiImage(getImageUriList())        // 分享多张图片, 本地图片 Uri 集合, shareBitmap 失效, 多图\单图 互斥
                        .shareImage(null)                               // 不调用 shareToStory shareImageMultiImage 将分享单张图片
                        .build(mCallback);
                break;
            case R.id.share_wb_link:
                Auth.withWB(this)
                        .setAction(Auth.ShareLink)
                        .shareLinkTitle("Title")                        // 必填
                        .shareLinkImage(null)                           // 必填
                        .shareLinkUrl("")                               // 必填, 网络链接
                        .shareLinkDescription("Description")
                        .shareLinkText("Text")
                        .build(mCallback);
                break;
            case R.id.share_wb_video:
                Auth.withWB(this)
                        .setAction(Auth.ShareVideo)
//                        .shareToStory()
                        .shareVideoTitle("Title")
                        .shareVideoDescription("Description")
                        .shareVideoText("Text")
                        .shareVideoUri(null)                            // 必填, 本地 Uri
                        .build(mCallback);
                break;

            case R.id.share_qq_image:
                Auth.withQQ(this)
                        .setAction(Auth.ShareImage)
//                        .shareToQzone(false)                            // 多图\分享到说说时, 设置无效果; 三种状态: 1. 不调用默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框 2. true 直接打开QZone的对话框, 3. false 隐藏分享到QZone
//                        .shareImageToMood()                             // 分享图文到说说, 会过滤掉 shareImageTitle 信息, 图片以 shareImageMultiImage 传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
//                        .shareImageScene("Scene")                       // 调用 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
//                        .shareImageBack("Back")                         // 调用 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
//                        .shareImageMultiImage(getImagePathList())       // 调用后 shareImageUrl 失效, 且默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃); shareImageToMood 模式下只支持本地图片, 不调用 shareImageToMood 同时支持网络和本地 // TODO QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
                        .shareImageUrl("")                              // 单图只支持本地路径, 图文支持分享图片的URL或者本地路径, 不设置 Title 为单图, 否则为多图或图文
//                        .shareImageTitle("Title")                       // 图文分享与多图分享时必传, 不传为单图分享
                        .shareImageTargetUrl(null)                      // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
                        .shareImageArk("{\"ark\":\"ark\"}")             // 可选, 分享携带ARK JSON串. 仅支持图文方式
                        .shareImageName("Name")                         // 单图\图文有效, 设置后无明显效果
                        .shareImageDescription("Description")           // 多图\图文有效
                        .build(mCallback);
                break;
            case R.id.share_qq_video:
                Auth.withQQ(this)                               // 由于 Video 只能分享到 QQ 空间, 不受 shareToQzone() 状态影响;
                        .setAction(Auth.ShareVideo)
                        .shareVideoUrl("")                              // 仅支持本地路径
                        .shareVideoScene("Scene")
                        .shareVideoBack("Back")
                        .build(mCallback);
                break;
            case R.id.share_qq_music:
                Auth.withQQ(this)
                        .setAction(Auth.ShareMusic)
                        .shareToQzone(false)
                        .shareMusicTitle("Title")
                        .shareMusicDescription("Description")
                        .shareMusicImage("")                            // 分享图片的URL或者本地路径
                        .shareMusicName("Name")
                        .shareMusicTargetUrl("")                        // 这条分享消息被好友点击后的跳转URL, 网络链接
                        .shareMusicUrl("")                              // 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
                        .build(mCallback);
                break;
            case R.id.share_qq_program:
                Auth.withQQ(this)
                        .setAction(Auth.ShareProgram)
                        .shareToQzone(false)
                        .shareProgramTitle("Title")
                        .shareProgramDescription("Description")
                        .shareProgramImage("")                          // 分享图片的URL或者本地路径
                        .shareProgramName("Name")
                        .build(mCallback);
                break;
        }
    }
}