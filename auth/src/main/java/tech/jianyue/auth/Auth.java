package tech.jianyue.auth;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

/**
 * 描述: 分发
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class Auth {
    public static final int ErrorUninstalled = -9991;              // 错误码，未安装客户端
    public static final int ErrorNotAction = -9992;                // 错误码，未设置Action
    public static final int ErrorParameter = -9993;                // 错误码，参数错误
    public static final int ErrorUnknown = -9994;                  // 错误码，未知错误

    public static final int Pay = 100;                          // 微信、支付宝、银联、华为，支付

    public static final int Rouse = 111;                        // 唤起微信、支付宝、华为，目前作为签约支付       微信(无回调) 唤起WebView，

    public static final int LOGIN = 121;                        // 微信、微博、QQ、华为，登录

    public static final int SHARE_TEXT = 131;                   // 微信、微博 分享文本
    public static final int SHARE_IMAGE = 132;                  // 微信、微博、QQ 分享图片
    public static final int SHARE_LINK = 133;                   // 微信、 微博 分享链接
    public static final int SHARE_VIDEO = 134;                  // 微信、微博、QQ 分享视频
    public static final int SHARE_MUSIC = 135;                  // 微信、QQ 分享音乐
    public static final int SHARE_PROGRAM = 136;                // 微信、QQ 分享小程序、应用

    public static final int WITH_HW = 146;                      // 华为 第三方标记
    public static final int WITH_QQ = 143;                      // QQ 第三方标记
    public static final int WITH_WB = 142;                      // 微博 第三方标记
    public static final int WITH_WX = 141;                      // 微信 第三方标记
    public static final int WITH_YL = 145;                      // 银联 第三方标记
    public static final int WITH_ZFB = 144;                     // 支付宝 第三方标记

    static HashSet<BaseAuthBuild> mBuilderSet = new HashSet<>();

    static void addBuilder(BaseAuthBuild build) {
        mBuilderSet.add(build);
    }

    static void removeBuilder(BaseAuthBuild build) {
        mBuilderSet.remove(build);
    }

    private Auth() { }

    static BaseAuthBuild getBuilder(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            for (BaseAuthBuild build: mBuilderSet) {
                if (sign.equals(build.mSign)) {
                    return build;
                }
            }
        }
        return null;
    }

    public static AuthBuilderInit init() {
        return new AuthBuilderInit();
    }

    public static BaseAuthBuildForHW withHW(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForHW().getAuthBuild(context);
    }

    public static BaseAuthBuildForQQ withQQ(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForQQ().getAuthBuild(context);
    }

    public static BaseAuthBuildForWB withWB(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForWB().getAuthBuild(context);
    }

    public static BaseAuthBuildForWX withWX(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForWX().getAuthBuild(context);
    }

    public static BaseAuthBuildForYL withYL(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForYL().getAuthBuild(context);
    }

    public static BaseAuthBuildForZFB withZFB(Context context) {
        return AuthBuilderInit.getInstance().getFactoryForZFB().getAuthBuild(context);
    }

    public static class AuthBuilderInit {
        private static AuthBuilderInit mInstance;

        private String QQAppID;

        private String WXAppID;
        private String WXSecret;

        private String WBAppKey;
        private String WBRedirectUrl;
        private String WBScope;

        private String HWMerchantID;
        private String HWAppID;
        private String HWKey;

        private AuthBuildFactory mFactoryForHW;
        private AuthBuildFactory mFactoryForQQ;
        private AuthBuildFactory mFactoryForWB;
        private AuthBuildFactory mFactoryForWX;
        private AuthBuildFactory mFactoryForYL;
        private AuthBuildFactory mFactoryForZFB;

        AuthBuilderInit() {}

        static AuthBuilderInit getInstance() {
            if (mInstance != null) {
                return mInstance;
            } else {
                throw new NullPointerException("添加依赖配置, 初始化");
            }
        }

        public String getQQAppID() {
            if (TextUtils.isEmpty(QQAppID)) {
                throw new NullPointerException("请配置 QQAppID");
            } else {
                return QQAppID;
            }
        }

        public String getWXAppID() {
            if (TextUtils.isEmpty(WXAppID)) {
                throw new NullPointerException("请配置 WXAppID");
            } else {
                return WXAppID;
            }
        }

        public String getWXSecret() {
            if (TextUtils.isEmpty(WXSecret)) {
                throw new NullPointerException("请配置 WXSecret");
            } else {
                return WXSecret;
            }
        }

        public String getWBAppKey() {
            if (TextUtils.isEmpty(WBAppKey)) {
                throw new NullPointerException("请配置 WBAppKey");
            } else {
                return WBAppKey;
            }
        }

        public String getWBRedirectUrl() {
            if (TextUtils.isEmpty(WBRedirectUrl)) {
                throw new NullPointerException("请配置 WBRedirectUrl");
            } else {
                return WBRedirectUrl;
            }
        }

        public String getWBScope() {
            if (TextUtils.isEmpty(WBScope)) {
                throw new NullPointerException("请配置 WBScope");
            } else {
                return WBScope;
            }
        }

        public String getHWMerchantID() {
            if (TextUtils.isEmpty(HWMerchantID)) {
                throw new NullPointerException("请配置 HWMerchantID");
            } else {
                return HWMerchantID;
            }
        }

        public String getHWAppID() {
            if (TextUtils.isEmpty(HWAppID)) {
                throw new NullPointerException("请配置 HWAppID");
            } else {
                return HWAppID;
            }
        }

        public String getHWKey() {
            if (TextUtils.isEmpty(HWKey)) {
                throw new NullPointerException("请配置 HWKey");
            } else {
                return HWKey;
            }
        }

        private AuthBuildFactory getFactoryForHW() {
            if (mFactoryForHW == null) {
                throw new NullPointerException("添加华为依赖, 并配置初始化");
            } else {
                return mFactoryForHW;
            }
        }

        private AuthBuildFactory getFactoryForQQ() {
            if (mFactoryForQQ == null) {
                throw new NullPointerException("添加QQ依赖, 并配置初始化");
            } else {
                return mFactoryForQQ;
            }
        }

        private AuthBuildFactory getFactoryForWB() {
            if (mFactoryForWB == null) {
                throw new NullPointerException("添加微博依赖, 并配置初始化");
            } else {
                return mFactoryForWB;
            }
        }

        private AuthBuildFactory getFactoryForWX() {
            if (mFactoryForWX == null) {
                throw new NullPointerException("添加微信依赖, 并配置初始化");
            } else {
                return mFactoryForWX;
            }
        }

        private AuthBuildFactory getFactoryForYL() {
            if (mFactoryForYL == null) {
                throw new NullPointerException("添加银联依赖, 并配置初始化");
            } else {
                return mFactoryForYL;
            }
        }

        private AuthBuildFactory getFactoryForZFB() {
            if (mFactoryForZFB == null) {
                throw new NullPointerException("添加支付宝依赖, 并配置初始化");
            } else {
                return mFactoryForZFB;
            }
        }

        public AuthBuilderInit setQQAppID(String appId) {
            QQAppID = appId;
            return this;
        }

        public AuthBuilderInit setWXAppID(String appID) {
            WXAppID = appID;
            return this;
        }

        public AuthBuilderInit setWXSecret(String secret) {
            WXSecret = secret;
            return this;
        }

        public AuthBuilderInit setWBAppKey(String key) {
            WBAppKey = key;
            return this;
        }

        public AuthBuilderInit setWBDedirectUrl(String url) {
            WBRedirectUrl = url;
            return this;
        }

        public AuthBuilderInit setWBScope(String scope) {
            WBScope = scope;
            return this;
        }

        public AuthBuilderInit setHWAppID(String id) {
            HWAppID = id;
            return this;
        }

        public AuthBuilderInit setHWMerchantID(String id) {
            HWMerchantID = id;
            return this;
        }

        /**
         * 配置华为公钥
         */
        public AuthBuilderInit setHWKey(String key) {
            HWKey = key;
            return this;
        }

        public AuthBuilderInit addFactoryForHW(AuthBuildFactory factory) {
            mFactoryForHW = factory;
            return this;
        }

        public AuthBuilderInit addFactoryForQQ(AuthBuildFactory factory) {
            mFactoryForQQ = factory;
            return this;
        }

        public AuthBuilderInit addFactoryForWB(AuthBuildFactory factory) {
            mFactoryForWB = factory;
            return this;
        }

        public AuthBuilderInit addFactoryForWX(AuthBuildFactory factory) {
            mFactoryForWX = factory;
            return this;
        }

        public AuthBuilderInit addFactoryForYL(AuthBuildFactory factory) {
            mFactoryForYL = factory;
            return this;
        }

        public AuthBuilderInit addFactoryForZFB(AuthBuildFactory factory) {
            mFactoryForZFB = factory;
            return this;
        }

        public void build() {
            mInstance = this;
        }
    }

    abstract static class AuthBuildFactory {
        abstract <T extends BaseAuthBuild> T getAuthBuild(Context context);
    }

    @IntDef({Rouse, Pay, LOGIN, SHARE_TEXT, SHARE_IMAGE, SHARE_LINK, SHARE_VIDEO, SHARE_MUSIC, SHARE_PROGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionWX { }

    @IntDef({LOGIN, SHARE_TEXT, SHARE_IMAGE, SHARE_LINK, SHARE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionWB { }

    @IntDef({LOGIN, SHARE_IMAGE, SHARE_MUSIC, SHARE_VIDEO, SHARE_PROGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionQQ { }

    @IntDef({Rouse, Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionZFB { }

    @IntDef({Pay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionYL { }

    @IntDef({LOGIN, Pay, Rouse})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionHW { }

    @IntDef({WITH_HW, WITH_QQ, WITH_WB, WITH_WX, WITH_YL, WITH_ZFB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WithThird { }
}