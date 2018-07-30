package com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.BaseMap;

import android.content.Context;


import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.Utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 底图图层加载管理类
 * Created by gis-luq on 2017/6/11.
 */

public class BaseMapManager {

    private Context context;
    private MapView mapView;

    private List<BasemapLayerInfo> basemapLayerInfoList =null;

    public BaseMapManager(Context context, MapView mapView,String path){

        this.context = context;
        this.mapView = mapView;

        JSONArray jsonArray = loadBaseMapConfig(path);
        if (jsonArray!=null){
            try {
                basemapLayerInfoList = getBaseLayers(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取底图图层列表
     * @return
     */
    public List<BasemapLayerInfo> getBasemapLayerInfos(){
        return this.basemapLayerInfoList;
    }

    /**
     * 加载基础底图信息
     * @param path
     * @return
     */
    private JSONArray loadBaseMapConfig(String path){
        JSONArray jsonArrBaseLayers =null;
        String JSON = FileUtils.openTxt(path,"GB2312");
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            jsonArrBaseLayers = jsonObject.getJSONArray("baselayers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArrBaseLayers;
    }

    /**
     * 解析基础底图列表信息
     * @param jsonArrBaseLayers
     * @return
     * @throws JSONException
     */
    private List<BasemapLayerInfo> getBaseLayers(JSONArray jsonArrBaseLayers) throws JSONException {
        List<BasemapLayerInfo> result = null;
        int num = jsonArrBaseLayers.length();
        if(num>0){
            result = new ArrayList<>();
            for (int i=0;i< num;i++){
                JSONObject obj = jsonArrBaseLayers.getJSONObject(i);
                BasemapLayerInfo basemapLayerInfo = new BasemapLayerInfo();
                try{
                    basemapLayerInfo.Name = obj.getString("name");
                }catch (Exception e){

                }
                try{
                    basemapLayerInfo.Type = obj.getString("type");
                }catch (Exception e){

                }
                try{
                    basemapLayerInfo.Path = obj.getString("path");
                }catch (Exception e){

                }

                try{
                    basemapLayerInfo.LayerIndex = obj.getInt("layerIndex");
                }catch (Exception e){

                }
                try{
                    basemapLayerInfo.Visable = obj.getBoolean("visable");
                }catch (Exception e){

                }
                try{
                    basemapLayerInfo.Opacity = obj.getDouble("opacity");
                }catch (Exception e){

                }
                result.add(basemapLayerInfo);
            }
        }
        result = SortingByLayerIndex(result);//通过读取的LayerIndex排序
        return  result;
    }

    /**
     * 底图图层列表排序——正序
     * @param basemapLayerInfoList
     * @return
     */
    private List<BasemapLayerInfo> SortingByLayerIndex(List<BasemapLayerInfo> basemapLayerInfoList) {
        List<BasemapLayerInfo> result = null;
        if(basemapLayerInfoList !=null){
            result = new ArrayList<>();
            /**
             * Collections.sort(list, new PriceComparator());的第二个参数返回一个int型的值，就相当于一个标志，告诉sort方法按什么顺序来对list进行排序。
             * 按照LayerIndex从小到大排序
             */
            Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    BasemapLayerInfo p1 = (BasemapLayerInfo) o1;
                    BasemapLayerInfo p2 = (BasemapLayerInfo) o2;
                    if (p1.LayerIndex < p2.LayerIndex)
                        return -1;
                    else if (p1.LayerIndex == p2.LayerIndex)
                        return 0;
                    else if (p1.LayerIndex > p2.LayerIndex)
                        return                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     1;
                    return 0;
                }
            };
            Collections.sort(basemapLayerInfoList, comp);
            result = basemapLayerInfoList;
        }
        return result;
    }


}
