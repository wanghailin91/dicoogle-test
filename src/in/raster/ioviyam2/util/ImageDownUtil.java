package in.raster.ioviyam2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import de.iftm.dcm4che.services.ConfigProperties;
import de.iftm.dcm4che.services.StorageService;

public class ImageDownUtil {
	private static Logger log = Logger.getLogger(ImageDownUtil.class);
	
	private String username="pacs3iftp";
	private String password="pacs3iftpUserView";
	private String type="dcm";
	private ConfigProperties cfgProperties;
	
	
	public ImageDownUtil(){
		try {
			cfgProperties = new ConfigProperties(
					StorageService.class.getResource("/resources/hospital.cfg"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public void modifyImage(String dir,String depType,String serial,String recordNo) {
		File tempDir=new File(dir);
		File[] tempDirFiles=tempDir.listFiles();
		DicomObject dcmObj;
		DicomInputStream din = null;
		FileOutputStream fos=null;
		int i=1;
		for (File file : tempDirFiles) {
			// 加载要修改的图片
			File fileDCM = new File(file.getAbsolutePath());
			try {
				din = new DicomInputStream(fileDCM);
				dcmObj = din.readDicomObject();
				dcmObj.remove(Tag.PatientID);
				dcmObj.putString(Tag.PatientID, VR.LO, serial);
				if (depType.equals(Constant.US)||depType.equals(Constant.OT)||depType.equals(Constant.CR)) {
					dcmObj.remove(Tag.Modality);
					dcmObj.putString(Tag.Modality, VR.CS, depType);
					dcmObj.remove(Tag.StudyInstanceUID);
					dcmObj.putString(Tag.StudyInstanceUID, VR.UI, recordNo);
					dcmObj.remove(Tag.SeriesInstanceUID);
					dcmObj.putString(Tag.SeriesInstanceUID, VR.UI, recordNo);
					dcmObj.remove(Tag.InstanceNumber);
					dcmObj.putInt(Tag.InstanceNumber, VR.IS, i);
					i++;					
				}
				din.close();
				fos = new FileOutputStream(fileDCM);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				DicomOutputStream dos = new DicomOutputStream(bos);
				dos.writeDicomFile(dcmObj);
				bos.flush();
				bos.close();
				dos.flush();
				dos.close();
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}finally {
				try {
					if (null!=din) {
						din.close();
					}
					if (null!=fos) {
						fos.close();
					}
				} catch (Exception e2) {}
			}
		}
	}
	
	
	
	/**
	 * 获取对应目录下所有文件信息
	 * @param path
	 * @return
	 */
	public  List<String> findFileList(String path){
	    HttpURLConnection httpConnection = null;
	    StringBuffer fileXml = new StringBuffer();
	    List<String> fileNames = new ArrayList<String>();
		try {
			URL url = new URL(cfgProperties.getProperty("hospital.image.address")
					  +"Filelist?username="+username+"&password="+password+"&type="+type+"&path="+path);
		    httpConnection = (HttpURLConnection)url.openConnection();
		    httpConnection.setDoInput(true);
		    httpConnection.setDoOutput(true);
		    httpConnection.connect();
	
		    InputStreamReader reader = new InputStreamReader(httpConnection.getInputStream());
		    BufferedReader bufferedReader = new BufferedReader(reader);
	
		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
		        fileXml.append(line);
		     }
		    
		    reader.close();
		    bufferedReader.close();
		    httpConnection.disconnect();
		    //获取文件名集合
		    String xmlString=fileXml.toString();
		    if (StringUtils.isNotBlank(xmlString)&&
		    		!XMLHander.findNodeStr(xmlString, "/filelist/files").equals("<files/>")) {
		    	String fileNode = XMLHander.findNodeStr(xmlString, "/filelist/files");
	 			Document doc = DocumentHelper.parseText(fileNode);
	 		    Element rootElement = doc.getRootElement();

	 			for (Iterator<?> i = rootElement.elementIterator(); i.hasNext(); ) {
	 				Element node = (Element)i.next();
	 				fileNames.add(node.attributeValue("name"));
	 			}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return fileNames;
	}
	
	
	
	
	/**
	 * 下载单个文件
	 * @param path
	 * @param fileName
	 * @param savePth
	 * @return
	 * @throws IOException 
	 */
	public  boolean downFile(String path, String fileName,String savePathDir) throws IOException{
		FileOutputStream fos=null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		
		try {
			File saveDir=new File(savePathDir);
			if (!saveDir.exists()) {
				saveDir.mkdirs();
		    }
			
			byte[] buf = new byte[1024*1024];
			int size = 0;
			
		    fos = new FileOutputStream(new File(savePathDir+File.separator+fileName));
			url = new URL(cfgProperties.getProperty("hospital.image.address")
					+"Download?username="+username+"&password="+password+"&path="+path+"&file="+fileName);

			httpUrl = (HttpURLConnection)url.openConnection();
			httpUrl.setDoInput(true);
			httpUrl.setDoOutput(true);
			httpUrl.connect();
	        bis = new BufferedInputStream(httpUrl.getInputStream());
	        while ((size = bis.read(buf)) != -1) {
			      fos.write(buf, 0, size);
			}
		    fos.flush();
		
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}finally{
			  if (null!=fos) {
				fos.close();
				fos=null;}
			  if (null!=bis) {
				bis.close();
				bis=null;}
			  if (null!=httpUrl) {
				  httpUrl.disconnect();
				  httpUrl=null;} 
		  }
		return true;
	}

}
