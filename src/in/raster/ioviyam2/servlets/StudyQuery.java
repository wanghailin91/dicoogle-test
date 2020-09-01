package in.raster.ioviyam2.servlets;

import in.raster.ioviyam2.model.QueryModel;
import in.raster.ioviyam2.services.DcmQR;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dcm4che2.data.DicomObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudyQuery extends HttpServlet {
	JSONObject sample;
	ArrayList<DicomObject> lists;
	String ae;
	String host;
	String port;
	String wado;
	QueryModel param;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		try {
			String patientid = request.getParameter("patientID");
			String patientname = request.getParameter("patientName");
			String modality = request.getParameter("Modality");
			String accno = request.getParameter("Acc-no");
			String birth = request.getParameter("Birthdate");
			String fromqDate = request.getParameter("From");
			String toqDate = request.getParameter("To");
			String fromtime = request.getParameter("tfrom");
			String totime = request.getParameter("tto");

			this.ae = request.getParameter("ae");
			this.host = request.getParameter("host");
			this.port = request.getParameter("port");
			this.wado = request.getParameter("wadoport");
			session.setAttribute("Ae", this.ae);
			session.setAttribute("host", this.host);
			session.setAttribute("port", this.port);
			session.setAttribute("wadoport", this.wado);
			this.param = new QueryModel();

			this.param.setModality(modality);
			this.param.setPatientId(patientid);
			this.param.setPatientName(patientname);
//			this.param.setFromdate(fromqDate);
//			this.param.setTodate(toqDate);
//			this.param.setTimefrom(fromtime);
//			this.param.setTimeto(totime);
			this.param.setAccno(accno);
			this.param.setBirth(birth);
			out.print(getData(this.param));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	JSONArray getData(QueryModel param) {
		JSONArray list = new JSONArray();
		try {
			this.lists = new ArrayList();
			Object[] TEST = (Object[]) argumentConst(param).toArray();
			String[] argss = Arrays.copyOf(TEST, TEST.length, String[].class);

			DcmQR query = new DcmQR("iOviyam2");
			this.lists = ((ArrayList) query.QueryDcm(argss));
	
			for (DicomObject dimobject : this.lists) {
				JSONObject Objectjson = new JSONObject();

				String PatientName = dimobject.getString(1048592);
				String PatientBirthDate = dimobject.getString(1048624);
				// String PatientSex = dimobject.getString(1048640);
				String[] Modality = dimobject.getStrings(524385);
				String id = dimobject.getString(1048608);
				String seriesnumber = dimobject.getString(2101766);
				// String studydsp = dimobject.getString(528432);
				String studydate = dimobject.getString(524320);

				// 修改拍片的日期格式，改为数字显示:12-06-2016
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sformate = new SimpleDateFormat("dd-MM-yyyy");
				try {
					if (studydate != null) {
						Date date = formatter.parse(studydate);
						studydate = sformate.format(date);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					// studydate = studydate;
				}
				//
				String instance = dimobject.getString(2101768);
				String accno = dimobject.getString(524368);
				String studyid = dimobject.getString(2097165);
				String studydesc = dimobject.getString(528432);
				String sex = dimobject.getString(1048640);
				String thickness = dimobject.getString(1572944);
				Objectjson.put("PatientName", PatientName);
				Objectjson.put("PatientId", id);
				Objectjson.put("DateOfBirth", PatientBirthDate);
				Objectjson.put("AcessionNo", accno);
				Objectjson.put("StudyDate", studydate);
				Objectjson.put("Modality", Modality);
				Objectjson.put("NumberOfImages", instance);
				Objectjson.put("studyid", studyid);
				Objectjson.put("seriesno", seriesnumber);
				Objectjson.put("studydesc", studydesc);
				Objectjson.put("sex", sex);
				Objectjson.put("thickness", thickness);

				list.put(Objectjson);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	ArrayList<String> argumentConst(QueryModel param) {
		ArrayList<String> armnts = new ArrayList<String>();

		armnts.add(this.ae + "@" + this.host + ":" + this.port);
		armnts.add("-r");
		armnts.add("PatientID");
		armnts.add("-r");
		armnts.add("PatientName");
		armnts.add("-r");
		armnts.add("PatientSex");
		armnts.add("-r");
		armnts.add("PatientBirthDate");
		armnts.add("-r");
		armnts.add("ModalitiesInStudy");
		armnts.add("-r");
		armnts.add("StudyDescription");

		if ((!param.getPatientId().equalsIgnoreCase(""))
				&& (param.getPatientId() != null)) {
			armnts.add("-qPatientID=" + param.getPatientId());
		}

		if ((!param.getPatientName().equalsIgnoreCase(""))
				&& (param.getPatientName() != null)) {
			armnts.add("-qPatientName=" + param.getPatientName());
		}

		if ((!param.getAccessionNo().equalsIgnoreCase(""))
				&& (param.getAccessionNo() != null)) {
			armnts.add("-qAccessionNumber=" + param.getAccessionNo());
		}
		if ((!param.getBirthDate().equalsIgnoreCase(""))
				&& (param.getBirthDate() != null)) {
			armnts.add("-qPatientBirthDate=" + param.getBirthDate());
		}
		if ((!param.getModality().equalsIgnoreCase(""))
				&& (!param.getModality().equalsIgnoreCase("All"))) {
			armnts.add("-qModalitiesInStudy=" + param.getModality());
		}

		if ((!param.getFromDate().equals(""))
				|| (!param.getToDate().equals(""))) {
			armnts.add("-qStudyDate=" + param.getFromDate() + "-"
					+ param.getToDate());
		}

		if ((!param.getFromTime().equals(""))
				|| (!param.getToTime().equals(""))) {
			armnts.add("-qStudyTime=" + param.getFromTime() + "-"
					+ param.getToTime());
		}

		return armnts;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	public String getServletInfo() {
		return "Short description";
	}
}