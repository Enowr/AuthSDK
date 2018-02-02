package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 描述: 分发
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class Auth {
    protected static AuthBuilder AuthBuilder;

    public static final int Pay = 100;
    public static final int LOGIN = 101;                        // 微信\微博\QQ 登录

    public static final int SHARE_TEXT = 102;                   // 微信/微博 分享文本
    public static final int SHARE_IMAGE = 103;                  // 微信/微博/QQ 分享图片
    public static final int SHARE_LINK = 104;                   // 微信/微博 分享链接
    public static final int SHARE_VIDEO = 105;                  // 微信/微博/QQ 分享视频
    public static final int SHARE_MUSIC = 106;                  // 微信/QQ 分享音乐
    public static final int SHARE_PROGRAM = 107;                // 微信/QQ 分享小程序/应用

    private Auth() {
    }

    public static AuthBuilder init() {
        return new AuthBuilder();
    }

    public static AuthBuildForWX withWX(Context context) {
        return new AuthBuildForWX(context);
    }

    public static AuthBuildForWB withWB(Context context) {
        return new AuthBuildForWB(context);
    }

    public static AuthBuildForQQ withQQ(Context context) {
        return new AuthBuildForQQ(context);
    }

    public static AuthBuildForZFB withZFB(Activity activity) {
        return new AuthBuildForZFB(activity);
    }

    @IntDef({Pay, LOGIN, SHARE_TEXT, SHARE_IMAGE, SHARE_LINK, SHARE_VIDEO, SHARE_MUSIC, SHARE_PROGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionWX {
    }

    @IntDef({LOGIN, SHARE_TEXT, SHARE_IMAGE, SHARE_LINK, SHARE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionWB {
    }

    @IntDef({LOGIN, SHARE_IMAGE, SHARE_MUSIC, SHARE_VIDEO, SHARE_PROGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionQQ {
    }

    @IntDef({Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionZFB {
    }

    public static abstract class Builder {
        int mAction = -1;                                       // 事件
        String Sign;                                            // 任务标记
        Context mContext;                                       // 上下文
        AuthCallback mCallback;                                 // 回调函数

        String mTitle;                                          // 分享标题
        String mText;                                           // 分享文本
        String mDescription;                                    // 文本描述, 分享到朋友圈可以不传,聊天界面和收藏必须传
        Bitmap mBitmap;                                         // 分享图片
        String mUrl;                                            // 分享的 Url

        Builder(Context context) {
            mContext = context;
            init();
        }

        abstract void init();

        void destroy() {
            Sign = "";
            mContext = null;
            mCallback = null;
            mBitmap = null;
        }

        public abstract Builder setAction(int action);

        public void build(AuthCallback callback) {
            if (callback == null) {
                destroy();
                throw new NullPointerException("AuthCallback is null");
            } else if (mContext == null) {
                destroy();
                throw new NullPointerException("Context is null");
            } else if (mAction == -1) {
                callback.onFailed("未设置Action, 请调用 setAction(action)");
                destroy();
            } else {
                Sign = String.valueOf(System.currentTimeMillis());
                mCallback = callback;
                AuthActivity.addBuilder(this);
            }
        }
    }

    public static class AuthBuilder {
        String QQ_APPID;

        String WECHAT_APPID;
        String WECHAT_SECRET;

        String WEIBO_APPKEY;
        String WEIBO_REDIRECT_URL;
        String WEIBO_SCOPE;

        public AuthBuilder setQQAppID(String appID) {
            QQ_APPID = appID;
            return this;
        }

        public AuthBuilder setWXAppID(String appID) {
            WECHAT_APPID = appID;
            return this;
        }

        public AuthBuilder setWXSecret(String secret) {
            WECHAT_SECRET = secret;
            return this;
        }

        public AuthBuilder setWBAppKey(String key) {
            WEIBO_APPKEY = key;
            return this;
        }

        public AuthBuilder setWBDedirectUrl(String url) {
            WEIBO_REDIRECT_URL = url;
            return this;
        }

        public AuthBuilder setWBScope(String scope) {
            WEIBO_SCOPE = scope;
            return this;
        }

        public void build() {
            AuthBuilder = this;
        }
    }
}