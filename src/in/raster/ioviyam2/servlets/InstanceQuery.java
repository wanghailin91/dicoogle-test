package in.raster.ioviyam2.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import in.raster.ioviyam2.model.QueryModel;
import in.raster.ioviyam2.services.DcmQR;
import in.raster.ioviyam2.util.ReadConfigUtil;

public class InstanceQuery extends HttpServlet {
	ArrayList<DicomObject> lists;
	QueryModel param;
	String ae;
	String host;
	String port;
	String wado;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		this.ae = ((String) session.getAttribute("Ae"));
		this.host = ((String) session.getAttribute("host"));
		this.port = ((String) session.getAttribute("port"));
		this.wado = ((String) session.getAttribute("wadoport"));
		File filelocation = new File(System.getProperty("user.dir"));
		String location = filelocation.getAbsolutePath() + File.separator;
		Map<String, String> config = ReadConfigUtil.readConfig(ae, host, port, wado, location);
		if (null != config) {
			this.ae = config.get("ae");
			this.host = config.get("host");
			this.port = config.get("port");
			this.wado = config.get("wado");
		}

		String patID = request.getParameter("patientId");
		String studyUID = request.getParameter("studyUID");
		String seriesUID = request.getParameter("seriesUID");
		out.print(getDataInstance(patID, studyUID, seriesUID));
	}

	ArrayList<String> argumentConstInstance(String patId, String studyId, String seriesID) {
		ArrayList<String> armnts = new ArrayList<String>();
		armnts.add(this.ae + "@" + this.host + ":" + this.port);
		armnts.add("-I");
		armnts.add("-q00100020=" + patId);
		armnts.add("-q0020000D=" + studyId);
		armnts.add("-q0020000E=" + seriesID);
		return armnts;
	}

	JSONArray getDataInstance(String patId, String studyId, String seriesID) {
		JSONArray list = new JSONArray();
		try {
			this.lists = new ArrayList();
			Object[] TEST = (Object[]) argumentConstInstance(patId, studyId, seriesID).toArray();
			String[] argss = Arrays.copyOf(TEST, TEST.length, String[].class);

			DcmQR query = new DcmQR("iOviyam2");
			this.lists = ((ArrayList) query.QueryDcm(argss));
			for (int i = 0; i < this.lists.size(); i++) {
				JSONObject jobject = new JSONObject();
				DicomObject dimobject = (DicomObject) this.lists.get(i);
				String objid = dimobject.getString(524312);

				String UrlTmp = "http://" + this.host + ":" + this.wado
						+ "/wado?requestType=WADO&contentType=application/dicom&studyUID=" + studyId + "&seriesUID="
						+ seriesID + "&objectUID=" + objid + "&transferSyntax=1.2.840.10008.1.2.1";
				list.put(getImageInfo(UrlTmp, jobject));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	JSONObject getImageInfo(String UrlTmp, JSONObject jobject) {
		try {
			InputStream is = null;
			DicomInputStream dis = null;
			URL url = new URL(UrlTmp);
			is = new BufferedInputStream(url.openStream());
			dis = new DicomInputStream(is);
			DicomObject dcmObj = dis.readDicomObject();
			DicomElement wcDcmElement = dcmObj.get(2625616);
			DicomElement wwDcmElement = dcmObj.get(2625617);
			DicomElement rowDcmElement = dcmObj.get(2621456);
			DicomElement colDcmElement = dcmObj.get(2621457);
			DicomElement imgOrientation = dcmObj.get(2097207);
			DicomElement imgPosition = dcmObj.get(2097202);
			DicomElement sliceThick = dcmObj.get(1572944);
			DicomElement frameOfRefUID = dcmObj.get(2097234);
			DicomElement pixelSpacingEle = dcmObj.get(2621488);
			DicomElement framedata = dcmObj.get(2621448);

			DicomElement imageType = dcmObj.get(524296);
			String image_type = "";
			if (imageType != null) {
				image_type = new String(imageType.getBytes());
				String[] imageTypes = image_type.split("\\\\");
				if (imageTypes.length >= 3) {
					image_type = imageTypes[2];
				}
			}

			DicomElement refImageSeq = dcmObj.get(528704);
			DicomElement refSOPInsUID = null;
			String referSopInsUid = "";
			if ((refImageSeq != null) && (refImageSeq.hasItems())) {
				DicomObject dcmObj1 = refImageSeq.getDicomObject();
				if (dcmObj1 != null) {
					refSOPInsUID = dcmObj1.get(528725);
				}
				referSopInsUid = refSOPInsUID != null ? new String(refSOPInsUID.getBytes()) : "";
			}

			String windowCenter = wcDcmElement != null ? new String(wcDcmElement.getBytes()) : "";
			String windowWidth = wwDcmElement != null ? new String(wwDcmElement.getBytes()) : "";
			int nativeRows = rowDcmElement != null ? rowDcmElement.getInt(false) : 0;
			int nativeColumns = colDcmElement != null ? colDcmElement.getInt(false) : 0;
			String imgOrient = imgOrientation != null ? new String(imgOrientation.getBytes()) : "";
			String sliceThickness = sliceThick != null ? new String(sliceThick.getBytes()) : "";
			String forUID = frameOfRefUID != null ? new String(frameOfRefUID.getBytes()) : "";
			String numberofframe = framedata != null ? new String(framedata.getBytes()) : "Empty";
			String sliceLoc = "";
			String imagePosition = "";
			if (imgPosition != null) {
				imagePosition = new String(imgPosition.getBytes());
				sliceLoc = imagePosition.substring(imagePosition.lastIndexOf("\\") + 1);
			}
			String pixelSpacing = pixelSpacingEle != null ? new String(pixelSpacingEle.getBytes()) : "";
			jobject.put("windowCenter", windowCenter.replaceAll("\\\\", "|"));
			jobject.put("windowWidth", windowWidth.replaceAll("\\\\", "|"));
			jobject.put("nativeRows", nativeRows);
			jobject.put("nativeColumns", nativeColumns);
			jobject.put("numberofframes", numberofframe);
			DicomElement cframedelay = dcmObj.get(2621449);
			DicomElement framedelayvec = dcmObj.get(1577061);
			DicomElement frametime = dcmObj.get(1577059);
			DicomElement RecommendedDisplayFrameRate = dcmObj.get(532804);

			if (frametime != null) {
				jobject.put("frametime", new String(frametime.getBytes()));
			}
			if (cframedelay != null) {

			}
			// 去掉cframedelay为空的判断
			if (framedelayvec != null) {
				byte[] arry = framedelayvec.getBytes();
				jobject.put("frametimevector", arry);
			}

			if (RecommendedDisplayFrameRate != null)
				;
			jobject.put("sliceLocation", sliceLoc);
			jobject.put("sliceThickness", sliceThickness);
			jobject.put("frameOfReferenceUID", forUID.replaceAll("", ""));
			jobject.put("imagePositionPatient", imagePosition);
			jobject.put("imageOrientPatient", imgOrient);
			jobject.put("pixelSpacing", pixelSpacing);
			jobject.put("refSOPInsUID", referSopInsUid.replaceAll("", ""));
			jobject.put("imageType", image_type);
			dis.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobject;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public String getServletInfo() {
		return "Short description";
	}
}