package in.raster.ioviyam2.servlets;


import de.iftm.dcm4che.services.CDimseService;
import de.iftm.dcm4che.services.ConfigProperties;
import de.iftm.dcm4che.services.StorageService;
import in.raster.ioviyam2.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dcm4che.util.DcmURL;
import org.jboss.system.server.ServerConfigLocator;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;

public class QueryIsExistServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(QueryIsExistServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String serial = req.getParameter("patientID");
        if (serial.contains("_")) {
            serial = serial.split("_")[0];
        }
        String deptype = req.getParameter("depType");


        JSONObject jsonObj = new JSONObject();
        try {
            //新增获取dicom配置
            String dcmUrl = null;
            File filelocation = ServerConfigLocator.locate().getServerHomeDir();
            String location = filelocation.getAbsolutePath() + File.separator;
            File filedata = new File(location + "iOviyam.properties");

            Properties prop = new Properties();
            prop.load(new FileInputStream(filedata));

            //获取dicom aetitle 配置
            String aetitle = prop.getProperty("AETitle");
            //获取dicom 服务端ip
            String hostname = prop.getProperty("Host");
            //获取dicom 服务端端口
            String port = prop.getProperty("Port");
            String wado = prop.getProperty("wado");

//	        if (!StringUtils.isEmpty(listener.getAetitle())) {
//				dcmUrl = "DICOM://"+ aetitle+ ":"+ listener.getAetitle()+"@"+hostname+":"+port;
//			}else {
            dcmUrl = "DICOM://" + aetitle + "@" + hostname + ":" + port;
//			}


            DcmURL url = new DcmURL(dcmUrl);
            jsonObj.put("exist", false);
            jsonObj.put("serverUrl", "http://" + hostname + ":" + wado + "/wado");
            jsonObj.put("dicomUrl", dcmUrl);

            jsonObj.put("aetitle", aetitle);
            jsonObj.put("hostname", hostname);
            jsonObj.put("port", port);
            jsonObj.put("wado", wado);

            ConfigProperties cfgProperties = new ConfigProperties(
                    StorageService.class.getResource("/resources/CDimseService.cfg"));
            cfgProperties.put("key.PatientID", serial);
            if (!StringUtils.isEmpty(deptype)) {
                if (deptype.equals(Constant.CR)) {
                    cfgProperties.put("key.ModalitiesInStudy", "CR");
                } else if (deptype.equals(Constant.MR)) {
                    cfgProperties.put("key.ModalitiesInStudy", "MR");
                } else if (deptype.equals(Constant.CT)) {
                    cfgProperties.put("key.ModalitiesInStudy", "CT");
                } else if (deptype.equals(Constant.XA)) {
                    cfgProperties.put("key.ModalitiesInStudy", "XA");
                } else if (deptype.equals(Constant.US)) {
                    cfgProperties.put("key.ModalitiesInStudy", "US");
                } else if (deptype.equals(Constant.OT)) {
                    cfgProperties.put("key.ModalitiesInStudy", "OT");
                }
            }

            CDimseService cDimseService = new CDimseService(cfgProperties, url);
            boolean isOpen = cDimseService.aASSOCIATE();
            if (isOpen) {
                jsonObj.put("deptype", deptype);
                Vector dsVector = cDimseService.cFIND();
                if (dsVector.size() > 0) {
                    jsonObj.put("exist", true);
                } else if (deptype.equals(Constant.CR) && dsVector.size() == 0) {
                    cDimseService.aRELEASE(true);
                    cDimseService.aASSOCIATE();
                    cfgProperties.put("key.ModalitiesInStudy", "DX");
                    cDimseService.setQueryKeys(cfgProperties);
                    dsVector = cDimseService.cFIND();
                    if (dsVector.size() > 0) {
                        jsonObj.put("deptype", "DX");
                        jsonObj.put("exist", true);
                    }
                }
            }
            out.println(jsonObj.toString());
            cDimseService.aRELEASE(true);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

}
