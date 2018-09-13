package tech.jianyue.auth;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * 描述: 微信相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public abstract class BaseAuthBuildForWX extends BaseAuthBuild {
    int mShareType = -100;                                      // 分享类型
    String mID;                                                 // 小程序 ID
    String mPath;                                               // 小程序 Path

    String mPartnerId;                                          // 微信支付 PartnerId. 微信支付分配的商户号
    String mPrepayId;                                           // 微信返回的支付交易会话ID
    String mPackageValue;                                       // 暂填写固定值Sign=WXPay, 但还是由外部传入, 避免以后变更
    String mNonceStr;                                           // 随机字符串，不长于32位
    String mTimestamp;                                          // 时间戳
    String mPaySign;                                            // 签名

    String mTitle;                                              // 标题
    String mText;                                               // 文本
    String mDescription;                                        // 描述
    Bitmap mBitmap;                                             // 图片
    String mUrl;                                                // Url

    BaseAuthBuildForWX(Context context) {
        super(context, Auth.WITH_WX);
    }

    abstract Controller getController(Activity activity);

    @Override
    public BaseAuthBuildForWX setAction(@Auth.ActionWX int action) {
        mAction = action;
        return this;
    }

    public BaseAuthBuildForWX rouseWeb(String url) {
        mUrl = url;
        return this;
    }

    public BaseAuthBuildForWX payPartnerId(String partnerId) {
        mPartnerId = partnerId;
        return this;
    }

    public BaseAuthBuildForWX payPrepayId(String prepayId) {
        mPrepayId = prepayId;
        return this;
    }

    public BaseAuthBuildForWX payPackageValue(String value) {
        mPackageValue = value;
        return this;
    }

    public BaseAuthBuildForWX payNonceStr(String str) {
        mNonceStr = str;
        return this;
    }

    public BaseAuthBuildForWX payTimestamp(String time) {
        mTimestamp = time;
        return this;
    }

    public BaseAuthBuildForWX paySign(String sign) {
        mPaySign = sign;
        return this;
    }

    public abstract BaseAuthBuildForWX shareToSession();

    public abstract BaseAuthBuildForWX shareToTimeline();

    public abstract BaseAuthBuildForWX shareToFavorite();

    public BaseAuthBuildForWX shareText(String text) {
        mText = text;
        return this;
    }

    public BaseAuthBuildForWX shareTextTitle(String title) {
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareTextDescription(String description) {
        mDescription = description;
        return this;
    }

    public BaseAuthBuildForWX shareImage(Bitmap bitmap) {              // imageData 大小限制为 10MB
        mBitmap = bitmap;
        return this;
    }

    public BaseAuthBuildForWX shareImageTitle(String title) {
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareMusicTitle(String title) {
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareMusicDescription(String description) {
        mDescription = description;
        return this;
    }

    public BaseAuthBuildForWX shareMusicImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public BaseAuthBuildForWX shareMusicUrl(String url) {
        mUrl = url;
        return this;
    }

    public BaseAuthBuildForWX shareLinkTitle(String title) {
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareLinkDescription(String description) {
        mDescription = description;
        return this;
    }

    public BaseAuthBuildForWX shareLinkImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public BaseAuthBuildForWX shareLinkUrl(String url) {
        mUrl = url;
        return this;
    }

    public BaseAuthBuildForWX shareVideoTitle(String title) {
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareVideoDescription(String description) {
        mDescription = description;
        return this;
    }

    public BaseAuthBuildForWX shareVideoImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public BaseAuthBuildForWX shareVideoUrl(String url) {
        mUrl = url;
        return this;
    }

    public BaseAuthBuildForWX shareProgramTitle(String title) {             // 分享小程序
        mTitle = title;
        return this;
    }

    public BaseAuthBuildForWX shareProgramDescription(String description) {
        mDescription = description;
        return this;
    }

    public BaseAuthBuildForWX shareProgramImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 低版本微信打开的网络链接
     */
    public BaseAuthBuildForWX shareProgramUrl(String url) {
        mUrl = url;
        return this;
    }

    public BaseAuthBuildForWX shareProgramId(String id) {
        mID = id;
        return this;
    }

    public BaseAuthBuildForWX shareProgramPath(String path) {
        mPath = path;
        return this;
    }

    interface Controller {
        void destroy();

        void callback();
    }
}