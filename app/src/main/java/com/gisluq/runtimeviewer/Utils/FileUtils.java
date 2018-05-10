package com.gisluq.runtimeviewer.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹操作类
 */
public class FileUtils {

    private static final String TAG ="FileUtils" ;
    private static String urlNull = "原文件路径不存在";
    private static String isFile = "原文件不是文件";
    private static String canRead = "原文件不能读";
    private static String message = "OK";
    private static String cFromFile = "创建原文件出错:";
    private static String ctoFile = "创建备份文件出错:";

    /**
     * 获取手机存储路径
     * @return
     */
    public static String getSdCardPath(){
        String result = "";
        File Dir = Environment.getExternalStorageDirectory();
        //得到一个路径，内容是内部sdcard的文件夹路径和名字
        result =Dir.getPath();
        return result;
    }

    /**
     * 在指定文件夹下创建子文件夹
     * @param path 文件夹路径
     * @param name 子文件夹路径
     * @return
     */
    public static boolean createChildFilesDir(String path, String name)
    {
        String childPath = path+"/"+name;
        File pathMain= new File(childPath);
        if (!pathMain.exists()) {
            //若不存在，创建目录
            pathMain.mkdirs();
        }
        return true;
    }

    /**
     * 在指定文件夹下创建子文件夹
     * @param pathname 文件夹路径
     * @return
     */
    public static boolean createChildFilesDir(String pathname)
    {
        String childPath = pathname;
        File pathMain= new File(childPath);
        if (!pathMain.exists()) {
            //若不存在，创建目录
            return pathMain.mkdirs();
        }
        return false;
    }

    /**
     * 删除文件夹下所有内容
     * @param path 文件夹路径
     * @return 返回是否删除成功
     */
    public static boolean deleteFiles(String path) {
        File file = new File(path);
        try {
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete(); // delete()方法
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFiles(files[i].getPath()); // 把每个文件 用这个方法进行迭代
                    }
                    //file.delete();//删除目录
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // TODO: 程序异常处理
            return false;
        }
    }

    /**
     * 文件拷贝
     * @param fromFileUrl 旧文件地址和名称
     * @param toFileUrl 新文件地址和名称
     * @return 返回备份文件的信息，ok是成功，其它就是错误
     */
    public static String copyFile(String fromFileUrl, String toFileUrl) {
        File fromFile = null;
        File toFile = null;
        try {
            fromFile = new File(fromFileUrl);
        } catch (Exception e) {
            return cFromFile + e.getMessage();
        }

        try {
            toFile = new File(toFileUrl);
        } catch (Exception e) {
            return ctoFile + e.getMessage();
        }

        if (!fromFile.exists()) {
            return urlNull;
        }
        if (!fromFile.isFile()) {
            return isFile;
        }
        if (!fromFile.canRead()) {
            return canRead;
        }

        // 复制到的路径如果不存在就创建
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists()) {
            toFile.delete();
        }

        if (!toFile.canWrite()) {
            //return notWrite;
        }

        try {
            FileInputStream fosfrom = new FileInputStream(
                    fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;

            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); // 将内容写到新文件当中
            }
            //关闭数据流
            fosfrom.close();
            fosto.close();

        } catch (Exception e) {
            e.printStackTrace();
            message = "备份失败!";
        }
        return message;
    }

    /**
     * 判断文件或文件夹是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean isExist(String filePath)
    {
        File file = new File(filePath);
        return  file.exists();
    }

    /**
     * 文件拷贝
     * @param myContext
     * @param ASSETS_NAME 要复制的文件路径及文件名
     * @param savePathName 要保存的文件路径及文件名
     */
    public static String copyFileFromAssets (Context myContext, String ASSETS_NAME, String savePathName) {
        String filename = savePathName;
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                return "拷贝成功";
            }else{
                return "文件不存在";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "拷贝异常";
        }
    }

    /**
     * 获取TXT文件内容
     * @param filePath 文件路径+名称
     * @return TXT文件中的内容 String
     */
    public static String openTxt(String filePath)
    {
        File file = new File(filePath);
        String result = "";
        if (!file.exists()) {
            //判断文件是否存在，如果不存在，则创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        try {
            //#从文件attribute.txt中读出数据
            //在内存中开辟一段缓冲区
            byte Buffer[] = new byte[1024*300];//300kb
            //得到文件输入流
            @SuppressWarnings("resource")
            FileInputStream in = new FileInputStream(file);
            //读出来的数据首先放入缓冲区，满了之后再写到字符输出流中
            int len = in.read(Buffer);
            //创建一个字节数组输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Buffer, 0, len);
            //把字节输出流转String
            result =  new String(outputStream.toByteArray(),"GB2312");
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG,"文件读取失败"+e.toString());
        }
        return result;
    }

    /**
     *在路径filePath下创建文件
     *@param filePath 文件地址+名称；
     *@param Content 内容；
     *@return 返回是否创建成功
     */
    public static boolean saveTxt(String filePath, String Content)
    {
        File file = new File(filePath);
        if (!file.exists()) {
            //判断文件是否存在，如果不存在，则创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        try {
            //#写数据到文件XXX.txt
            //创建一个文件输出流
            FileOutputStream out = new FileOutputStream(file, false);//true表示在文件末尾添加
            out.write(Content.getBytes("GB2312"));
            out.close();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }


    /**
     *获取文件夹下文件列表
     * @param path 文件夹路径
     * @param type 1-all（加载全部） 2-folder（只加载文件夹） 3-.apk (加载指定后缀文件)
     * @return
     */
    public static List<FileInfo> getFileListInfo(String path, String type){
        List<FileInfo> result = null;
        try{
            File f = new File(path);
            File[] files = f.listFiles();// 列出所有文件
            // 将所有文件存入list中
            if(files != null){
                int count = files.length;// 文件个数
                result =  new ArrayList<FileInfo>();
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    FileInfo file_t = new FileInfo();
                    file_t.FileName = file.getName();
                    file_t.FilePath = file.getPath();
                    if (type=="all") {
                        result.add(file_t);
                    }else if(type =="folder"){
                        String str = file_t.FileName;
                        if(str.indexOf(".")==-1){//只加载文件夹
                            result.add(file_t);
                        }else{
                            continue;
                        }
                    }else{
                        String[] strArray = file_t.FileName.split("\\.");
                        int suffixIndex = strArray.length -1;
                        if(strArray[suffixIndex].indexOf(type)!=-1){//只加载指定类型数据
                            result.add(file_t);
                        }else{
                            continue;
                        }
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 文件信息类
     */
    public static class FileInfo{
        public String FileName;// 文件或文件夹名称
        public String FilePath;//文件或文件夹路径
    }

}
