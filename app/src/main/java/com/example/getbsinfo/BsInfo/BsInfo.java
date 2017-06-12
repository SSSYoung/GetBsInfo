package com.example.getbsinfo.BsInfo;

import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;

import com.example.getbsinfo.Utils.TimeUtils;

import java.util.List;

/**
 * Created by DK on 2017/6/10.
 */

public class BsInfo {
    /**
     * 获取所有连接基站的信息
     * @param mTelephonyManager
     * @return
     */
    public static String getBSInfo(TelephonyManager mTelephonyManager){
        List<CellInfo> cellInfoLists = mTelephonyManager.getAllCellInfo();
        StringBuffer sb = new StringBuffer(TimeUtils.getCurrentTime()+","+cellInfoLists.size()+",");
        int i=1;
        if (cellInfoLists!=null&&cellInfoLists.size()!=0){
            for (CellInfo info : cellInfoLists) {

                switch (info.getClass().getName()) {

                    case "android.telephony.CellInfoCdma":
                        /**
                         * cdma基站的情况
                         */
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                        CellIdentityCdma identityCdma = cellInfoCdma.getCellIdentity();
                        //主基站的ID
                        int cid = identityCdma.getBasestationId();
                        //基站的经纬度
                        int latitude = identityCdma.getLatitude();
                        int longitude = identityCdma.getLongitude();

                        CellSignalStrengthCdma signalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        int dbmCdma = signalStrengthCdma.getCdmaDbm();
                        sb.append(cid+","+dbmCdma+",");
//                    sb.append("CDMA基站:"+cid+"\t"+latitude+"\t"+longitude+"\t"+dbmCdma+"dBm\n");
                        break;
                    /**
                     *
                     * Lte基站的情况
                     */
                    case "android.telephony.CellInfoLte":

                        CellInfoLte cellInfoLte=(CellInfoLte)info;
                        CellIdentityLte identityLte = cellInfoLte.getCellIdentity();
                        int ci = identityLte.getCi();
                        int pci = identityLte.getPci();
                        int tac = identityLte.getTac();
                        CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();
                        int dbmLte = signalStrengthLte.getDbm();
                        sb.append(pci+","+dbmLte+",");
//                    sb.append(i+"号LTE基站:"+"\t"+dbmLte+"\n");
                        i++;
                        break;
                    default:
                        break;

                }


            }
        }

        sb.append("\n");
        return sb.toString();
    }

}
