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

package com.gisluq.runtimeviewer.Config.Xml;

import android.content.Context;


import com.gisluq.runtimeviewer.BMOD.MapModule.Resource.Constant;
import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.Config.Entity.WidgetEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统配置文件解析工具
 */
public class XmlParser {

	private final static String XML_NODE_RUNTIMEKEY = "runtimekey";
	private final static String XML_NODE_RUNTIMEKEY_LICENSE = "license";

	private final static String XML_NODE_WORKSPACE = "workspace";
	private final static String XML_NODE_WORKSPACE_PATH = "path";

	private final static String XML_NODE_WIDGETCONTAINER = "widgetcontainer";
	private final static String XML_NODE_MENUS = "menus";

	private final static String XML_NODE_WIDGET_GROUP = "widgetgroup";
	private final static String XML_NODE_WIDGET = "widget";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_LABEL = "label";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_GROUP = "group";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_ICON = "icon";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_SELECT_ICON = "select_icon";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_CONFIG = "config";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_ISSHOWING = "showing";
	private final static String XML_NODE_WIDGET_ATTRIBUTE_CLASSNAME = "classname";
	
	public static ConfigEntity getConfig(Context c) throws Exception
	{
		ConfigEntity config = new ConfigEntity();
		XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser pullParser = pullParserFactory.newPullParser();
		InputStream input;
		int widgetid = 10000;//组件ID，从10000开始计数

		try {
			input = c.getResources().getAssets().open(Constant.CONFIG_FILE);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		pullParser.setInput(input, "UTF-8");

		List<WidgetEntity> mListWidget = null;

		boolean isWidgetGroup = false;//是否是Widget组
		String widgetGroupName = "";//widget组名

		boolean isWidgetContainer = false;
		boolean isBaseMap = true;
		String nodeName = "", temp = "";
		int index=0;
		try
		{
			int eventType = pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				nodeName = pullParser.getName();
				switch (eventType) 
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (XML_NODE_RUNTIMEKEY.equals(nodeName)){//runtimeKey
							String license=pullParser.getAttributeValue(null,XML_NODE_RUNTIMEKEY_LICENSE);
							config.setRuntimrKey(license);
						}else if(XML_NODE_WORKSPACE.equals(nodeName)){//workPath
							String path=pullParser.getAttributeValue(null,XML_NODE_WORKSPACE_PATH);
							config.setWorkspacePath(path);
						}else if (XML_NODE_WIDGETCONTAINER.equals(nodeName)) {//widgetcontainer -widget列表
							if(mListWidget == null) mListWidget = new ArrayList<>();
							isWidgetContainer = true;
						}else if (XML_NODE_WIDGET_GROUP.equals(nodeName)){//widget组
							isWidgetGroup = true;
							widgetGroupName = pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_LABEL);
						}else if (XML_NODE_WIDGET.equals(nodeName)) {//widget组件
							WidgetEntity entity = new WidgetEntity();
							entity.setId(widgetid++);
							entity.setLabel(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_LABEL));
							entity.setClassname(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_CLASSNAME));
							entity.setIconName(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_ICON));
							entity.setSelectIconName(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_SELECT_ICON));
							entity.setStatus(Boolean.valueOf(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_ISSHOWING)));
							if(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_CONFIG) != null)
							{
								temp = pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_CONFIG);
								if(!temp.equals("")) entity.setConfig(temp);
							}
							if (pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_GROUP) != null){
								entity.setGroup(pullParser.getAttributeValue(null,XML_NODE_WIDGET_ATTRIBUTE_GROUP));
							}
							if(mListWidget != null) mListWidget.add(entity);
						}else if (XML_NODE_MENUS.equals(nodeName)) {
							if(mListWidget == null) mListWidget = new ArrayList<>();
							isWidgetContainer = false;
						} 
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				eventType = pullParser.next();
			}
			config.setListWidget(mListWidget);
			input.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return config;
	}


	
}
