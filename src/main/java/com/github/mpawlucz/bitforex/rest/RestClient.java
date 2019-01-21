package com.github.mpawlucz.bitforex.rest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestClient {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    public String get(String uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200){
                throw new RuntimeException("Status code: " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            final String s = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return s;
        } finally {
            response.close();
        }
    }

    public String post(String uri, String json, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(json));
        for (String key : headers.keySet()) {
            final String value = headers.get(key);
            httpPost.addHeader(key, value);
        }
        httpPost.addHeader("Content-type", "application/json");
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 204){
                String errorResponse = null;
                if (response.getEntity() != null){
                    errorResponse = EntityUtils.toString(response.getEntity());
                }
                throw new RuntimeException("Status code: " + statusCode
                        + ", json: " + json
                        + Optional.ofNullable(errorResponse).orElseGet(() -> ""));
            }
            HttpEntity entity = response.getEntity();
            if (entity == null){
                return null;
            }
            final String s = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return s;
        } finally {
            response.close();
        }
    }

    public String post(String uri, List<NameValuePair> params, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        for (String key : headers.keySet()) {
            final String value = headers.get(key);
            httpPost.addHeader(key, value);
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 204){
                String errorResponse = null;
                if (response.getEntity() != null){
                    errorResponse = EntityUtils.toString(response.getEntity());
                }
                throw new RuntimeException("Status code: " + statusCode
                        + Optional.ofNullable(errorResponse).orElseGet(() -> ""));
            }
            HttpEntity entity = response.getEntity();
            if (entity == null){
                return null;
            }
            final String s = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return s;
        } finally {
            response.close();
        }
    }

}
