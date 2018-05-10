package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 文件列表
 * Created by luq on 2017/5/5.
 */

public class FileAdapter extends BaseAdapter {


    public class AdapterHolder{//列表绑定项
        public TextView txtName;
    }

    private List<FileUtils.FileInfo> fileList =null;
    private Context context;

    public FileAdapter(Context c, List<FileUtils.FileInfo> list) {
        this.fileList = list;
        this.context = c;
    }

    public void setAdapterList(List<FileUtils.FileInfo> infos) {
        this.fileList = infos;
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        if (fileList==null){
            return 0;
        }else {
            return fileList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AdapterHolder holder = null;
        if(convertView == null){
            holder = new AdapterHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_feature_edit_alert_mediafiles_item, null);
            holder.txtName = convertView.findViewById(R.id.widget_view_feature_edit_alert_mediafiles_item_txtFileName);
            convertView.setTag(holder);
        }else {
            holder = (AdapterHolder) convertView.getTag();
        }

        holder.txtName.setText(fileList.get(position).FileName);
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String url = fileList.get(position).FilePath;
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    File file = new File(url);
                    String[] strArray = fileList.get(position).FileName.split("\\.");
                    int suffixIndex = strArray.length -1;
                    if(strArray[suffixIndex].indexOf("jpg")!=-1){
                        it.setDataAndType(Uri.fromFile(file),"image/*");
                    }else if (strArray[suffixIndex].indexOf("mp4")!=-1){
                        it.setDataAndType(Uri.fromFile(file),"video/*");
                    }else if (strArray[suffixIndex].indexOf("amr")!=-1){
                        it.setDataAndType(Uri.fromFile(file),"audio/*");
                    }
                    context.startActivity(it);
                }catch (Exception e){
                    Toast.makeText(context, "未检测到系统有该格式文件浏览功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.txtName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("是否删除?");
                builder.setCancelable(true);
                builder.setTitle("系统提示");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1) {
                        FileUtils.FileInfo fileInfo = fileList.get(position);
                        boolean bool = FileUtils.deleteFiles(fileInfo.FilePath);
                        if(bool){
                            fileList.remove(position);
                            refreshData();
                        }else{
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                builder.show();
                return false;
            }
        });

        return convertView;
    }
}
