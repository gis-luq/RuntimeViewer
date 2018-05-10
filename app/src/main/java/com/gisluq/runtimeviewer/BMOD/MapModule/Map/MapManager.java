package com.gisluq.runtimeviewer.BMOD.MapModule.Map;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.BMOD.MapModule.Location.DMUserLocationManager;
import com.gisluq.runtimeviewer.BMOD.MapModule.PartView.Compass;
import com.gisluq.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;
import com.gisluq.runtimeviewer.BMOD.MapModule.Running.MapConfigInfo;
import com.gisluq.runtimeviewer.R;

import java.text.DecimalFormat;

import gisluq.lib.Util.ToastUtils;


/**
 * 地图组件管理类
 * Created by gis-luq on 2018/4/10.
 */

public class MapManager {

    private static String TAG = "MapManager";

    private Context context;
    private ResourceConfig resourceConfig;

    private ConfigEntity configEntity;

    private String projectPath;

    private ArcGISMap map;//地图容器

    public MapManager(Context context, ResourceConfig resourceConfig, ConfigEntity ce, String dirPath){
        this.context = context;
        this.resourceConfig = resourceConfig;
        this.configEntity = ce;
        this.projectPath = dirPath;

        this.map = new ArcGISMap();//初始化
        resourceConfig.mapView.setMap(map);

        initMapResource();//初始化配置
    }


    /**
     * 初始化地图资源
     */
    private void initMapResource() {

        try {
            /**设置许可**/
            ArcGISRuntimeEnvironment.setLicense(configEntity.getRuntimrKey());
            String version =ArcGISRuntimeEnvironment.getAPIVersion();
            String lic =ArcGISRuntimeEnvironment.getLicense().getLicenseLevel().name();
            ToastUtils.showShort(context,"ArcGIS Runtime版本:"+version +"; 许可信息:"+lic);
        }catch (Exception e){
            ToastUtils.showShort(context,"ArcGIS Runtime 许可设置异常:"+e.getMessage());
        }

        /***显示放大镜*/
        resourceConfig.mapView.setMagnifierEnabled(false);
        resourceConfig.mapView.setCanMagnifierPanMap(false);

        /**最大最小比例尺设置*/
        resourceConfig.mapView.getMap().setMinScale(999999999);//最小比例尺
        resourceConfig.mapView.getMap().setMaxScale(1500);//最大比例尺

        /**旋转*/
        final Compass mCompass = new Compass(context, null, resourceConfig.mapView);
        mCompass.setClickable(true);
        resourceConfig.compassView.addView(mCompass);
        // Set a single tap listener on the MapView.
        mCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When a single tap gesture is received, reset the map to its default rotation angle,
                // where North is shown at the top of the device.
                resourceConfig.mapView.setViewpointRotationAsync(0);

                //设置旋转角度
                mCompass.setRotationAngle(0);
                //设置旋转隐藏
                mCompass.setVisibility(View.GONE);
            }
        });

        /**显示比例尺级别*/
        final DecimalFormat df = new DecimalFormat("###");

        //设置默认比例尺级别
        String scale = df.format(resourceConfig.mapView.getMapScale());
        resourceConfig.txtMapScale.setText("比例尺 1:"+scale);

        //根据缩放设置比例尺级别
        resourceConfig.mapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                String scale = df.format(resourceConfig.mapView.getMapScale());
                resourceConfig.txtMapScale.setText("比例尺 1:"+scale);
            }
        });

        /**
         * 设置定位相关事件
         */
        MapConfigInfo.dmUserLocationManager = new DMUserLocationManager(context,resourceConfig.mapView);
        resourceConfig.togbtnLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ToastUtils.showShort(context,"定位开");
                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_on);
                    MapConfigInfo.dmUserLocationManager.start();
                }else{
                    ToastUtils.showShort(context,"定位关");
                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_off);
                    MapConfigInfo.dmUserLocationManager.stop();
                    resourceConfig.txtLocation.setText(context.getString(R.string.txt_location_info));
                }
            }
        });

    }

}
