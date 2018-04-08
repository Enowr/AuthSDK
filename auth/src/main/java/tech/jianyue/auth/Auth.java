package tech.jianyue.auth;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * 描述: 分发
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class Auth {
    public static final int UNKNOWN_TYPE = -1;                  // 未知类型

    public static final int Pay = 100;                          // 微信\支付宝\银联 支付

    public static final int RouseWeb = 111;                     // 微信(无回调) 唤起WebView

    public static final int LOGIN = 121;                        // 微信\微博\QQ 登录

    public static final int SHARE_TEXT = 131;                   // 微信/微博 分享文本
    public static final int SHARE_IMAGE = 132;                  // 微信/微博/QQ 分享图片
    public static final int SHARE_LINK = 133;                   // 微信/微博 分享链接
    public static final int SHARE_VIDEO = 134;                  // 微信/微博/QQ 分享视频
    public static final int SHARE_MUSIC = 135;                  // 微信/QQ 分享音乐
    public static final int SHARE_PROGRAM = 136;                // 微信/QQ 分享小程序/应用

    public static final int WITH_WX = 141;                      // 微信 第三方标记
    public static final int WITH_WB = 142;                      // 微博 第三方标记
    public static final int WITH_QQ = 143;                      // QQ 第三方标记
    public static final int WITH_ZFB = 144;                     // 支付宝 第三方标记
    public static final int WITH_YL = 145;                      // 银联 第三方标记
    public static final int WITH_HW = 146;                      // 华为 第三方标记

    static AuthBuilder AuthBuilder;
    static HashMap<String, AbsAuthBuild> BuilderMap = new HashMap<>();

    private Auth() {
    }

    static AbsAuthBuild getBuilder(String key) {
        return Auth.BuilderMap.get(key);
    }

    public static AuthBuilder init() {
        return new AuthBuilder();
    }

    public static AbsAuthBuildForHW withHW(Context context) {
        return AuthBuilder.getFactory(WITH_HW).getBuildByHW(context);
    }

    public static AbsAuthBuildForQQ withQQ(Context context) {
        return AuthBuilder.getFactory(WITH_QQ).getBuildByQQ(context);
    }

    public static AbsAuthBuildForWB withWB(Context context) {
        return AuthBuilder.getFactory(WITH_WB).getBuildByWB(context);
    }

    public static AbsAuthBuildForWX withWX(Context context) {
        return AuthBuilder.getFactory(WITH_WX).getBuildByWX(context);
    }

    public static AbsAuthBuildForYL withYL(Context context) {
        return AuthBuilder.getFactory(WITH_YL).getBuildByYL(context);
    }

    public static AbsAuthBuildForZFB withZFB(Context context) {
        return AuthBuilder.getFactory(WITH_ZFB).getBuildByZFB(context);
    }

    @IntDef({RouseWeb, Pay, LOGIN, SHARE_TEXT, SHARE_IMAGE, SHARE_LINK, SHARE_VIDEO, SHARE_MUSIC, SHARE_PROGRAM})
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

    @IntDef({RouseWeb, Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionZFB {
    }

    @IntDef({Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionYL {
    }

    @IntDef({Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionHW {
    }

    @IntDef({WITH_HW, WITH_QQ, WITH_WB, WITH_WX, WITH_YL, WITH_ZFB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WithThird {
    }

    public static class AuthBuilder {
        String QQAppID;

        String WXAppID;
        String WXSecret;

        String WBAppKey;
        String WBRedirectUrl;
        String WBScope;

        String HWMerchantID;
        String HWAppID;
        String HWKey;

        private SparseArray<AuthBuildFactory> mFactoryArray = new SparseArray<>();

        private AuthBuildFactory getFactory(@WithThird int with) {
            if (mFactoryArray.get(with) == null) {
                throw new NullPointerException("添加依赖, 并配置初始化");
            } else {
                return mFactoryArray.get(with);
            }
        }

        public AuthBuilder setQQAppID(String appId) {
            QQAppID = appId;
            return this;
        }

        public AuthBuilder setWXAppID(String appID) {
            WXAppID = appID;
            return this;
        }

        public AuthBuilder setWXSecret(String secret) {
            WXSecret = secret;
            return this;
        }

        public AuthBuilder setWBAppKey(String key) {
            WBAppKey = key;
            return this;
        }

        public AuthBuilder setWBDedirectUrl(String url) {
            WBRedirectUrl = url;
            return this;
        }

        public AuthBuilder setWBScope(String scope) {
            WBScope = scope;
            return this;
        }

        public AuthBuilder setHWAppID(String id) {
            HWAppID = id;
            return this;
        }

        public AuthBuilder setHWMerchantID(String id) {
            HWMerchantID = id;
            return this;
        }

        public AuthBuilder setHWKey(String key) {
            HWKey = key;
            return this;
        }

        public AuthBuilder addFactoryByHW(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_HW, factory);
            return this;
        }

        public AuthBuilder addFactoryByQQ(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_QQ, factory);
            return this;
        }

        public AuthBuilder addFactoryByWB(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_WB, factory);
            return this;
        }

        public AuthBuilder addFactoryByWX(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_WX, factory);
            return this;
        }

        public AuthBuilder addFactoryByYL(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_YL, factory);
            return this;
        }

        public AuthBuilder addFactoryByZFB(AuthBuildFactory factory) {
            mFactoryArray.put(Auth.WITH_ZFB, factory);
            return this;
        }

        public void build() {
            AuthBuilder = this;
        }
    }
}