package com.app.excel;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class testDemo {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
//        String url = "https://4share.vn/f/380e09090a0b000c/Sully [2016] BluRay REMUX 1080p AVC Atmos TrueHD7.1-JoRo.m2ts";
//        String  aa = URLEncoder.encode(url,"UTF-8");
//        System.out.println(aa);
        String url = "https://4share.vn/f/380e09090a0b000c/Sully [2016] BluRay REMUX 1080p AVC Atmos TrueHD7.1-JoRo.m2ts";
//        String url = "https://www.baidu.com";
        String wd = "智能+学习";
        String urlPrefix = "https://";
        String[] split = url.split(urlPrefix);
        System.out.println(split[1]);
        String encode = null;
        URL url1 = new URL(url);
        System.out.println(url1.getPath());
        int i = url.lastIndexOf("/");
        String start = url.substring(0, i + 1);
        String end = url.substring(i + 1, url.length());
        String endEncode = URLEncoder.encode(end, "utf-8");
        String newUrl = start + endEncode;

        System.out.println(newUrl);
        HttpClient4 httpClient4 = new HttpClient4();
        httpClient4.doGet("ssss");
    }
}
