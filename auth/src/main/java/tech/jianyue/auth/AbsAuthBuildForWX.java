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
public abstract class AbsAuthBuildForWX extends AbsAuthBuild {
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

    AbsAuthBuildForWX(Context context) {
        super(context, Auth.WITH_WX);
    }

    abstract Controller getController(Activity activity);

    @Override
    public AbsAuthBuildForWX setAction(@Auth.ActionWX int action) {
        mAction = action;
        return this;
    }

    public AbsAuthBuildForWX rouseWeb(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForWX payPartnerId(String partnerId) {
        mPartnerId = partnerId;
        return this;
    }

    public AbsAuthBuildForWX payPrepayId(String prepayId) {
        mPrepayId = prepayId;
        return this;
    }

    public AbsAuthBuildForWX payPackageValue(String value) {
        mPackageValue = value;
        return this;
    }

    public AbsAuthBuildForWX payNonceStr(String str) {
        mNonceStr = str;
        return this;
    }

    public AbsAuthBuildForWX payTimestamp(String time) {
        mTimestamp = time;
        return this;
    }

    public AbsAuthBuildForWX paySign(String sign) {
        mPaySign = sign;
        return this;
    }

    public abstract AbsAuthBuildForWX shareToSession();

    public abstract AbsAuthBuildForWX shareToTimeline();

    public abstract AbsAuthBuildForWX shareToFavorite();

    public AbsAuthBuildForWX shareText(String text) {
        mText = text;
        return this;
    }

    public AbsAuthBuildForWX shareTextTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareTextDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForWX shareImage(Bitmap bitmap) {              // imageData 大小限制为 10MB
        mBitmap = bitmap;
        return this;
    }

    public AbsAuthBuildForWX shareImageTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareMusicTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareMusicDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForWX shareMusicImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public AbsAuthBuildForWX shareMusicUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForWX shareLinkTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareLinkDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForWX shareLinkImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public AbsAuthBuildForWX shareLinkUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForWX shareVideoTitle(String title) {
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareVideoDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForWX shareVideoImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public AbsAuthBuildForWX shareVideoUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForWX shareProgramTitle(String title) {             // 分享小程序
        mTitle = title;
        return this;
    }

    public AbsAuthBuildForWX shareProgramDescription(String description) {
        mDescription = description;
        return this;
    }

    public AbsAuthBuildForWX shareProgramImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 低版本微信打开的网络链接
     */
    public AbsAuthBuildForWX shareProgramUrl(String url) {
        mUrl = url;
        return this;
    }

    public AbsAuthBuildForWX shareProgramId(String id) {
        mID = id;
        return this;
    }

    public AbsAuthBuildForWX shareProgramPath(String path) {
        mPath = path;
        return this;
    }

    interface Controller {
        void destroy();

        void callback();
    }
}