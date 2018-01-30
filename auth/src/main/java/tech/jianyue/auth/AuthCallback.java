package tech.jianyue.auth;

public abstract class AuthCallback {

    public void onSuccessForLogin(UserInfoForThird info) {

    }

    public void onSuccessForShare() {

    }

    public void onCancel() {

    }

    public void onFailed(String msg) {

    }
}