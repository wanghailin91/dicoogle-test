package in.raster.ioviyam2.util;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLHander {
	private static final Log log = LogFactory.getLog(XMLHander.class);

	/**
	 * 得到PATH对应的TEXT内容
	 * 
	 * @param content
	 *            xml内容
	 * @param path
	 *            路径，例如/Header/RequestType或/Task/Parameter@type
	 * @return
	 */
	public static String elementText(Document document, String path) {
		try {
			String[] eleStr = path.substring( 1).split("@");
			String paramStr = null;
			if( eleStr.length==2)
				paramStr = eleStr[1];
			eleStr = eleStr[0].substring( 1).split("/");
			if (eleStr == null || eleStr.length == 0)
				return null;
			Element ele = document.getRootElement();
			for (int i = 1; i < eleStr.length; i++) {
				ele = ele.element(eleStr[i]);
				if (ele == null)
					return null;
			}
			if( paramStr==null)
				return ele.getText();
			else
				return ele.attributeValue( paramStr);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	/**
	 * 得到PATH对应的TEXT内容
	 * 
	 * @param content
	 *            xml内容
	 * @param path
	 *            路径，例如/Header/RequestType或/Task/Parameter@type
	 * @return
	 */
	public static String setElementText(Document document, String path, String text) {
		try {
			String[] eleStr = path.substring( 1).split("@");
			String paramStr = null;
			if( eleStr.length==2)
				paramStr = eleStr[1];
			eleStr = eleStr[0].substring( 1).split("/");
			if (eleStr == null || eleStr.length == 0)
				return null;
			Element ele = document.getRootElement();
			for (int i = 1; i < eleStr.length; i++) {
				ele = ele.element(eleStr[i]);
				if (ele == null)
					return document.asXML();
			}
			if( paramStr==null){
				ele.setText( text);
				return document.asXML();
			}
			else{
				ele.attribute( paramStr).setValue( text);
				return document.asXML();
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return document.asXML();
	}

	/**
	 * 得到PATH对应的TEXT内容
	 * 
	 * @param content
	 *            xml内容
	 * @param path
	 *            路径，例如Header/RequestType
	 * @return
	 */
	public static String elementText(String content, String path) {
		try {
			Document document = DocumentHelper.parseText(content);
			return elementText(document, path);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	public static String setElementText(String content, String path, String text) {
		try {
			Document document = DocumentHelper.parseText(content);
			return setElementText(document, path, text);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	public static Document parseText(String content) {
		try {
			return DocumentHelper.parseText(content);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	/**
	 * 格式化XML
	 * 
	 * @param document
	 * @return
	 */
	public static String format(Document document) {
		StringWriter writer = new StringWriter();

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlwriter = new XMLWriter(writer, format);
		try {
			xmlwriter.write(document);
		} catch (Exception e) {
			return "";
		}

		return writer.toString();
	}

	/**
	 * 格试化XML
	 * 
	 * @param text
	 * @return
	 */
	public static String format(String text) {
		StringWriter writer = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlwriter = new XMLWriter(writer, format);
		try {
			Document document = DocumentHelper.parseText(text);
			xmlwriter.write(document);
		} catch (Exception e) {
			return "";
		}

		return writer.toString();
	}
	/**  
	    * load  
	    * 载入一个xml文档  
	    * @return 成功返回Document对象，失败返回null  
	    * @param uri 文件路径  
	    */  
	   public static Document load(String filename)   
	   {   
	      Document document = null;   
	      try    
	      {    
	          SAXReader saxReader = new SAXReader();   
	          document = saxReader.read(new File(filename));   
	      }   
	      catch (Exception ex){   
	          ex.printStackTrace();   
	      }     
	      return document;   
	   } 
	   
	   
	   /**
		 * 查找某个节点的所有元素list
		 * @param doc 
		 * @param path 路径如:/product/modules/module
		 * @param keyProperty 唯一标识element的属性名称
		 * @return map<key,element>
		 */
		public static Map<String ,Element> findProcessModules(Document doc,String path,String keyProperty){
			Map<String ,Element> moduleMap=new HashMap<String ,Element>();
			List list = doc.selectNodes(path);
			for(int i=0;list!=null&&list.size()>0&&i<list.size();i++){
				Element element=(Element) list.get(i);
				String key=element.attributeValue(keyProperty);
			    moduleMap.put(key,element);
			}
			return moduleMap;
		}
		
		/***
		 * 读取xml 单一子串
		 * 
		 */
		public static  String findNodeStr(String content,String xpath){
			try {
				Document doc = DocumentHelper.parseText(content);
				Node res = doc.selectSingleNode(xpath);
				return res.asXML();
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			return null;
		}
		/***
		 * 读取xml all子串
		 * 
		 */
		public static  List<String> findMultiNodeStr(String content,String xpath,String eleName){
			try {
				Document doc = DocumentHelper.parseText(content);
				Node root = doc.selectSingleNode(xpath);
				List<Element> list=root.selectNodes(eleName);
				 List<String> resList=new ArrayList<String>();
				if(list==null||list.size()<=0)return null;
				for(Element ele:list){
					resList.add(ele.asXML());
				}
				return resList;
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			return null;
		}
		
		/***
		 * 读取xml all子串
		 * 
		 */
		public static  List<String> findMultiNodeText(String content,String xpath,String eleName){
			try {
				Document doc = DocumentHelper.parseText(content);
				Node root = doc.selectSingleNode(xpath);
				List<Element> list=root.selectNodes(eleName);
				 List<String> resList=new ArrayList<String>();
				if(list==null||list.size()<=0)return null;
				for(Element ele:list){
					resList.add(ele.getText());
				}
				return resList;
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			return null;
		}
		
		/***
		 * 读取xml all子串
		 * 
		 */
		public static  Map<String,String> findMultiNodeTextAndAttr(String content,String xpath,String eleName){
			try {
				Document doc = DocumentHelper.parseText(content);
				Node root = doc.selectSingleNode(xpath);
				List<Element> list=root.selectNodes(eleName);
				Map<String,String> resMap=new HashMap<String,String>();
				if(list==null||list.size()<=0)return null;
				for(Element ele:list){
					resMap.put(ele.getTextTrim(),ele.attributeValue("feature"));
				}
				return resMap;
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			return null;
		}
		
		
		/**
		 * 
		 * @param content
		 * @param xpath /Result/Hospitalinfo
		 * @param childPath CustomerInfo/accessCode
		 * @return
		 */
		public static  Map<String,String> findMultiNodeTextAndChildElement(String content,String xpath,String childPath){
			try {
				Document doc = DocumentHelper.parseText(content);
				//Node root = doc.selectSingleNode(xpath);
				List<Element> list=doc.selectNodes(xpath);
				Map<String,String> resMap=new HashMap<String,String>();
				if(list==null||list.size()<=0)return null;
				for(Element ele:list){
					Node temp = ele.selectSingleNode(childPath);
					resMap.put(temp.getText().trim(),ele.attributeValue("id"));
				}
				return resMap;
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			return null;
		}
		
		
}
