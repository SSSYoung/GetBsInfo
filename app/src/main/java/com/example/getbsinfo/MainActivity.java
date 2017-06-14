package com.example.getbsinfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getbsinfo.BsInfo.BsInfo;
import com.example.getbsinfo.Utils.FileUtils;
import com.example.getbsinfo.Utils.WeatherUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GSMCellLocationActivity";
    private static final String TAG_Bs="基站信息";
    private static final String TAG_We="天气";
    private Handler handler=new Handler();
    private Runnable runnableBs;
    private Runnable runnableWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_ceaseSave = (Button)findViewById(R.id.bt_ceasesave);
        final Button bt_getBsInfo = (Button) findViewById(R.id.bt_getBsinfo);
        Button bt_deleteFile = (Button) findViewById(R.id.bt_deletefile);
        final Button bt_weatherInfo= (Button) findViewById(R.id.bt_weatherInfo);
        final TextView tv_BsInfo = (TextView) findViewById(R.id.tv_bsinfo);


        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        bt_getBsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //一次性申请多个权限,吧需要申请的权限放进集合，再一次性申请
                ArrayList<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (!permissionList.isEmpty()) {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);

                } else {
                    startBsAlarmtask();
                    bt_getBsInfo.setVisibility(View.INVISIBLE);

                }
            }
        });
        bt_deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleteSuccessBs = FileUtils.deleteInfo(getApplicationContext(), "bsInfo.txt");
                boolean isDeleteSuccessWe = FileUtils.deleteInfo(getApplicationContext(), "WeatherInfo.txt");
                if (isDeleteSuccessBs&&isDeleteSuccessWe) {
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "删除失败或不存在此文件", Toast.LENGTH_LONG).show();
                }
            }
        });
        bt_ceaseSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止handler往消息队列发数据
                handler.removeCallbacks(runnableBs);
                handler.removeCallbacks(runnableWeather);
                Toast.makeText(getApplicationContext(),"停止写入",Toast.LENGTH_SHORT).show();
            }
        });
        bt_weatherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWeatherAlarmtask();
                bt_weatherInfo.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void startWeatherAlarmtask() {

        //天气信息:每15分钟写入一次
        runnableWeather = new Runnable() {

            @Override
            public void run() {
                String fileName=String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+"日"+"天气信息"+".txt";
                String weatherInfo = WeatherUtils.queryWeather("pixian");
                boolean isSaveSuccess = FileUtils.saveInfo(getApplicationContext(), weatherInfo, fileName);
                if (isSaveSuccess){
                    Toast.makeText(getApplicationContext(),"存入天气文件成功",Toast.LENGTH_SHORT).show();
                    Log.i(TAG_We, "天气信息:"+weatherInfo);
                }else{
                    Toast.makeText(getApplicationContext(),"存入天气文件失败",Toast.LENGTH_SHORT).show();
                    Log.i(TAG_We, "天气信息获取失败");
                }
                //天气信息:每15分钟写入一次
                handler.postDelayed(this,5000);
            }
        };
        handler.postDelayed(runnableWeather,3000);

    }

    /**
     * 每隔20s将基站数据写入bsInfo.txt文件中
     */
    private void startBsAlarmtask() {
        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        handler = new Handler();

        runnableBs = new Runnable(){

            @Override
            public void run() {

                String fileName= String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+"日"+"基站信息"+".txt";
                String bsInfo = BsInfo.getBSInfo(mTelephonyManager);
//                boolean isSaveSuccess = FileUtils.saveInfo(getApplicationContext(), bsInfo, "bsInfo.txt");
                boolean isSaveSuccess = FileUtils.saveInfo(getApplicationContext(), bsInfo, fileName);
                if (isSaveSuccess){
                    Toast.makeText(getApplicationContext(),"存入基站信息文件成功",Toast.LENGTH_SHORT).show();
                    Log.i(TAG_Bs, "基站信息:"+bsInfo);
                }else{
                    Toast.makeText(getApplicationContext(),"存入基站信息文件失败",Toast.LENGTH_SHORT).show();
                    Log.i(TAG_Bs, "基站信息:获取失败");
                }
                //每30秒写入一次
                handler.postDelayed(this,5000);

            }

        };
        handler.postDelayed(runnableBs,1000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才可以", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    startBsAlarmtask();
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }


}
