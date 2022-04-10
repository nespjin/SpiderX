/*
 * Copyright (c) 2020-2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.internet;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/8/19 9:39
 * @Project NespMovie
 **/
@Deprecated
public class NespOkHttp {

    public static String getResponseBody(String url) throws IOException {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .build();
        final Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public static void getResponseBody(String url, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * get的同步请求
     * 需要在子线程执行
     *
     * @param url
     */
    public static void getDataSync(String url) {
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
            Request request = getRequest(url);
            Response response = null;
            response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get的异步请求
     *
     * @param url
     * @param callback
     */
    public static void getDataAsync(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = getRequest(url);
        client.newCall(request).enqueue(callback);
    }

//    new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//        }
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            if(response.isSuccessful()){//回调的方法执行在子线程。
//
//            }
//        }
//    }

    /**
     * post请求的使用方法
     * <p>
     * 使用FormBody传递键值对参数
     */
    public static void postDataWithParame(String url, String paramName, String paramValue, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add(paramName, paramValue);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(callback);//回调方法的使用与get异步请求相同，此时略。
    }

    /**
     * 使用RequestBody传递Json或File对象
     *
     * @param url
     * @param filePath
     * @param callback
     */
    public static void postDataWithFile(String url, String filePath, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType fileType = MediaType.parse("File/*");//数据类型为json格式，
        File file = new File(filePath);//file对象.
        RequestBody body = RequestBody.create(fileType, file);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    /**
     * 使用RequestBody传递Json或File对象
     *
     * @param url
     * @param json
     * @param callback
     */
    public static void postDataWithJson(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    public static Response postDataWithJson(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(body)
                .build();
       return client.newCall(request).execute();
    }

    /**
     * 使用RequestBody传递Json或File对象
     *
     * @param url
     * @param json
     * @param callback
     */
    public static void putDataWithJson(String url, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .put(body)
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    public static void getDataWithJson(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .get()
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    public static void deleteDataWithJson(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .delete()
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    /**
     * 使用MultipartBody同时传递键值对参数和File对象
     *
     * @param url
     * @param params
     * @param filePath
     * @param callback
     */
    public static void postDataWithFileParame(String url, HashMap<String, String> params, String filePath, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        File file = new File(filePath);

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());//添加键值对参数
        }
        multipartBodyBuilder.addFormDataPart("file", new File(filePath).getName(), RequestBody.create(MediaType.parse("file/*"), file));//添加文件
        MultipartBody multipartBody = multipartBodyBuilder.build();

        final Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(multipartBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 自定义RequestBody实现流的上传
     *
     * @param url
     * @param filePath
     * @param callback
     */
    public static void postDataWithStream(String url, String filePath, Callback callback) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。

        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {//重写writeTo方法
                FileInputStream fio = new FileInputStream(new File(filePath));
                byte[] buffer = new byte[1024 * 8];
                if (fio.read(buffer) != -1) {
                    sink.write(buffer);
                }
            }
        };

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);//此处省略回调方法。
    }

    private static Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
                .addHeader("token", "myToken")
                .build();
    }

}
