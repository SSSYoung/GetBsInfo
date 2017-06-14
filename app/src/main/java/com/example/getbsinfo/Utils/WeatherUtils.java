package com.example.getbsinfo.Utils;


import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by DK on 2017/6/12.
 */

public class WeatherUtils {
    private static String weatherInfo;

    /**
     *
     * @param city 如郫县,"pixian"
     * @return 返回天气信息
     */
    public static String queryWeather(String city){


        final String weatherurl="https://free-api.heweather.com/v5/now?city="+city+"&key=73a560261cc241b59ea355637f5d8fe3";
        httpUtils.sendOkHttpRequest(weatherurl, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                weatherInfo="InternetError";
            }

            @Override
            public void onResponse(Response response) throws IOException {
                weatherInfo=parseJSON(response.body().string());
            }
        });
        return weatherInfo;



    }
    /**
     * 具体的解析json
     * @param jsonData
     */
    private static String parseJSON(String jsonData) {
        String s=new String();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            //[]里面表达的是一个数组
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            JSONObject allJsonObject = jsonArray.getJSONObject(0);
            String status = allJsonObject.getString("status");
            if (status.equals("ok")){
                String locTime = allJsonObject.getJSONObject("basic").getJSONObject("update").getString("loc");
                JSONObject now = allJsonObject.getJSONObject("now");

                String temp = now.getString("tmp");
                String hum = now.getString("hum");
                //风 速
                String spd = now.getJSONObject("wind").getString("spd");
                String deg = now.getJSONObject("wind").getString("deg");
                //降雨量mm
                String pcpn = now.getString("pcpn");
                s=locTime+"\t"+temp+"\t"+hum+"\t"+spd+"\t"+deg+"\t"+pcpn+"\n";
            }else {
                s="fail";

            }
        } catch (JSONException e) {
            e.printStackTrace();
            s="error";
        }
        return s;
    }
}
