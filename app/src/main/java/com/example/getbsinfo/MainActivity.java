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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getbsinfo.BsInfo.BsInfo;
import com.example.getbsinfo.Utils.FileUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GSMCellLocationActivity";
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_ceaseSave = (Button)findViewById(R.id.bt_ceasesave);
        Button bt_getInfo = (Button) findViewById(R.id.bt_getinfo);
        Button bt_deleteFile = (Button) findViewById(R.id.bt_deletefile);
        TextView tv_BsInfo = (TextView) findViewById(R.id.tv_bsinfo);

        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        bt_getInfo.setOnClickListener(new View.OnClickListener() {
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
                    startAlarmtask();
//                    bsInfo = BsInfo.getBSInfo(mTelephonyManager);
                }
            }
        });
        bt_deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleteSuccess = FileUtils.deleteInfo(getApplicationContext(), "bsInfo.txt");
                if (isDeleteSuccess) {
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "删除失败或不存在此文件", Toast.LENGTH_LONG).show();
                }
            }
        });
        bt_ceaseSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                Toast.makeText(getApplicationContext(),"停止写入",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void startAlarmtask() {
        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run() {
                String bsInfo = BsInfo.getBSInfo(mTelephonyManager);
                boolean isSaveSuccess = FileUtils.saveInfo(getApplicationContext(), bsInfo, "bsInfo.txt");
                if (isSaveSuccess){
                    Toast.makeText(getApplicationContext(),"存入文件成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"存入文件失败",Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this,5000);

            }

        };
        handler.postDelayed(runnable,1000);

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
                    startAlarmtask();
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }


}
