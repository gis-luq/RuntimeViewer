package com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.BaseMap;

/**
 * 图层信息列表
 * Created by lq on 2015/4/2.
 */
public class BasemapLayerInfo {

    /**
     * 支持离线底图类型
     */
    public static String LYAER_TYPE_TPK="LocalTiledPackage";//tpk
    public static String LYAER_TYPE_TIFF="LocalGeoTIFF";//tiff
    public static String LYAER_TYPE_SERVERCACHE="LocalServerCache";//server cache
    public static String LYAER_TYPE_ONLINE_TILEDLAYER="OnlineTiledMapServiceLayer";//在线切片
    public static String LYAER_TYPE_ONLINE_DYNAMICLAYER="OnlineDynamicMapServiceLayer";//在线动态图层
    public static String LYAER_TYPE_VTPK="LocalVectorTilePackage";//vtpk


    public String Name;//名称
    public String Type;//类型
    public String Path;//本地路径
    public int LayerIndex;//图层顺序
    public boolean Visable;//是否可以显示
    public double Opacity;//图层透明度
    public String Render;
}
