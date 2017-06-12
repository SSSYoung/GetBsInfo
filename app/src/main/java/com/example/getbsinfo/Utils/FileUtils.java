package com.example.getbsinfo.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by DK on 2017/6/12.
 */

public class FileUtils {
    /**
     *
     * @param context
     * @param bsInfo
     * @param fileName
     */
    public static boolean saveInfo(Context context,String bsInfo, String fileName){
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        //Log.i(TAG, "saveBsInfo: "+Environment.getExternalStorageState()+"路径"+Environment.getExternalStorageDirectory().toString()+"\t"+file.toString());
        BufferedWriter bw = null;
        boolean isSaveSuccess=false;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));

            bw.write(bsInfo);
            bw.flush();
            isSaveSuccess=true;

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"写入文件失败",Toast.LENGTH_SHORT);
            isSaveSuccess=false;
        }finally {
            if (bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSaveSuccess;
    }

    public static boolean deleteInfo(Context context, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()){
            return file.delete();
        }else {

            return false;
        }
    }
}
