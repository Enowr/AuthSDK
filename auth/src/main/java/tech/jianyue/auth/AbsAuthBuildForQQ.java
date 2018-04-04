package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * 描述: QQ 相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public abstract class AbsAuthBuildForQQ extends AbsAuthBuild {
    String mTitle;                                          // 标题
    String mDescription;                                    // 描述
    String mUrl;                                            // Url

    String mImageUrl;                                       // 图片 url
    String mAudioUrl;                                       // 音频 url
    String mArk;
    String mName;
    Boolean mQzone = null;
    boolean mMood = false;
    boolean mMultiImage = false;
    String mBack;
    String mScene;
    ArrayList<String> mImageList;                           // 图片集合, 最多支持9张图片，多余的图片会被丢弃; 说说 <=9张图片为发表说说，>9张为上传图片到相册,只支持本地图片


    AbsAuthBuildForQQ(Context context) {
        super(context, Auth.WITH_QQ);
    }

    abstract Controller getController(Activity activity);

    @Override
    public AbsAuthBuildForQQ setAction(@Auth.ActionQQ int action) {
        mAction = action;
        return this;
    }

    /**
     * 三种状态: 默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框(true, 直接打开QZone的对话框, false 隐藏分享到QZone)
     */
    public AbsAuthBuildForQQ shareToQzone(boolean qzone) {
        mQzone = qzone;
        return this;
    }

    /**
     * 分享到说说
     */
    public AbsAuthBuildForQQ shareImageToMood() {
        mMood = true;
        return this;
    }

    /**
     * 单图只支持本地路径, 图文支持分享图片的URL或者本地路径, 不设置 Title 为单图, 否则为多图或图文
     */
    public AbsAuthBuildForQQ shareImageUrl(String url) {
        mImageUrl = url;
        return this;
    }

    /**
     * 最长 30 个字符
     */
    public AbsAuthBuildForQQ shareImageTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * 网络链接, 点击后跳转 url
     */
    public AbsAuthBuildForQQ shareImageTargetUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForQQ shareImageDescription(String description) {
        mDescription = description;
        return this;
    }

    // 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替              // 测试后无效果
    public AbsAuthBuildForQQ shareImageName(String name) {
        mName = name;
        return this;
    }

    /**
     * 可选, 分享携带ARK JSON串
     */
    public AbsAuthBuildForQQ shareImageArk(String ark) {
        mArk = ark;
        return this;
    }

    /**
     * 可选, 调用了 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
     */
    public AbsAuthBuildForQQ shareImageScene(String scene) {
        mScene = scene;
        return this;
    }

    /**
     * 可选, 调用了 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
     */
    public AbsAuthBuildForQQ shareImageBack(String back) {
        mBack = back;
        return this;
    }

    /**
     * 调用后 shareImageUrl 失效, 且默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
     * QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
     * <p>
     * 如果调用了 shareToMood 则发表说说: 说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
     */
    public AbsAuthBuildForQQ shareImageMultiImage(ArrayList<String> list) {
        mMultiImage = true;
        mImageList = list;
        return this;
    }

    public AbsAuthBuildForQQ shareMusicTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
     */
    public AbsAuthBuildForQQ shareMusicUrl(String url) {
        mAudioUrl = url;
        return this;
    }

    /**
     * 网络链接
     */
    public AbsAuthBuildForQQ shareMusicTargetUrl(String url) {
        mUrl = url;
        return this;
    }

    /**
     * 分享图片的URL或者本地路径
     */
    public AbsAuthBuildForQQ shareMusicImage(String url) {
        mImageUrl = url;
        return this;
    }

    public AbsAuthBuildForQQ shareMusicDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForQQ shareMusicName(String name) {
        mName = name;
        return this;
    }

    public AbsAuthBuildForQQ shareVideoUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForQQ shareVideoScene(String scene) {
        mScene = scene;
        return this;
    }

    public AbsAuthBuildForQQ shareVideoBack(String back) {
        mBack = back;
        return this;
    }

    public AbsAuthBuildForQQ shareProgramTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForQQ shareProgramDescription(String description) {
        mDescription = description;
        return this;
    }

    /**
     * 分享图片的URL或者本地路径
     */
    public AbsAuthBuildForQQ shareProgramImage(String url) {
        mImageUrl = url;
        return this;
    }

    public AbsAuthBuildForQQ shareProgramName(String name) {
        mName = name;
        return this;
    }

    interface Controller {
        void destroy();

        void callback(int requestCode, int resultCode, Intent data);
    }
}