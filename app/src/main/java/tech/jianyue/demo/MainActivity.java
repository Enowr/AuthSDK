package tech.jianyue.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import tech.jianyue.auth.Auth;
import tech.jianyue.auth.AuthCallback;
import tech.jianyue.auth.UserInfoForThird;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String VideoUrl = "https://showimg.caixin.com/dolphinfile/caixin/2017/12/0_21.mp4";
    private String MusicUrl = "http://sc1.111ttt.cn/2017/1/11/11/304112002493.mp3";
    private String LinkUrl = "http://www.baidu.com";
    private String ImagePath = Environment.getExternalStorageDirectory().getPath() + "/assets";


    private AuthCallback mCallback = new AuthCallback() {
        @Override
        public void onSuccessForPay(String result) {
            Toast.makeText(MainActivity.this, "支付成功: " + result, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessForRouse(String result) {
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
        public void onFailed(String msg) {
            String with = "";
            String action;
            switch (getWith()) {
                case Auth.WITH_WB:
                    with = "微博";
                    break;
                case Auth.WITH_QQ:
                    with = "QQ";
                    break;
                case Auth.WITH_WX:
                    with = "微信";
                    break;
                case Auth.WITH_ZFB:
                    with = "支付宝";
                    break;
                case Auth.WITH_YL:
                    with = "银联";
                    break;
            }
            switch (getAction()) {
                case Auth.Pay:
                    action = "支付";
                    break;
                case Auth.LOGIN:
                    action = "登录";
                    break;
                case Auth.RouseWeb:
                    action = "唤起";
                    break;
                case Auth.UNKNOWN_TYPE:
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
                .setWXAppID("WECHAT_APPID")
                .setWXSecret("WECHAT_SECRET")
                .setWBAppKey("WEIBO_APPKEY")
                .setWBDedirectUrl("WEIBO_REDIRECT_URL")
                .setWBScope("WEIBO_SCOPE")
                .build();
        Assets2Sd(ImagePath);
    }

    private void initView() {
        findViewById(R.id.pay_yl).setOnClickListener(this);
        findViewById(R.id.pay_wx).setOnClickListener(this);
        findViewById(R.id.pay_zfb).setOnClickListener(this);

        findViewById(R.id.login_wx).setOnClickListener(this);
        findViewById(R.id.login_wb).setOnClickListener(this);
        findViewById(R.id.login_qq).setOnClickListener(this);

        findViewById(R.id.rouse_web).setOnClickListener(this);

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
                        .payOrderInfo("111")
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
                        .rouseWeb("http://www.caixin.com")
                        .build(mCallback);
                break;
//            case R.id.rouse_web:
//                Auth.withZFB(this)
//                        .setAction(Auth.RouseWeb)
//                        .rouseWeb("")
//                        .build(mCallback);
//                break;
            case R.id.share_wx_text:
                Auth.withWX(this)
                        .setAction(Auth.SHARE_TEXT)
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
            case R.id.share_wx_program:                                 // TODO 未测试
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
//                        .shareToStory()                                 // 分享到微博故事, 仅支持单图和视频, 需要设置 shareImageUri(uri)
//                        .shareImageUri(getImageUri())                   // 分享图片到微博故事时调用, shareImage shareImageText shareImageMultiImage 将失效, 只使用 uri 内容, Uri 为本地图片
                        .shareImageText("Text")
//                        .shareImageMultiImage(getImageUriList())        // 分享多张图片, 本地图片 Uri 集合, shareBitmap 失效, 多图\单图 互斥
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
//                        .shareToStory()
                        .shareVideoTitle("Title")
                        .shareVideoDescription("Description")
                        .shareVideoText("Text")
                        .shareVideoUri(getVideoUri())                    // 必填, 本地 Uri
                        .build(mCallback);
                break;

            case R.id.share_qq_image:
                Auth.withQQ(this)
                        .setAction(Auth.SHARE_IMAGE)
//                        .shareToQzone(false)                            // 多图\分享到说说时, 设置无效果; 三种状态: 1. 不调用默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框 2. true 直接打开QZone的对话框, 3. false 隐藏分享到QZone
//                        .shareImageToMood()                             // 分享图文到说说, 会过滤掉 shareImageTitle 信息, 图片以 shareImageMultiImage 传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
//                        .shareImageScene("Scene")                       // 调用 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
//                        .shareImageBack("Back")                         // 调用 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
//                        .shareImageMultiImage(getImagePathList())       // 调用后 shareImageUrl 失效, 且默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃); shareImageToMood 模式下只支持本地图片, 不调用 shareImageToMood 同时支持网络和本地 // TODO QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
                        .shareImageUrl(getImagePath())                   // 单图只支持本地路径, 图文支持分享图片的URL或者本地路径, 不设置 Title 为单图, 否则为多图或图文
//                        .shareImageTitle("Title")                       // 图文分享与多图分享时必传, 不传为单图分享
                        .shareImageTargetUrl(LinkUrl)                   // 图文分享与多图分享时必传, 点击后的跳转URL, 网络链接
                        .shareImageArk("{\"ark\":\"ark\"}")             // 可选, 分享携带ARK JSON串. 仅支持图文方式
                        .shareImageName("Name")                         // 单图\图文有效, 设置后无明显效果
                        .shareImageDescription("Description")           // 多图\图文有效
                        .build(mCallback);
                break;
            case R.id.share_qq_video:
                Auth.withQQ(this)                               // 由于 Video 只能分享到 QQ 空间, 不受 shareToQzone() 状态影响;
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

    private Bitmap getBitmap() {
        try {
            InputStream is = getResources().getAssets().open("img_01.jpg");
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getImageUrl() {
        return "http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-014.jpg";
    }

    private ArrayList<String> getImageUrlList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-001.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-002.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-003.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-004.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-005.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-006.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-007.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-008.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-009.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-010.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-011.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-012.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-013.jpg");
        list.add("http://img.ivsky.com/img/bizhi/pre/201801/12/jeep_wrangler_geiger_willys-014.jpg");
        return list;
    }

    private String getImagePath() {
        return ImagePath + "/img_01.jpg";
    }

    private ArrayList<String> getImagePathList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ImagePath + "/img_01.jpg");
        list.add(ImagePath + "/img_02.jpg");
        return list;
    }

    private String getVideoPath() {
        return ImagePath + "/video_01.mp4";
    }

    private Uri getImageUri() {
        return Uri.fromFile(new File(ImagePath + "/img_01.jpg"));
    }

    private ArrayList<Uri> getImageUriList() {
        ArrayList<Uri> list = new ArrayList<>();
        list.add(Uri.fromFile(new File(ImagePath + "/img_01.jpg")));
        list.add(Uri.fromFile(new File(ImagePath + "/img_02.jpg")));
        return list;
    }

    private Uri getVideoUri() {
        return Uri.fromFile(new File(ImagePath + "/video_01.mp4"));
    }

    private void Assets2Sd(String fileSdPath) {
        File file = new File(fileSdPath);
        if (!file.exists()) {
            try {
                copyFilesFromAssets("", fileSdPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFilesFromAssets(String assetsPath, String savePath) throws Exception {
        String fileNames[] = getAssets().list(assetsPath);      // 获取assets目录下的所有文件及目录名
        if (fileNames.length > 0) {                             // 如果是目录
            File file = new File(savePath);
            file.mkdirs();                                      // 如果文件夹不存在，则创建
            for (String fileName : fileNames) {
                if (fileName.endsWith("jpg") || fileName.endsWith("mp4")) {
                    copyFilesFromAssets(fileName, savePath + "/" + fileName);
                }
            }
        } else {                                                // 如果是文件
            InputStream is = getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(new File(savePath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {       // 循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);            // 将读取的输入流写入到输出流
            }
            fos.flush();                                        // 刷新缓冲区
            is.close();
            fos.close();
        }
    }
}