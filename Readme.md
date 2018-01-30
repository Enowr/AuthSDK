1. gradle.properties 文件添加配置:  
    QQ_APPID=""  
    QQ_SCHEME=""  

    WECHAT_APPID=""  
    WECHAT_SECRET=""  (微信获取用户信息)

    WEIBO_APPKEY=""  
    WEIBO_REDIRECT_URL=""  
    WEIBO_SCOPE=""  

2. (微博添加配置)根目录添加 maven 仓库地址:  
    maven { url "https://dl.bintray.com/thelasterstar/maven/" }
    
3. (微信添加配置)引用项目包名下添加 wxapi 包文件:  
    内部添加 WXEntryActivity 类, 并继承 AuthActivity 类;  
    AndroidManifest.xml 文件添加配置:  
    
    ```
    <activity
     android:name=".wxapi.WXEntryActivity"
     android:label="@string/app_name"
     android:exported="true"/>
    ```
    
4. 分享图片限制:  
   微信 图片限制在 10M 内, 缩略图为 32K;
   微博 图片限制在 2M 内, 缩略图为 32K;
   QQ shareTextAndImageImage(str) 可以为本地路径
    
5. 微信分享视频\音乐 为网络链接
   微博分享视频 为本地 Uri
   
   