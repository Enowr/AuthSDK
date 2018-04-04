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

    public static AuthBuilder init() {
        return new AuthBuilder();
    }

    public static AbsAuthBuildForHW withHW(Context context) {
        if (AuthBuilder.FactoryArray == null || AuthBuilder.FactoryArray.get(WITH_HW) == null) {
            throw new NullPointerException("添加华为依赖, 并配置初始化");
        } else {
            return AuthBuilder.FactoryArray.get(WITH_HW).getHWBuild(context);
        }
    }

    public static AbsAuthBuildForQQ withQQ(Context context) {
        if (AuthBuilder.FactoryArray == null || AuthBuilder.FactoryArray.get(WITH_QQ) == null) {
            throw new NullPointerException("添加QQ依赖, 并配置初始化");
        } else {
            return AuthBuilder.FactoryArray.get(WITH_QQ).getQQBuild(context);
        }
    }

    public static AbsAuthBuildForWB withWB(Context context) {
        if (AuthBuilder.FactoryArray == null || AuthBuilder.FactoryArray.get(WITH_WB) == null) {
            throw new NullPointerException("添加微博依赖, 并配置初始化");
        } else {
            return AuthBuilder.FactoryArray.get(WITH_WB).getWBBuild(context);
        }
    }

    public static AbsAuthBuildForWX withWX(Context context) {
        if (AuthBuilder.FactoryArray == null || AuthBuilder.FactoryArray.get(WITH_WX) == null) {
            throw new NullPointerException("添加微信依赖, 并配置初始化");
        } else {
            return AuthBuilder.FactoryArray.get(WITH_WX).getWXBuild(context);
        }
    }

    public static AuthBuildForZFB withZFB(Context context) {
        return new AuthBuildForZFB(context);
    }

    public static AuthBuildForYL withYL(Context context) {
        return new AuthBuildForYL(context);
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

    @IntDef({WITH_WX, WITH_WB, WITH_QQ, WITH_ZFB, WITH_YL, WITH_HW})
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

        SparseArray<AuthBuildFactory> FactoryArray = new SparseArray<>();

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

        public AuthBuilder addHWFactory(AuthBuildFactory factory) {
            FactoryArray.put(Auth.WITH_HW, factory);
            return this;
        }

        public AuthBuilder addWXFactory(AuthBuildFactory factory) {
            FactoryArray.put(Auth.WITH_WX, factory);
            return this;
        }

        public AuthBuilder addWBFactory(AuthBuildFactory factory) {
            FactoryArray.put(Auth.WITH_WB, factory);
            return this;
        }

        public AuthBuilder addQQFactory(AuthBuildFactory factory) {
            FactoryArray.put(Auth.WITH_QQ, factory);
            return this;
        }

        public void build() {
            AuthBuilder = this;
        }
    }
}