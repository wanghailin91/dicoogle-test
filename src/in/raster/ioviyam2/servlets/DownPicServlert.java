package in.raster.ioviyam2.servlets;



import in.raster.ioviyam2.util.Constant;
import in.raster.ioviyam2.util.DicomSendUtil;
import in.raster.ioviyam2.util.ImageDownUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import de.iftm.dcm4che.services.ConfigProperties;
import de.iftm.dcm4che.services.StorageService;

public class DownPicServlert extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hospitalCode;//医院编码
	private String partition;//多路径
	private static Logger log = Logger.getLogger(DownPicServlert.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
	    PrintWriter out = resp.getWriter();
		JSONObject jsonObj = new JSONObject();
		
		String userDir=System.getProperty("user.dir");
		String isReport = req.getParameter("isReport");
		String serial = req.getParameter("serial");
		String recordNo = req.getParameter("recordNo");
		String deptype = req.getParameter("depType");
		String dcmUrl = req.getParameter("dcmUrl");
		String consultantPath=req.getParameter("consultantPath");
		try {
			jsonObj.put("down", false);
			ImageDownUtil imageDownUtil = new ImageDownUtil();
			//区分会诊平台特殊调用的情况
			String path=null;
			String tempFileDir=null;
			if (!StringUtils.isEmpty(consultantPath)) {
				//来自会诊平台的调用
				path=consultantPath;
				tempFileDir=userDir+File.separator+serial;
			}else {
				path = convertPath(recordNo, deptype, isReport, serial);
				tempFileDir=userDir+File.separator+recordNo;
			}
			List<String> fileNames = imageDownUtil.findFileList(path);
			
			for (String fileName : fileNames) {
				imageDownUtil.downFile(path, fileName, tempFileDir);
			}
			//进行修改
			imageDownUtil.modifyImage(tempFileDir,deptype,serial,recordNo);
			if (tempFileDir!=null
					&&fileNames!=null
					&&fileNames.size()>0) {
				//调用服务传输dicom影像
				DicomSendUtil sendUtil=new DicomSendUtil();
				String address=dcmUrl.substring(dcmUrl.indexOf("//")+2);
				if (sendUtil.sendBySelf(address, tempFileDir)) {
					jsonObj.put("down", true);
					sendUtil.deleteTempFiles(tempFileDir);
				}	
			}
		  out.println(jsonObj.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			 ConfigProperties cfgProperties= new ConfigProperties(
					StorageService.class.getResource("/resources/hospital.cfg"));
			 hospitalCode=cfgProperties.getProperty("hospital.code");
			 partition=cfgProperties.getProperty("data.partition");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public String convertPath(String recordNo, String deptype, String isReport,
			String serial) {
		StringBuffer relativePath = new StringBuffer();
		if (!StringUtils.isEmpty(partition)) {
			relativePath.append("/" + partition);
		}
		if (isReport.equals("y")) {
			// 已出报告，从报告路径取图
			relativePath.append("/" + recordNo.substring(0, 3));
			relativePath.append("/" + getPacsType(deptype));
			relativePath.append("/" + recordNo.substring(3, 7));
			relativePath.append("/" + recordNo.substring(7, 9));
			relativePath.append("/" + recordNo.substring(9, 11));
			relativePath.append("/" + recordNo);
		} else {
			// 未出报告,从serial目录取图
			relativePath.append("/gwdata/" + hospitalCode + "/DICOM/"+ deptype+"/" + serial);
		}

		return relativePath.toString();
	}
	

	//检查类型转换
	public String getPacsType(String checkType) {
		if (checkType.equals(Constant.CR)) {
			return "2";
		} else if (checkType.equals(Constant.MR)) {
			return "9";
		} else if (checkType.equals(Constant.CT)) {
			return "0";
		} else if (checkType.equals(Constant.XA)) {
			return "1";
		} else if (checkType.equals(Constant.US)) {
			return "5";
		} else if (checkType.equals(Constant.OT)) {
			return "6";
		} else {
			return "11";
		}
	}

}
