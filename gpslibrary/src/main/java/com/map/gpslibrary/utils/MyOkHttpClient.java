package com.map.gpslibrary.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttpClient工具类
 * Created by Administrator on 2017/5/12.
 */
public class MyOkHttpClient {
    private final static String TAG = MyOkHttpClient.class.getSimpleName();
    //实例
    private OkHttpClient mOkHttpClient;
    //超时时间
    public static final int TIMEOUT=1000*60;
    //json请求
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //请求头
    private Headers mHeaders;
    //请求回调
    private OnOkHttpRequestListener onOkHttpRequestListener;
    //子线程与主线程切换
    private Handler handler = new Handler(Looper.getMainLooper());

    public static MyOkHttpClient getInstance(){
        return new MyOkHttpClient();
    }

    /**
     * 构造函数
     */
    public MyOkHttpClient() {
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        mOkHttpClient = new OkHttpClient();

        //设置超时
        mOkHttpClient.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).
                writeTimeout(TIMEOUT,TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * 设置请求回调监听
     * @param onOkHttpRequestListener
     */
    public MyOkHttpClient setOnOkHttpRequestListener(OnOkHttpRequestListener onOkHttpRequestListener) {
        this.onOkHttpRequestListener = onOkHttpRequestListener;
        return this;
    }

    /**
     * 设置请求头
     * @param headersParams 头部参数
     * @return 请求头
     */
    public  MyOkHttpClient setHeaders(Map<String, String> headersParams) {
        Headers.Builder headersbuilder = new Headers.Builder();
        if (headersParams != null) {
            Iterator<String> iterator = headersParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                headersbuilder.add(key, headersParams.get(key));
            }
        }
        mHeaders = headersbuilder.build();
        return this;
    }

    /**
     * 获取请求头
     * @return
     */
    public Headers getHeaders() {
        return mHeaders;
    }

    /**
     * 判断是否有设置请求头，有则先设置请求头
     * @return Request.Builder
     */
    private Request.Builder getRequestBuilder(){
        Request.Builder builder = new Request.Builder();
        if(getHeaders() != null){
            builder.headers(getHeaders());
        }
        return builder;
    }


    /**
     * post请求  json数据为body
     * @param url 请求URL地址
     * @param json json字符串
     */
    public void postJson(String url,String json){
        RequestBody body = RequestBody.create(JSON,json);
        Request request = getRequestBuilder().url(url).post(body).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerPostError(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handlerPostSuccess(response.body().string());
                } else {
                    handlerPostError(response.code() + ","+ response.message());
                }
            }
        });
    }

    /**
     * post请求  map数据转json作为body
     * @param url 请求URL地址
     * @param map Map数据
     */
    public void postMap(String url, Map<String,String> map){
        String json = new Gson().toJson(map);
        Log.e(TAG, "postMap: ====>"  + json );
        Log.e(TAG, "url: ====>"  + url );
        RequestBody body = RequestBody.create(JSON,json);
        Request request = getRequestBuilder().url(url).post(body).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerPostError(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handlerPostSuccess(response.body().string());
                } else {
                    handlerPostError(response.code() + ","+ response.message());
                }
            }
        });
    }

    /**
     * 上传文件
     * @param url 请求URL地址
     * @param file 文件
     * @param map 其他需要上传的参数, 为null时表示无其他参数
     */
    public void uploadFile(String url , File file, Map<String, String> map){

        MultipartBody.Builder multipartBody = new MultipartBody.Builder();

        multipartBody.setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file));

        if (map != null) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                multipartBody.addFormDataPart(next.getKey(), next.getValue());
            }
        }

        RequestBody requestBody = multipartBody.build();

        Request request = getRequestBuilder().url(url).post(requestBody).build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerPostError(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handlerPostSuccess(response.body().string());
                } else {
                    handlerPostError(response.code() + ","+ response.message());
                }
            }
        });
    }


    /**
     * get 请求
     * @param url 请求URL
     */
    public void getJson(String url){
        Log.e(TAG, "getJson: ====》" + url );
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handlerPostError(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handlerPostSuccess(response.body().string());
                } else {
                    handlerPostError(response.code() + ","+ response.message());
                }
            }
        });
    }


    /**
     * 请求成功
     * @param data 数据
     */
    public void handlerPostSuccess(final String data){
        Log.e(TAG, "handlerPostSuccess: =====>" + data);

        if(onOkHttpRequestListener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {//在主线程操作
                    onOkHttpRequestListener.onSusscess(data);
                }
            });
        }
    }

    /**
     * 请求错误
     * @param msg 错误消息
     */
    public void handlerPostError(final String msg) {
        Log.e(TAG, "handlerPostSuccess: =====>" + msg);
        if(onOkHttpRequestListener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onOkHttpRequestListener.onError(msg);
                }
            });
        }
    }

    public interface OnOkHttpRequestListener{
        //成功回调
        void onSusscess(String data);
        //失败
        void onError(String meg);
    }
}
