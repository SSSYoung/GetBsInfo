package com.example.getbsinfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GSMCellLocationActivity";
    private String bsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_getInfo = (Button) findViewById(R.id.bt_getinfo);
        Button bt_ceaseInfo = (Button) findViewById(R.id.bt_ceaseinfo);
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
                } else {
                    bsInfo = BsInfo.getBSInfo(mTelephonyManager);
                }
                Log.i(TAG, "onCreate: " + bsInfo);

                saveBsInfo(bsInfo);

            }
        });


    }

    /**
     * 把Bs信息写入文件
     *
     * @param bsInfo 在手机内部存储的bsInfo.txt
     */
    private void saveBsInfo(String bsInfo) {

        File file = new File(Environment.getExternalStorageDirectory(), "bsInfo.txt");
        Log.i(TAG, "saveBsInfo: "+Environment.getExternalStorageState()+"路径"+Environment.getExternalStorageDirectory().toString()+"\t"+file.toString());
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));

            bw.write(bsInfo);
            bw.flush();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"写入文件失败",Toast.LENGTH_SHORT);
        }finally {
            if (bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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
                    bsInfo = BsInfo.getBSInfo(mTelephonyManager);
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }
//    public void save(String bsInfo){
//        FileOutputStream out=null;
//        BufferedWriter writer=null;
//        try {
//            out = openFileOutput("BsInfo.txt", Context.MODE_PRIVATE);
//            writer=new BufferedWriter((new OutputStreamWriter(out)));
//            writer.write(bsInfo);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"写入文件失败",Toast.LENGTH_LONG);
//        }finally {
//            if (writer!=null){
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
