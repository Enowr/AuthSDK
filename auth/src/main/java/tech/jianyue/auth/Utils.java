package tech.jianyue.auth;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 第三方相关工具类
 * 作者: WJ
 * 时间: 2018/1/19
 * 版本: 1.0
 */
public class Utils {
    private Utils() {
    }

    static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (needRecycle) {
                bmp.recycle();
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static String get(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);                                                    // 利用 string url 构建 URL 对象
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                return getStringFromInputStream(is);
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    static String post(String url, String content) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("POST");                                              // 设置请求方法为post
            conn.setReadTimeout(5000);                                                  // 设置读取超时为5秒
            conn.setConnectTimeout(10000);                                              // 设置连接网络超时为10秒
            conn.setDoOutput(true);                                                     // 设置此方法,允许向服务器输出内容

            // 获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
            OutputStream out = conn.getOutputStream();                                  // 获得一个输出流,向服务器写数据
            out.write(content.getBytes());
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();                                  // 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                return getStringFromInputStream(is);
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();                                                      // 关闭连接
            }
        }
        return null;
    }

    private static String getStringFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();                                                   // 把流中的数据转换成字符串,采用的编码是utf-8
        os.close();
        return state;
    }


    static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();              // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);           // 获取所有已安装程序的包信息
        List<String> pName = new ArrayList<>();                                         // 用于存储所有已安装程序的包名

        if (pinfo != null) {                                                            // 从pinfo中将包名字逐一取出，压入pName list中
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);                                             // 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    static String decodeURL(String url, String key) {
        String decode = null;
        try {
            decode = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(decode) || !decode.contains(key)) {
            return "";
        }
        String str = decode.substring(decode.indexOf(key) + key.length());
        String[] strings = str.split("[&]|[?]");
        return strings[0];
    }
}
