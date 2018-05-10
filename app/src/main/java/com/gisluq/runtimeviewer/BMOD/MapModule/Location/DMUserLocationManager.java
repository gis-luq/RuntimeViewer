package com.gisluq.runtimeviewer.BMOD.MapModule.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;


import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.DialogUtils;

import java.text.DecimalFormat;

/**
 * 用户扩展位置显示类
 * Created by luq on 2017/3/11.
 */
public class DMUserLocationManager {

    //定位
    final String TAG = "DMUserLocationManager";

    private Context context = null;
    private MapView mapView = null;

    private GraphicsOverlay locgraphicOverlay = null;//定制显示图层
    public LocationManager loctionManager;//声明LocationManager对象
    public String provider = null;
//    public Location locTemp = null;//位置零时存储
    public Point locationPoint = null;//当前位置点

    private TextView txtLocInfo = null;//显示位置信息

    private boolean isFirstStart = true;

    public boolean isLocating = false;//是的定位中

    public DMUserLocationManager(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        //设置位置显示图层
        locgraphicOverlay = new GraphicsOverlay();

        txtLocInfo = (TextView) ((Activity) context).findViewById(R.id.activity_map_mapview_locationInfo);

    }

    /**
     * 获取当前缓存位置
     * @return
     */
    public Point getLocationPoint() {
        return locationPoint;
    }

    /**
     * 初始化loctionManager
     * @param t 位置时间采集频率
     */
    public void intiLocationManager(int t) {
        try {
            String contextService = Context.LOCATION_SERVICE;
            //通过系统服务，取得LocationManager对象
            loctionManager = (LocationManager) context.getSystemService(contextService);
            //使用标准集合，让系统自动选择可用的最佳位置提供器，提供位置
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
            criteria.setAltitudeRequired(true);//要求海拔
            criteria.setBearingRequired(true);//要求方位
            criteria.setCostAllowed(true);//允许有花费
            criteria.setSpeedRequired(true);
            criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);//功耗
            //从可用的位置提供器中，匹配以上标准的最佳提供器
            provider = loctionManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                DialogUtils.showDialog(context,"请检查定位权限问题");
                return;
            }
            loctionManager.requestLocationUpdates(provider, t * 1000, 0, mListener);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 开始
     */
    public void start() {
        this.mapView.getGraphicsOverlays().add(locgraphicOverlay);
        intiLocationManager(2);//初始化LocationManager
        isFirstStart = true;
        isLocating = true;
    }

    /**
     * 停止
     */
    public void stop() {
        try {
            isLocating = false;
            loctionManager.removeUpdates(mListener);
            loctionManager = null;
            locgraphicOverlay.getGraphics().clear();//清空定位图层显示内容
            this.mapView.getGraphicsOverlays().remove(locgraphicOverlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 位置采集监听事件
     */
    public final LocationListener mListener = new LocationListener() {

        DecimalFormat df = new DecimalFormat("###.000000");

        public void onLocationChanged(Location lo) {
            try {
                if (lo != null) {
//                    locTemp = lo;//记录当前坐标位置并缓存与locTemp
                    if (mapView != null) {

                        double lon = lo.getLongitude();
                        double lat = lo.getLatitude();

                        //定位地图上
//                        Point pt_location = new Point(lon, lat);//GPS设备获取的坐标
                        double[] locex= EvilTransform.transform(lon,lat);//坐标偏移
                        Point pt_location = new Point(locex[0], locex[1], SpatialReference.create(4326));//加偏后坐标信息

                        locationPoint = pt_location;//缓存备用
                        Point pt_mapsr = (Point) GeometryEngine.project(pt_location,mapView.getSpatialReference());//转换到当前地图坐标
                        if (locgraphicOverlay != null) {
                            //显示到地图上
                            locgraphicOverlay.getGraphics().clear();
                            Drawable drawable = context.getDrawable(R.drawable.arcgisruntime_location_display_default_symbol);
                            ListenableFuture<PictureMarkerSymbol> symbol = PictureMarkerSymbol.createAsync((BitmapDrawable) drawable);
                            Graphic g = new Graphic(pt_mapsr, symbol.get());
                            locgraphicOverlay.getGraphics().add(g);
                            if (isFirstStart) {
                                mapView.setViewpointCenterAsync(pt_mapsr);//定位到中心
                                isFirstStart = false;//仅定位到中心一次
                            }
                        }

                        Log.d(TAG, "Lon:" + df.format(lon) + " , " + "Lat:" + df.format(lat));
                        txtLocInfo.setText(String.format(df.format(lon) + " , " + df.format(lat)));
                    }
                }
            } catch (Exception e) {
                // TODO: GPS异常
//                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
        }

        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub
            Log.d("onProviderDisabled", "come in");
        }

        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub
            Log.d("onProviderEnabled", "come in");
        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }
    };


    public void setLocationListener(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DialogUtils.showDialog(context,"请检查定位权限问题");
            return;
        }
        loctionManager.requestLocationUpdates(provider, 2 * 1000, 0, locationListener);
    }

    public void setLocationListenerDefault() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                DialogUtils.showDialog(context,"请检查定位权限问题");
                return;
            }
            loctionManager.requestLocationUpdates(provider, 2 * 1000, 0, mListener);
        }catch (Exception e){
//            Log.e(TAG,e.getMessage());
        }
    }

}
