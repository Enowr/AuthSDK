package tech.jianyue.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.StoryMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.util.ArrayList;

/**
 * 导入文件: jniLibs/xxx(libweibosdkcore.so)
 * 描述: 微博相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthBuildForWB extends Auth.Builder {
    private static boolean isInit = false;

    private Uri mUri;                                                   // 微博 Uri 地址
    private boolean mStory = false;                                     // 微博 是否分享到微博故事, 仅支持单图 和 视频
    private boolean mMultiImage = false;                                // 是否是多图分享
    private ArrayList<Uri> mImagePathList;                              // 微博 多图路径地址

    AuthBuildForWB(Context context) {
        super(context);
    }

    @Override
    void init() {
        if (!isInit) {
            if (TextUtils.isEmpty(Auth.AuthBuilder.WEIBO_APPKEY) || TextUtils.isEmpty(Auth.AuthBuilder.WEIBO_REDIRECT_URL) || TextUtils.isEmpty(Auth.AuthBuilder.WEIBO_SCOPE)) {
                throw new IllegalArgumentException("WEIBO_APPKEY | WEIBO_REDIRECT_URL | WEIBO_SCOPE was empty");
            } else {
                WbSdk.install(mContext, new AuthInfo(mContext, Auth.AuthBuilder.WEIBO_APPKEY, Auth.AuthBuilder.WEIBO_REDIRECT_URL, Auth.AuthBuilder.WEIBO_SCOPE));
                isInit = true;
            }
        }
    }

    @Override                       // 清理资源
    void destroy() {
        super.destroy();
        if (mImagePathList != null) {
            mImagePathList.clear();
            mImagePathList = null;
        }
        mUri = null;
    }

    @Override
    public AuthBuildForWB setAction(@Auth.ActionWB int action) {
        mAction = action;
        return this;
    }

    /**
     * 是否分享到微博故事, 仅支持单图 和 视频
     * 如果分享视频到微博故事, shareVideoUri shareVideoTitle shareVideoText shareVideoDescription 将失效, 只使用 uri 内容 , Uri 为本地视频
     */
    public AuthBuildForWB shareToStory() {
        mStory = true;
        return this;
    }

    public AuthBuildForWB shareText(String text) {
        mText = text;
        return this;
    }

    public AuthBuildForWB shareImage(Bitmap bitmap) {              // imageData 大小限制为 2MB
        mBitmap = bitmap;
        return this;
    }

    public AuthBuildForWB shareImageText(String text) {
        mText = text;
        return this;
    }

    /**
     * 分享多张图片, 本地图片 Uri 集合, shareImage 失效
     */
    public AuthBuildForWB shareImageMultiImage(ArrayList<Uri> list) {
        mMultiImage = true;
        mImagePathList = list;
        return this;
    }

    /**
     * 分享图片到微博故事时调用, shareImage shareImageText 将失效, 只使用 uri 内容, Uri 为本地图片
     */
    public AuthBuildForWB shareImageUri(Uri uri) {
        mUri = uri;
        return this;
    }

    public AuthBuildForWB shareLinkTitle(String title) {
        mTitle = title;
        return this;
    }

    public AuthBuildForWB shareLinkDescription(String description) {
        mDescription = description;
        return this;
    }

    public AuthBuildForWB shareLinkImage(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    /**
     * 网络链接
     */
    public AuthBuildForWB shareLinkUrl(String url) {
        mUrl = url;
        return this;
    }

    public AuthBuildForWB shareLinkText(String text) {
        mText = text;
        return this;
    }

    public AuthBuildForWB shareVideoTitle(String title) {
        mTitle = title;
        return this;
    }

    public AuthBuildForWB shareVideoText(String text) {
        mText = text;
        return this;
    }

    public AuthBuildForWB shareVideoDescription(String description) {
        mDescription = description;
        return this;
    }

    /**
     * 本地视频 Uri
     */
    public AuthBuildForWB shareVideoUri(Uri uri) {
        mUri = uri;
        return this;
    }

    @Override
    public void build(AuthCallback callback) {
        WbSdk.checkInit();
        super.build(callback);

        Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra("Sign", Sign);
        mContext.startActivity(intent);
    }

    void share(AuthActivity activity, WbShareHandler handler) {                        // 微博分享 API
        if (handler == null) {
            mCallback.onFailed("微博分享失败, WbShareHandler 为空");
            activity.finish();
        } else {
            switch (mAction) {
                case Auth.SHARE_TEXT:
                    shareText(activity, handler);
                    break;
                case Auth.SHARE_IMAGE:
                    shareImage(activity, handler);
                    break;
                case Auth.SHARE_LINK:
                    shareLink(activity, handler);
                    break;
                case Auth.SHARE_VIDEO:
                    shareVideo(activity, handler);
                    break;
                default:
                    if (mAction != -1) {
                        mCallback.onFailed("微博暂未支持的 Action");
                    }
                    activity.finish();
                    break;
            }
        }
    }

    private void shareText(AuthActivity activity, WbShareHandler handler) {
        if (TextUtils.isEmpty(mText)) {
            mCallback.onFailed("必须添加文本, 使用 shareText(str) ");
            activity.finish();
        } else {
            TextObject textObject = new TextObject();
            textObject.text = mText;
//            textObject.description = mDescription;        // 当前版本设置后无效果
//            textObject.title = mTitle;
//            textObject.actionUrl = mUrl;

            WeiboMultiMessage msg = new WeiboMultiMessage();
            msg.textObject = textObject;

            handler.shareMessage(msg, false);
        }
    }

    private void shareImage(AuthActivity activity, WbShareHandler handler) {
        if (mStory) {                                                       // 分享到 微博故事
            if (mUri != null) {
                StoryMessage sm = new StoryMessage();
                sm.setImageUri(mUri);
                handler.shareToStory(sm);
            } else {
                mCallback.onFailed("分享到微博故事, 必须添加 Uri, 且不为空, 使用 shareImageUri(uri) ");
                activity.finish();
            }
        } else if (mMultiImage) {
            // pathList 设置的是本地文件的路径,并且是当前应用可以访问的路径，现在不支持网络路径（多图分享依靠微博最新版本的支持，所以当分享到低版本的微博应用时，多图分享失效
            // 可以通过WbSdk.hasSupportMultiImage 方法判断是否支持多图分享,h5分享微博暂时不支持多图）多图分享接入程序必须有文件读写权限，否则会造成分享失败
            if (!WbSdk.supportMultiImage(activity)) {
                mCallback.onFailed("当前微博版本暂不支持多图分享");
                activity.finish();
            } else if (mImagePathList == null || mImagePathList.size() < 1) {
                mCallback.onFailed("必须添加多图路径集合, 使用 shareImageMultiImage(list) ");
                activity.finish();
            } else {
                TextObject textObject = new TextObject();                   // sdk 原因, 不添加 TextObject 分享会失败
                textObject.text = mText;

                MultiImageObject multiImageObject = new MultiImageObject();
                multiImageObject.setImageList(mImagePathList);

                WeiboMultiMessage msg = new WeiboMultiMessage();
                msg.textObject = textObject;
                msg.multiImageObject = multiImageObject;

                handler.shareMessage(msg, false);
            }
        } else if (mBitmap == null) {
            mCallback.onFailed("必须添加 Bitmap, 且不为空, 使用 shareImage(bitmap) ");
            activity.finish();
        } else {
            ImageObject imageObject = new ImageObject();                    // 图片大小限制2M
            imageObject.setImageObject(mBitmap);

            WeiboMultiMessage msg = new WeiboMultiMessage();
            msg.imageObject = imageObject;

            if (!TextUtils.isEmpty(mText)) {
                TextObject textObject = new TextObject();
                textObject.text = mText;
                msg.textObject = textObject;
            }

            handler.shareMessage(msg, false);
        }
    }

    private void shareLink(AuthActivity activity, WbShareHandler handler) {
        if (TextUtils.isEmpty(mUrl)) {
            mCallback.onFailed("必须添加链接, 且不为空, 使用 shareLinkUrl(url) ");
            activity.finish();
        } else if (mBitmap == null) {
            mCallback.onFailed("必须添加链接缩略图, 且不为空, 使用 shareLinkImage(bitmap) ");
            activity.finish();
        } else if (mTitle == null) {
            mCallback.onFailed("必须添加链接标题, 使用 shareLinkTitle(title) ");
            activity.finish();
        } else {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(mBitmap, 150, 150, true);
            WebpageObject mediaObject = new WebpageObject();
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = mTitle;
            mediaObject.description = mDescription;
            mediaObject.actionUrl = mUrl;
            mediaObject.setThumbImage(thumbBmp);                                    // 最终压缩过的缩略图大小不得超过 32kb
            mediaObject.defaultText = "WebPage";                                    // 默认文案

            WeiboMultiMessage msg = new WeiboMultiMessage();
            msg.mediaObject = mediaObject;

            if (!TextUtils.isEmpty(mText)) {
                TextObject textObject = new TextObject();
                textObject.text = mText;
                msg.textObject = textObject;
            }

            handler.shareMessage(msg, false);
        }
    }

    private void shareVideo(AuthActivity activity, WbShareHandler handler) {
        if (!WbSdk.supportMultiImage(activity)) {
            mCallback.onFailed("当前微博版本暂不支持视频分享");
            activity.finish();
        } else if (mUri == null) {
            mCallback.onFailed("必须添加视频Uri, 且不为空, 使用 shareVideoUri(uri) ");
            activity.finish();
        } else if (mStory) {                                         // 分享到 微博故事
            StoryMessage sm = new StoryMessage();
            sm.setVideoUri(mUri);
            handler.shareToStory(sm);
        } else {
            TextObject textObject = new TextObject();           // sdk 原因, 不添加 TextObject 分享会失败
            textObject.text = mText;

            VideoSourceObject videoSourceObject = new VideoSourceObject();
            videoSourceObject.videoPath = mUri;
            videoSourceObject.title = mTitle;
            videoSourceObject.description = mDescription;

            WeiboMultiMessage msg = new WeiboMultiMessage();
            msg.textObject = textObject;
            msg.videoSourceObject = videoSourceObject;

            handler.shareMessage(msg, false);
        }
    }

    // 通过 AuthActivity 调用
    void getInfo(Oauth2AccessToken oauth) {
        new AuthBuildForWB.GetInfo(mCallback).execute(oauth);
    }

    private static class GetInfo extends AsyncTask<Oauth2AccessToken, Void, UserInfoForThird> {
        private AuthCallback callback;                                      // 回调函数

        GetInfo(AuthCallback callback) {
            this.callback = callback;
        }

        @Override
        protected UserInfoForThird doInBackground(Oauth2AccessToken... oauths) {
            try {
                Oauth2AccessToken oauth = oauths[0];
                String url = "https://api.weibo.com/2/users/show.json?"
                        + "access_token="
                        + oauth.getToken()
                        + "&uid="
                        + oauth.getUid();
                // 微博登录, 获取用户信息
                return new UserInfoForThird().initForWB(Utils.get(url), oauth);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserInfoForThird info) {
            super.onPostExecute(info);
            if (info != null) {
                callback.onSuccessForLogin(info);
            } else {
                callback.onFailed("微博登录失败");
            }
            callback = null;
        }
    }
}