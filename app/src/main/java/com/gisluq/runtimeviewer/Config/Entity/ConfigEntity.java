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

package com.gisluq.runtimeviewer.Config.Entity;


import com.gisluq.runtimeviewer.BMOD.MapModule.Common.CommTools;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件实体类
 */
public class ConfigEntity 
{
	private String runtimrKey = null;//许可信息
	private String workspacePath = null;//工作空间路径

	private double[] extentArray = null;

	private List<WidgetEntity> mListWidget = new ArrayList<WidgetEntity>();

	public String getRuntimrKey() {
		return runtimrKey;
	}

	public void setRuntimrKey(String runtimrKey) {
		this.runtimrKey = runtimrKey;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public void setMapExtent(String extent)
	{
		extentArray = CommTools.getExtent(extent);
	}
	public double[] getMapExtend()
	{
		return extentArray;
	}

	public void setListWidget(List<WidgetEntity> list)
	{
		mListWidget = list;
	}
	public List<WidgetEntity> getListWidget()
	{
		return mListWidget;
	}
	
	
}
