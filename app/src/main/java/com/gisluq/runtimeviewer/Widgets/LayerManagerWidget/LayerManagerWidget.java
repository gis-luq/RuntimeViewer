package com.gisluq.runtimeviewer.Widgets.LayerManagerWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.data.VectorTileCache;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.raster.Raster;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;
import com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.Adapter.LayerListviewAdapter;
import com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.Adapter.LegendListviewAdapter;
import com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.BaseMap.BaseMapManager;
import com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.BaseMap.BasemapLayerInfo;

import java.io.File;
import java.util.List;

/**
 * 图层控制
 * Created by gis-luq on 2017/5/5.
 */
public class LayerManagerWidget extends BaseWidget {

    private static String TAG = "LayerManagerWidget";

    public View mWidgetView = null;//
    public ListView baseMapLayerListView = null;
    public ListView featureLayerListView = null;

    private Context context;

    private LayerListviewAdapter featureLayerListviewAdapter =null;
    private LayerListviewAdapter basemapLayerListviewAdapter =null;
    private LegendListviewAdapter legendListviewAdapter = null;

    @Override
    public void active() {
        super.active();
        super.showWidget(mWidgetView);
//        super.showMessageBox(super.name);
    }

    @Override
    public void create() {

        context = super.context;

        initBaseMapResource();//初始化底图
        initOperationalLayers();//初始化业务图层

        initWidgetView();//初始化UI

    }

    /**
     * UI初始化
     */
    private void initWidgetView() {
        /**
         * **********************************************************************************
         * 布局容器
         */
        mWidgetView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager,null);
        TextView txtLayerListBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_layer_manager_txtBtnLayerList);
        final View viewLayerListSelect = mWidgetView.findViewById(R.id.widget_view_layer_manager_viewLayerList);
        TextView txtLegendBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_layer_manager_txtBtnLegend);
        final View viewLegendSelect = mWidgetView.findViewById(R.id.widget_view_layer_manager_viewLegend);
        final RelativeLayout viewContent = mWidgetView.findViewById(R.id.widget_view_layer_manager_contentView);//内容区域

        /**
         * **********************************************************************************
         * 图例
         */
        final View legendView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager_legend,null);
        ListView listViewLenged = legendView.findViewById(R.id.widget_view_layer_manager_legend_layerListview);
        legendListviewAdapter = new LegendListviewAdapter(context,super.mapView.getMap().getOperationalLayers());
        listViewLenged.setAdapter(legendListviewAdapter);
        TextView textViewNoLegend = (TextView) legendView.findViewById(R.id.widget_view_layer_manager_legend_txtNoLegend);

        /**
         * **********************************************************************************
         * 图层列表
         */
        final View layerManagerView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_layer_manager_layers,null);
        this.baseMapLayerListView = (ListView)layerManagerView.findViewById(R.id.widget_view_layer_manager_layers_basemapLayerListview);
        this.featureLayerListView = (ListView)layerManagerView.findViewById(R.id.widget_view_layer_manager_layers_featureLayerListview);

        basemapLayerListviewAdapter = new LayerListviewAdapter(context,super.mapView.getMap().getBasemap().getBaseLayers());
        this.baseMapLayerListView.setAdapter(basemapLayerListviewAdapter);
        featureLayerListviewAdapter = new LayerListviewAdapter(context,super.mapView.getMap().getOperationalLayers());
        this.featureLayerListView.setAdapter(featureLayerListviewAdapter);

        //业务图层操作
        Button operationBtnMore = (Button)layerManagerView.findViewById(R.id.widget_view_layer_managet_layers_operationlayer_btnMore);
        operationBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pmp = new PopupMenu(context, v);
                pmp.getMenuInflater().inflate(R.menu.menu_layer_handle_tools, pmp.getMenu());
                pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_layer_handle_tools_openAllLayer:
                                for (int i=0;i<mapView.getMap().getOperationalLayers().size();i++){
                                    Layer layer = mapView.getMap().getOperationalLayers().get(i);
                                    layer.setVisible(true);
                                }
                                featureLayerListviewAdapter.refreshData();
                                break;
                            case R.id.menu_layer_handle_tools_closedAllLayer:
                                for (int i=0;i<mapView.getMap().getOperationalLayers().size();i++){
                                    Layer layer = mapView.getMap().getOperationalLayers().get(i);
                                    layer.setVisible(false);
                                }
                                featureLayerListviewAdapter.refreshData();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pmp.show();
            }
        });

        //底图图层
        Button basemapnBtnMore = (Button)layerManagerView.findViewById(R.id.widget_view_layer_managet_layers_basemap_btnMore);
        basemapnBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pmp = new PopupMenu(context, v);
                pmp.getMenuInflater().inflate(R.menu.menu_layer_handle_tools, pmp.getMenu());
                pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_layer_handle_tools_openAllLayer:
                                for (int i=0;i<mapView.getMap().getBasemap().getBaseLayers().size();i++){
                                    Layer layer = mapView.getMap().getBasemap().getBaseLayers().get(i);
                                    layer.setVisible(true);
                                }
                                basemapLayerListviewAdapter.refreshData();
                                break;
                            case R.id.menu_layer_handle_tools_closedAllLayer:
                                for (int i=0;i<mapView.getMap().getBasemap().getBaseLayers().size();i++){
                                    Layer layer = mapView.getMap().getBasemap().getBaseLayers().get(i);
                                    layer.setVisible(false);
                                }
                                basemapLayerListviewAdapter.refreshData();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pmp.show();
            }
        });

        /**
         * **********************************************************************************
         * 布局容器事件
         */
        viewContent.addView(layerManagerView);//默认显示图层列表
        txtLayerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewContent.getChildAt(0)!=layerManagerView){
                    viewContent.removeAllViews();
                    viewContent.addView(layerManagerView);
//                    txtLayerListBtn.setTextColor();
                    viewLayerListSelect.setVisibility(View.VISIBLE);
                    viewLegendSelect.setVisibility(View.GONE);
                }
            }
        });
        txtLegendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewContent.getChildAt(0)!=legendView){
                    viewContent.removeAllViews();
                    viewContent.addView(legendView);
                    viewLayerListSelect.setVisibility(View.GONE);
                    viewLegendSelect.setVisibility(View.VISIBLE);

                    //打开窗口前先刷新数据面板
                    legendListviewAdapter.refreshData();
                }
            }
        });
    }

    @Override
    public void inactive(){
        super.inactive();
    }

    /**
     * 初始化业务图层
     */
    private void initOperationalLayers(){
        String path = getOperationalLayersPath();
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(path,"shp");
        for (int i=0;i<fileInfos.size();i++) {
            FileUtils.FileInfo fileInfo = fileInfos.get(i);

            final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(fileInfo.FilePath);
            shapefileFeatureTable.loadAsync();//异步方式读取文件
            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    //数据加载完毕后，添加到地图
                    FeatureLayer mainShapefileLayer = new FeatureLayer(shapefileFeatureTable);
                    mapView.getMap().getOperationalLayers().add(mainShapefileLayer);
                }
            });
        }
    }

    /**
     * 初始化基础底图资源
     */
    private void initBaseMapResource() {
//        String strMapUrl="http://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";
//        ArcGISTiledMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISTiledMapServiceLayer(strMapUrl);
//        resourceConfig.mapView.addLayer(arcGISTiledMapServiceLayer);
        String configPath = getBasemapPath("basemap.json");
        BaseMapManager basemapManager = new BaseMapManager(context,super.mapView,configPath);
        List<BasemapLayerInfo> basemapLayerInfoList= basemapManager.getBasemapLayerInfos();
        if (basemapLayerInfoList==null) return;
        for (int i=0;i<basemapLayerInfoList.size();i++){
            BasemapLayerInfo layerInfo = basemapLayerInfoList.get(i);
            String type = layerInfo.Type;
            if(type.equals(BasemapLayerInfo.LYAER_TYPE_TPK)){//TPK
                String path =getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)){//判断是否存在
                    TileCache tileCache = new TileCache(path);
                    ArcGISTiledLayer localTiledLayer = new ArcGISTiledLayer(tileCache);
                    localTiledLayer.setName(layerInfo.Name);
                    localTiledLayer.setVisible(layerInfo.Visable);
                    localTiledLayer.setOpacity((float) layerInfo.Opacity);
                    super.mapView.getMap().getBasemap().getBaseLayers().add(localTiledLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalTiledPackage)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalTiledPackage)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_TIFF)){//Tiff
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    Raster raster = new Raster(path);
                    RasterLayer rasterLayer = new RasterLayer(raster);
                    rasterLayer.setName(layerInfo.Name);
                    rasterLayer.setVisible(layerInfo.Visable);
                    rasterLayer.setOpacity((float) layerInfo.Opacity);
                    super.mapView.getMap().getBasemap().getBaseLayers().add(rasterLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalGeoTIFF)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalGeoTIFF)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_SERVERCACHE)){//Server缓存切片
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    TileCache tileCache = new TileCache(path);
                    ArcGISTiledLayer localTiledLayer = new ArcGISTiledLayer(tileCache);
                    localTiledLayer.setName(layerInfo.Name);
                    localTiledLayer.setVisible(layerInfo.Visable);
                    localTiledLayer.setOpacity((float) layerInfo.Opacity);
                    super.mapView.getMap().getBasemap().getBaseLayers().add(localTiledLayer);
                }else{
                    Log.d(TAG,"底图文件(LocalServerCache)不存在,"+path);
                    Toast.makeText(context, "底图文件(LocalServerCache)不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_ONLINE_TILEDLAYER)){//在线瓦片
                String url = layerInfo.Path;
                ArcGISTiledLayer tiledMapServiceLayer = new ArcGISTiledLayer(url);
                tiledMapServiceLayer.setName(layerInfo.Name);
                tiledMapServiceLayer.setVisible(layerInfo.Visable);
                tiledMapServiceLayer.setOpacity((float) layerInfo.Opacity);
                super.mapView.getMap().getBasemap().getBaseLayers().add(tiledMapServiceLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_ONLINE_DYNAMICLAYER)) {//在线动态图层
                String url = layerInfo.Path;
                ArcGISMapImageLayer dynamicMapServiceLayer = new ArcGISMapImageLayer(url);
                dynamicMapServiceLayer.setName(layerInfo.Name);
                dynamicMapServiceLayer.setVisible(layerInfo.Visable);
                dynamicMapServiceLayer.setOpacity((float) layerInfo.Opacity);
                super.mapView.getMap().getBasemap().getBaseLayers().add(dynamicMapServiceLayer);
            }else if(type.equals(BasemapLayerInfo.LYAER_TYPE_VTPK)){//VTPK
                String path = getBasemapPath(layerInfo.Path);
                if(FileUtils.isExist(path)) {//判断是否存在
                    VectorTileCache vectorTileCache = new VectorTileCache(path);
                    ArcGISVectorTiledLayer arcGISVectorTiledLayer = new ArcGISVectorTiledLayer(vectorTileCache);
                    arcGISVectorTiledLayer.setName(layerInfo.Name);
                    arcGISVectorTiledLayer.setVisible(layerInfo.Visable);
                    arcGISVectorTiledLayer.setOpacity((float)layerInfo.Opacity);
                    super.mapView.getMap().getBasemap().getBaseLayers().add(arcGISVectorTiledLayer);
                }else{
                    Log.d(TAG,"vtpk文件不存在,"+path);
                    Toast.makeText(context, "vtpk文件不存在,"+path, Toast.LENGTH_LONG).show();
                    continue;
                }
            }
        }
    }

    /**
     * 获取基础底图路径
     * @param path
     * @return
     */
    private String getBasemapPath(String path){
        return projectPath+ File.separator+"BaseMap"+File.separator + path;
    }

    /**
     * 获取基础底图路径
     * @return
     */
    private String getOperationalLayersPath(){
        return projectPath+ File.separator+"OperationalLayers"+File.separator;
    }

}
