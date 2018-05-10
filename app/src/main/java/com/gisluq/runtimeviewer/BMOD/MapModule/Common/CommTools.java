////////////////////////////////////////////////////////////////////////////////
//
//Copyright (c) 2011-2012 Esri
//
//All rights reserved under the copyright laws of the United States.
//You may freely redistribute and use this software, with or
//without modification, provided you include the original copyright
//and use restrictions.  See use restrictions in the file:
//<install location>/License.txt
//
////////////////////////////////////////////////////////////////////////////////
package com.gisluq.runtimeviewer.BMOD.MapModule.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 * Some commond API
 */ 
public class CommTools 
{
	public static final String SHARED_PREFERENCED = "sp_esri_androidviewer";
	
	public static int[] getScreenSize(Context c)
	 {     
		int[] size = new int[2];
		DisplayMetrics dm = new DisplayMetrics();
		dm = c.getApplicationContext().getResources().getDisplayMetrics();
		size[0] = dm.widthPixels;
		size[1] = dm.heightPixels;
		return size;	      
	 }
	/*
	 * Get bitmap from file which be in folder "assets"
	 */
	public static Bitmap getBitmapFromAsset(Context c, String strName)
	{
		  Bitmap bitmap = null;
		  AssetManager assetManager = c.getAssets();
		  InputStream istr;
		  try {
			  Log.d("getBitmapFromAsset","strName = "+strName);
			  istr = assetManager.open(strName);
			  bitmap = BitmapFactory.decodeStream(istr);
			  istr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return bitmap;
	}
	public static Drawable getDrawableFromAsset(Context c, String strName)
	{
		Drawable db = null;
		  AssetManager assetManager = c.getAssets();
		  InputStream istr;
		  try {
				istr = assetManager.open(strName);
				db = Drawable.createFromStream(istr,"db");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return db;
	}
	public static View getTitleView(Context c, String title)
	{
		LinearLayout layout = new LinearLayout(c);
		layout.setBackgroundColor(0xFF808080);
		//layout.setBackgroundResource(R.drawable.bg1);
		TextView tv = new TextView(c);
		tv.setText(title);
		tv.setTextColor(0xFFFFFFFF);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
    			ViewGroup.LayoutParams.FILL_PARENT,
    			ViewGroup.LayoutParams.WRAP_CONTENT);
		p.leftMargin = 5;
		p.topMargin = 3;
		p.bottomMargin = 3;
		tv.setLayoutParams(p);
		layout.addView(tv);
		return layout;
	}
	public static Bitmap imageScale(Bitmap src, int width, int height)
	 {	
		int w = src.getWidth();
		int h = src.getHeight();
		if(w==width && h==height) return src;
		
		float scaleWidth = ((float) width) / w;
	    float scaleHeight = ((float) height) / h;
	    Matrix m = new Matrix();
	    m.postScale(scaleWidth, scaleHeight);

	    Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, w, h, m, true);
	    return resizedBitmap;
	 }
	public static Bitmap getWidgetPressedIcon(Context c, Bitmap src, int width, int height)
	 {	
		//Bitmap src = getBitmapFromAsset(c, strName);
		if(src == null) return null;
		int w = src.getWidth();
		int h = src.getHeight();
		
		float scaleWidth = ((float) width-2) / w;
	    float scaleHeight = ((float) height-2) / h;
	    Matrix m = new Matrix();
	    m.postScale(scaleWidth, scaleHeight);

	    Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, w, h, m, true);
	    // create the new blank bitmap
	       Bitmap newb = Bitmap.createBitmap(width,height, Config.ARGB_8888);//

	       Canvas cv = new Canvas(newb);
	       cv.drawColor(Color.BLUE);
	       Paint p = new Paint();
	       p.setColor(Color.WHITE);
	       cv.drawRect(new Rect(0, 0, width, height), p);
	       cv.drawBitmap(resizedBitmap, 0, 0, null);
	       cv.save(Canvas.ALL_SAVE_FLAG);//
	       cv.restore();
	       resizedBitmap.recycle();

	    return newb;
	 }
	
	public static Drawable colorMatrixImageView(Drawable drawable, float saturation, float[] array){
		
		 drawable.mutate();
		 ColorMatrix cm = new ColorMatrix();
		 cm.setSaturation(saturation);
		 cm.set(array);
		 ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		 drawable.setColorFilter(cf);
		return drawable;
	}
	
	public static double[] getExtent(String extent)
	{
		double[] extentArray = new double[4]; 
		extentArray[0] = 0.0;
		extentArray[1] = 0.0;
		extentArray[2] = 0.0;
		extentArray[3] = 0.0;
		
		String temp = "";
		double coordinate = 0.0;
		String space = " ";
		if(extent == null) return null;
		extent = extent.trim();
	    int index = extent.indexOf(space);
	    if(index<0) return null;
	    int num = 0;
	    while(true)
	    {
	    	temp = extent.substring(0,index);
	    	coordinate = getDouble(temp);
	    	extentArray[num++] = coordinate;

	    	extent = extent.substring(index);
	    	extent = extent.trim();
	    	index = extent.indexOf(space);
	    	if(index < 0)
	    	{
	    		extentArray[num] = getDouble(extent);
	    		break;
	    	}
	    	
	    	if(num>3) break;
	    }
	    return extentArray;
	}
	public static double getDouble(String number)
	{
		double d = 0.0;
		try {
    		d = Double.parseDouble(number); }
    	catch(Exception e){
    		e.printStackTrace();
    		d = 0.0;
    	}
		return d;
	}
	
	public static byte[] getByteFromDoc(Document doc)
	{
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty("encoding","UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(doc), new StreamResult(bos));
			return bos.toByteArray();			
		}
		catch(Exception e)
		{		
			e.printStackTrace();
			return null;
		}
	}
	
	public static void putString(Context c, String key, String value)
	{
		 SharedPreferences settings = c.getSharedPreferences(SHARED_PREFERENCED, Context.MODE_PRIVATE);
		 SharedPreferences.Editor editor = settings.edit();
		 editor.putString(key, value);						       			 		
		 editor.commit();	
	}
	public static String getString(Context c, String key, String defautValue)
	{
		SharedPreferences sp = c.getSharedPreferences(SHARED_PREFERENCED, Context.MODE_PRIVATE);
		return sp.getString(key, defautValue);
	}
	
	public static boolean getBoolean(Context c, String key, boolean defaultValue)
	{
		SharedPreferences sp = c.getSharedPreferences(SHARED_PREFERENCED, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}
	public static void putBoolean(Context c, String key, boolean value)
	{
		SharedPreferences settings = c.getSharedPreferences(SHARED_PREFERENCED, Context.MODE_PRIVATE);
		 SharedPreferences.Editor editor = settings.edit();
		 editor.putBoolean(key, value);						       			 		
		 editor.commit();
	}
}
