package tech.jianyue.auth;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 描述: QQ 相关授权操作
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class AuthBuildForQQ extends Auth.Builder {
    private Tencent mTencent;

    private String mImageUrl;               // 图片 url
    private String mAudioUrl;               // 音频 url
    private String mArk;
    private String mName;
    private Boolean mQzone = null;
    private boolean mMood = false;
    private boolean mMultiImage = false;
    private String mBack;
    private String mScene;
    private ArrayList<String> mImageList;   // 图片集合, 最多支持9张图片，多余的图片会被丢弃; 说说 <=9张图片为发表说说，>9张为上传图片到相册,只支持本地图片

    AuthBuildForQQ(Context context) {
        super(context, Auth.WITH_QQ);
    }

    @Override
    void init() {
        if (TextUtils.isEmpty(Auth.AuthBuilder.QQAppID)) {
            throw new IllegalArgumentException("QQAppID was empty");
        } else {
            mTencent = Tencent.createInstance(Auth.AuthBuilder.QQAppID, mContext.getApplicationContext());
        }
    }

    @Override                       // 清理资源
    void destroy() {
        super.destroy();
        mTencent = null;
        if (mImageList != null) {
            mImageList.clear();
            mImageList = null;
        }
    }

    Tencent getQQApi() {
        return mTencent;
    }

    @Override
    public AuthBuildForQQ setAction(@Auth.ActionQQ int action) {
        mAction = action;
        return this;
    }

    /**
     * 三种状态: 默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框(true, 直接打开QZone的对话框, false 隐藏分享到QZone)
     */
    public AuthBuildForQQ shareToQzone(boolean qzone) {
        mQzone = qzone;
        return this;
    }

    /**
     * 分享到说说
     */
    public AuthBuildForQQ shareImageToMood() {
        mMood = true;
        return this;
    }

    /**
     * 单图只支持本地路径, 图文支持分享图片的URL或者本地路径, 不设置 Title 为单图, 否则为多图或图文
     */
    public AuthBuildForQQ shareImageUrl(String url) {
        mImageUrl = url;
        return this;
    }

    /**
     * 最长 30 个字符
     */
    public AuthBuildForQQ shareImageTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * 网络链接, 点击后跳转 url
     */
    public AuthBuildForQQ shareImageTargetUrl(String url) {
        mUrl = url;
        return this;
    }

    public AuthBuildForQQ shareImageDescription(String description) {
        mDescription = description;
        return this;
    }

    // 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替              // 测试后无效果
    public AuthBuildForQQ shareImageName(String name) {
        mName = name;
        return this;
    }

    /**
     * 可选, 分享携带ARK JSON串
     */
    public AuthBuildForQQ shareImageArk(String ark) {
        mArk = ark;
        return this;
    }

    /**
     * 可选, 调用了 shareImageToMood 后生效, 区分分享场景，用于异化feeds点击行为和小尾巴展示
     */
    public AuthBuildForQQ shareImageScene(String scene) {
        mScene = scene;
        return this;
    }

    /**
     * 可选, 调用了 shareImageToMood 后生效, 游戏自定义字段，点击分享消息回到游戏时回传给游戏
     */
    public AuthBuildForQQ shareImageBack(String back) {
        mBack = back;
        return this;
    }

    /**
     * 调用后 shareImageUrl 失效, 且默认为(仅支持)发表到QQ空间, 以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
     * QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持
     *
     * 如果调用了 shareToMood 则发表说说: 说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
     */
    public AuthBuildForQQ shareImageMultiImage(ArrayList<String> list) {
        mMultiImage = true;
        mImageList = list;
        return this;
    }

    public AuthBuildForQQ shareMusicTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
     */
    public AuthBuildForQQ shareMusicUrl(String url) {
        mAudioUrl = url;
        return this;
    }

    /**
     * 网络链接
     */
    public AuthBuildForQQ shareMusicTargetUrl(String url) {
        mUrl = url;
        return this;
    }

    /**
     * 分享图片的URL或者本地路径
     */
    public AuthBuildForQQ shareMusicImage(String url) {
        mImageUrl = url;
        return this;
    }

    public AuthBuildForQQ shareMusicDescription(String description) {
        mDescription = description;
        return this;
    }

    public AuthBuildForQQ shareMusicName(String name) {
        mName = name;
        return this;
    }

    public AuthBuildForQQ shareVideoUrl(String url) {
        mUrl = url;
        return this;
    }

    public AuthBuildForQQ shareVideoScene(String scene) {
        mScene = scene;
        return this;
    }

    public AuthBuildForQQ shareVideoBack(String back) {
        mBack = back;
        return this;
    }

    public AuthBuildForQQ shareProgramTitle(String title) {
        mTitle = title;
        return this;
    }

    public AuthBuildForQQ shareProgramDescription(String description) {
        mDescription = description;
        return this;
    }

    /**
     * 分享图片的URL或者本地路径
     */
    public AuthBuildForQQ shareProgramImage(String url) {
        mImageUrl = url;
        return this;
    }

    public AuthBuildForQQ shareProgramName(String name) {
        mName = name;
        return this;
    }

    @Override
    public void build(AuthCallback callback) {
        super.build(callback);
        if (mTencent.isQQInstalled(mContext)) {
            Intent intent = new Intent(mContext, AuthActivity.class);
            intent.putExtra("Sign", Sign);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            mCallback.onFailed("未安装QQ客户端");
            destroy();
        }
    }

    void share(AuthActivity activity) {
        switch (mAction) {
            case Auth.SHARE_IMAGE:
                shareImage(activity);
                break;
            case Auth.SHARE_MUSIC:
                shareMusic(activity);
                break;
            case Auth.SHARE_VIDEO:
                shareVideo(activity);
                break;
            case Auth.SHARE_PROGRAM:
                shareProgram(activity);
                break;
            default:
                if (mAction != Auth.UNKNOWN_TYPE) {
                    mCallback.onFailed("QQ 暂未支持的 Action");
                }
                activity.finish();
                break;
        }
    }

    private void shareImage(AuthActivity activity) {
        if (mMood) {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mTitle);               // 传图和传视频接口会过滤第三方传过来的自带描述，目的为了鼓励用户自行输入有价值信息
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, mImageList);// 说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
            Bundle extParams = new Bundle();
            extParams.putString(QzonePublish.HULIAN_EXTRA_SCENE, mScene);           // 区分分享场景，用于异化feeds点击行为和小尾巴展示
            extParams.putString(QzonePublish.HULIAN_CALL_BACK, mBack);              // 游戏自定义字段，点击分享消息回到游戏时回传给游戏
            params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, extParams);
            mTencent.publishToQzone(activity, params, activity);
        } else if (TextUtils.isEmpty(mTitle)) {
            if (TextUtils.isEmpty(mImageUrl)) {
                mCallback.onFailed("必须添加Image本地路径, 且不为空, 使用 shareImageUrl(url), 若使用多图分享或图文分享,还需要添加 Title, 使用 shareImageTitle(str) ");
                activity.finish();
            } else {
                Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mImageUrl);
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mName);
                if (mQzone != null) {
                    if (mQzone) {       // 分享时自动打开分享到QZone的对话框。
                        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                    } else {            // 分享时隐藏分享到QZone
                        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
                    }
                }
                mTencent.shareToQQ(activity, params, activity);
            }
        } else if (TextUtils.isEmpty(mUrl)) {
            mCallback.onFailed("必须添加跳转链接, 且不为空, 使用 shareImageTargetUrl(url) ");
            activity.finish();
        } else if (mMultiImage) {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);             // 必填
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mDescription);     // 选填
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mUrl);          // 必填
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, mImageList);
            mTencent.shareToQzone(activity, params, activity);
        } else {
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mName);
            if (!TextUtils.isEmpty(mArk)) {
                params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, mArk);
            }
            if (mQzone != null) {
                if (mQzone) {       // 分享时自动打开分享到QZone的对话框。
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                } else {            // 分享时隐藏分享到QZone
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
                }
            }
            mTencent.shareToQQ(activity, params, activity);
        }
    }

    private void shareMusic(AuthActivity activity) {
        if (TextUtils.isEmpty(mTitle)) {
            mCallback.onFailed("必须添加标题, 使用 shareMusicTitle(str) ");
            activity.finish();
        } else if (TextUtils.isEmpty(mUrl)) {
            mCallback.onFailed("必须添加点击后跳转链接, 且不为空, 使用 shareMusicTargetUrl(url) ");
            activity.finish();
        } else if (TextUtils.isEmpty(mAudioUrl)) {
            mCallback.onFailed("必须添加音乐链接, 不支持本地音乐, 使用 shareMusicUrl(url) ");
            activity.finish();
        } else {
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);                // 这条分享消息被好友点击后的跳转URL
            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, mAudioUrl);             // 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);             // 分享图片的URL或者本地路径
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mName);
            if (mQzone != null) {
                if (mQzone) {       // 分享时自动打开分享到QZone的对话框。
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                } else {            // 分享时隐藏分享到QZone
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
                }
            }
            mTencent.shareToQQ(activity, params, activity);
        }
    }

    private void shareVideo(AuthActivity activity) {
        if (TextUtils.isEmpty(mUrl)) {
            mCallback.onFailed("必须添加Video链接, 且不为空, 使用 shareVideoUrl(url) ");
            activity.finish();
        } else {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO);
            params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, mUrl);
            Bundle extParams = new Bundle();
            extParams.putString(QzonePublish.HULIAN_EXTRA_SCENE, mScene);           // 区分分享场景，用于异化feeds点击行为和小尾巴展示
            extParams.putString(QzonePublish.HULIAN_CALL_BACK, mBack);              // 游戏自定义字段，点击分享消息回到游戏时回传给游戏
            params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, extParams);
            mTencent.publishToQzone(activity, params, activity);
        }
    }

    private void shareProgram(AuthActivity activity) {
        if (TextUtils.isEmpty(mTitle)) {
            mCallback.onFailed("必须添加标题, 使用 shareProgramTitle(str) ");
            activity.finish();
        } else {
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mName);
            if (mQzone != null) {
                if (mQzone) {       // 分享时自动打开分享到QZone的对话框。
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                } else {            // 分享时隐藏分享到QZone
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
                }
            }
            mTencent.shareToQQ(activity, params, activity);
        }
    }

    // 通过 AuthActivity 调用
    void getInfo(JSONObject object) {
        new GetInfo(mCallback, mContext.getApplicationContext(), mTencent).execute(object);
    }

    private static class GetInfo extends AsyncTask<JSONObject, Void, UserInfo> {
        String openid;
        String access_token;
        int expires_in;
        long expires_time;
        private AuthCallback callback;                                      // 回调函数
        private Context context;
        private Tencent tencent;

        GetInfo(AuthCallback callback, Context context, Tencent tencent) {
            this.callback = callback;
            this.context = context;
            this.tencent = tencent;
        }

        @Override
        protected UserInfo doInBackground(JSONObject... objects) {
            try {
                JSONObject object = objects[0];
                if (object != null && object.length() != 0 && object.optInt("ret", -1) == 0) {
                    openid = object.getString("openid");
                    access_token = object.getString("access_token");
                    expires_in = object.optInt("expires_in", 0);
                    expires_time = object.optLong("expires_time", 0);

                    if (!TextUtils.isEmpty(access_token) && !TextUtils.isEmpty(openid)) {
                        tencent.setAccessToken(access_token, String.valueOf(expires_in));
                        tencent.setOpenId(openid);
                    }
                    return new UserInfo(context, tencent.getQQToken());
                } else {
                    callback.onFailed(object.getString("msg"));
                    return null;
                }
            } catch (Exception e) {
                callback.onFailed(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserInfo info) {
            super.onPostExecute(info);
            if (info != null) {
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            UserInfoForThird info = new UserInfoForThird().initForQQ((JSONObject) o, openid, access_token, expires_time, expires_in);
                            if (info != null) {
                                callback.onSuccessForLogin(info);
                            } else {
                                callback.onFailed("QQ 登录失败");
                            }
                            destroy();
                        } catch (Exception e) {
                            callback.onFailed("QQ 登录失败");
                            destroy();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        callback.onFailed(uiError.errorMessage);
                        destroy();
                    }

                    @Override
                    public void onCancel() {
                        callback.onCancel();
                        destroy();
                    }
                });
            } else {
                callback.onFailed("QQ登录失败");
                destroy();
            }
        }

        private void destroy() {
            callback = null;
            context = null;
            tencent = null;
        }
    }
}