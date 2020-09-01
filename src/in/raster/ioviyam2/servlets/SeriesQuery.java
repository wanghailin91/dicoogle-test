package in.raster.ioviyam2.servlets;

import in.raster.ioviyam2.model.QueryModel;
import in.raster.ioviyam2.services.DcmQR;
import in.raster.ioviyam2.util.ReadConfigUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dcm4che2.data.DicomObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class SeriesQuery extends HttpServlet {
	ArrayList<DicomObject> lists;
	QueryModel param;
	String ae;
	String host;
	String port;
	String wado;
	String objid;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		this.ae = ((String) session.getAttribute("Ae"));
		this.host = ((String) session.getAttribute("host"));
		this.port = ((String) session.getAttribute("port"));
		this.wado = ((String) session.getAttribute("wadoport"));
		try {
			File filelocation = new File(System.getProperty("user.dir"));
			String location = filelocation.getAbsolutePath() + File.separator;
			Map<String, String> config = ReadConfigUtil.readConfig(ae, host, port, wado, location);
			if (null != config) {
				this.ae = config.get("ae");
				this.host = config.get("host");
				this.port = config.get("port");
				this.wado = config.get("wado");
			}
			this.param = new QueryModel();
			this.param.setPatientId(request.getParameter("PatientID"));
			this.param.setStudyId(request.getParameter("StudyID"));
			out.print(getDataSeries(this.param));
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	ArrayList<String> argumentConstSeries(QueryModel param) {
		ArrayList<String> armnts = new ArrayList<String>();
		armnts.add(this.ae + "@" + this.host + ":" + this.port);
		armnts.add("-S");

		armnts.add("-q0020000D=" + param.getstudyId());
		armnts.add("-r");
		armnts.add("0008103E");
		return armnts;
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
				DicomObject dimobject = (DicomObject) this.lists.get(i);
				this.objid = dimobject.getString(524312);

				int index = 0;
				String indexnum = dimobject.getString(2097171);
				String numberframe = dimobject.getString(1577062);

				if (indexnum == null) {
					list.put("imagestream.do?studyUID=" + studyId + "&seriesUID=" + seriesID + "&objectUID="
							+ this.objid);
					// list.put("Image.do?serverURL=http://192.168.30.83:8080/wado&study="
					// + studyId + "&series=" + seriesID + "&object=" +
					// this.objid);
				} else {
					index = Integer.parseInt(dimobject.getString(2097171));
					list.put(index, "imagestream.do?studyUID=" + studyId + "&seriesUID=" + seriesID + "&objectUID="
							+ this.objid);
					// list.put(index,"Image.do?serverURL=http://192.168.30.83:8080/wado&study="
					// + studyId + "&series=" + seriesID + "&object=" +
					// this.objid);
				}

			}

			for (int i = 0; i < list.length(); i++) {
				if (list.get(i).equals(null)) {
					list.remove(i);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
	}

	JSONArray getDataSeries(QueryModel param) {
		JSONArray list = new JSONArray();
		try {
			this.lists = new ArrayList();
			Object[] TEST = (Object[]) argumentConstSeries(param).toArray();
			String[] argss = Arrays.copyOf(TEST, TEST.length, String[].class);

			DcmQR query = new DcmQR("iOviyam2");
			this.lists = ((ArrayList) query.QueryDcm(argss));

			for (DicomObject dimobject : this.lists) {
				JSONObject Objectjson = new JSONObject();

				String seriesUID = dimobject.getString(2097166);

				String seriesNumber = dimobject.getString(2097169);
				String modality = dimobject.getString(524384);

				String bodypart = dimobject.getString(1572885);
				//String totalInstance = dimobject.getString(2101769);
				String seriesDesc = dimobject.getString(528446);

				Objectjson.put("seriesUID", seriesUID);
				Objectjson.put("seriesNumber", seriesNumber);
				Objectjson.put("modality", modality);
				Objectjson.put("seriesDesc", seriesDesc);
				Objectjson.put("bodyPart", bodypart);
				Objectjson.put("patientId", param.getPatientId());
				Objectjson.put("studyUID", param.getstudyId());
				JSONArray array=getDataInstance(param.getPatientId(), param.getstudyId(), seriesUID);
				Objectjson.put("url",array);
				Objectjson.put("totalInstances", array.length());
				list.put(Objectjson);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
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