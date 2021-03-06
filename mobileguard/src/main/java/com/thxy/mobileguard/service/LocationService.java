package com.thxy.mobileguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.thxy.mobileguard.utils.EncryptTools;
import com.thxy.mobileguard.utils.MyConstants;
import com.thxy.mobileguard.utils.SpTools;

import java.util.List;

/**
 * 定位的服务管理器
 */
public class LocationService extends Service {

    private LocationManager lm;
    private LocationListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                // 获取位置变化的结果
                float accuracy = location.getAccuracy();// 精确度,以米为单位
                double altitude = location.getAltitude();// 获取海拔高度
                double longitude = location.getLongitude();// 获取经度
                double latitude = location.getLatitude();// 获取纬度
                float speed = location.getSpeed();// 速度
                //定位信息
                StringBuilder tv_mess = new StringBuilder();
                tv_mess.append("accuracy:" + accuracy + "\n");
                tv_mess.append("altitude:" + altitude + "\n");
                tv_mess.append("longitude:" + longitude + "\n");
                tv_mess.append("latitude:" + latitude + "\n");
                tv_mess.append("speed:" + speed + "\n");
                // 发送短信

                String safeNumber = SpTools.getString(LocationService.this, MyConstants
                        .SAFENUMBER, "");
                safeNumber = EncryptTools.decryption(safeNumber);
                //发送短信给安全号码
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safeNumber, "", tv_mess + "", null, null);

                // 关闭gps
                stopSelf();//关闭自己
            }
        };
        //注册监听回调
        //获取所有提供的定位方式
        List<String> allProviders = lm.getAllProviders();
        for (String string : allProviders) {
            System.out.println(string + ">>定位方式");
        }
        //动态获取手机的最佳定位方式
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消定位的监听
        lm.removeUpdates(listener);
        lm = null;
        super.onDestroy();
    }
}
