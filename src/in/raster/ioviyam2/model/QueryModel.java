package in.raster.ioviyam2.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryModel {
	private String patientId = "";
	private String patientName = "";
	private String birthDate = "";
	private String searchDate = "";
	private String modality = "";
	private String from = "";
	private String to = "";
	private String searchDays = "";
	private String accessionNo = "";
	private String seriesId = "";
	private String studyId = "";
	private String fromdate = "";
	private String todate = "";
	Date date = new Date();
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat brthdateformat = new SimpleDateFormat("dd/MM/yyyy");
	String s = this.dateFormat.format(this.date);
	String pdate = null;
	String fromtime = "";
	String totime = "";
	String fromq;
	String toq;

	public void setPatientId(String patientId) {
		if ((patientId.equals("null")) || (patientId == null))
			this.patientId = "";
		else
			this.patientId = patientId;
	}

	public void getprada(String dates) {
		this.pdate = dates;
	}

	public void setPatientName(String patientName) {
		if ((patientName.equals("null")) || (patientName == null))
			this.patientName = "";
		else
			this.patientName = patientName;
	}

	public void setTimefrom(String fromtime) {
		if (fromtime == null)
			this.fromtime = "";
		else
			this.fromtime = fromtime.replace(":", "");
	}

	public void setTimeto(String totime) {
		if (totime == null)
			this.totime = "";
		else
			this.totime = totime.replace(":", "");
	}

	public void setBirth(String birthDate) {
		if ((birthDate.equals("null")) || (birthDate == null))
			this.birthDate = "";
		else
			this.birthDate = birthDate.replace("/", "");
	}

	public void setAccno(String Accno) {
		if ((Accno.equals("null")) || (Accno == null))
			this.accessionNo = "";
		else
			this.accessionNo = Accno;
	}

	public void setModality(String modality) {
		if ((modality.equals("null")) || (modality == null))
			this.modality = "";
		else
			this.modality = modality;
	}

	public void setFromdate(String date) {
		if ((date.equals("null")) || (date == null))
			this.fromdate = "";
		else
			this.fromdate = date.replace("/", "");
	}

	public void setTodate(String to) {
		if ((to.equals("null")) || (to == null))
			this.todate = "";
		else
			this.todate = to.replace("/", "");
	}

	public void setBirthDate(String birthDate) {
		if (birthDate == null)
			this.birthDate = "";
		else
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat sformate = new SimpleDateFormat("yyyyMMdd");
				Date datee = formatter.parse(birthDate);
				birthDate = sformate.format(datee);
				this.birthDate = birthDate;
			} catch (ParseException ex) {
				ex.printStackTrace();
				Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE,
						null, ex);
			}
	}

	public void setSearchDate(String searchDate) {
		if (searchDate == null)
			this.searchDate = "";
		else
			this.searchDate = searchDate;
	}

	public void setTo(String to) {
		if ((to == null) || (to.equalsIgnoreCase("")))
			this.to = "";
		else
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat sformate = new SimpleDateFormat("yyyyMMdd");

				Date datee = formatter.parse(to);
				to = sformate.format(datee);
				this.to = to;
			} catch (ParseException ex) {
				ex.printStackTrace();
				Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE,
						null, ex);
			}
	}

	public void setFrom(String from) {
		if ((from == null) || (from.equalsIgnoreCase("")))
			this.from = "";
		else
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat sformate = new SimpleDateFormat("yyyyMMdd");

				Date datee = formatter.parse(from);
				from = sformate.format(datee);
				this.from = from;
			} catch (ParseException ex) {
				ex.printStackTrace();
				Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE,
						null, ex);
			}
	}

	public void setFromQ(String from) {
		if (from == null)
			this.fromq = "";
		else
			this.fromq = from;
	}

	public void setToQ(String to) {
		if (to == null)
			this.toq = "";
		else
			this.toq = to;
	}

	public String getfromQ() {
		return this.fromq;
	}

	public String gettoQ() {
		return this.toq;
	}

	public String getSearchDaysQ() {
		String date = "";
		if ((!this.fromq.equals("")) && (!this.toq.equals(""))) {
			date = this.fromq + "-" + this.toq;
		}
		return date;
	}

	public void setSearchDays(String searchDays) {
		if (searchDays == null)
			this.searchDays = "";
		else
			this.searchDays = searchDays;
	}

	public void setSearchDays(String searchDays, String from, String to) {
		if ((searchDays == null) && (from == null) && (to == null))
			this.searchDays = "";
		else
			try {
				this.searchDays = searchDays;
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat sformate = new SimpleDateFormat("yyyyMMdd");

				Date datee = formatter.parse(from);
				Date todate = formatter.parse(to);
				from = sformate.format(datee);
				to = sformate.format(todate);
				this.from = from;
				this.to = to;
			} catch (ParseException ex) {
				Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE,
						null, ex);
			}
	}

	public String getSearchDays() {
		return this.searchDays;
	}

	public String getSearchTime() {
		String time = "";
		if ((!this.fromtime.equals("")) && (!this.totime.equals("")))
			time = this.fromtime + "-" + this.totime;
		else if (!this.fromtime.equals(""))
			time = this.fromtime;
		else if (!this.totime.equals("")) {
			time = this.totime;
		}
		return time;
	}

	public String getLastWeek() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currDate = Calendar.getInstance();
		currDate.add(5, -7);
		currDate.getTime();
		return dateFormat.format(currDate.getTime());
	}

	public String getLastMonth() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currDate = Calendar.getInstance();
		currDate.add(5, -31);
		currDate.getTime();
		return dateFormat.format(currDate.getTime());
	}

	public String getYesterday() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currDate = Calendar.getInstance();
		currDate.add(5, -1);
		currDate.getTime();
		return dateFormat.format(currDate.getTime());
	}

	public String getSearchDate() {
		if (this.searchDays.equalsIgnoreCase("Last week")) {
			String lastWeek = getLastWeek();
			lastWeek = lastWeek.replace("-", "");
			this.s = this.s.replace("-", "");
			this.searchDate = (lastWeek + "-" + this.s);
		} else if (this.searchDays.equalsIgnoreCase("Today")) {
			this.s = this.s.replace("-", "");
			this.searchDate = (this.s + "-" + this.s);
		} else if (this.searchDays.equalsIgnoreCase("Last Month")) {
			String lastMonth = getLastMonth();
			lastMonth = lastMonth.replace("-", "");
			this.s = this.s.replace("-", "");
			this.searchDate = (lastMonth + "-" + this.s);
		} else if (this.searchDays.equalsIgnoreCase("Yesterday")) {
			String yesterDay = getYesterday();
			yesterDay = yesterDay.replace("-", "");
			this.searchDate = (yesterDay + "-" + yesterDay);
		} else if (this.searchDays.equalsIgnoreCase("Between")) {
			this.from = this.from.replace("/", "");
			this.to = this.to.replace("/", "");
			this.from = this.from.replace("-", "");
			this.to = this.to.replace("-", "");
			if (this.from.equals(" ")) {
				this.from = " ";
			}
			if (this.to.equals(" ")) {
				this.to = "";
			}

			this.searchDate = (this.from + "-" + this.to);
		} else if (this.searchDays.equalsIgnoreCase("quick")) {
			this.searchDate = (this.fromq + "-" + this.toq);
		} else {
			this.searchDate = "";
		}

		return this.searchDate;
	}

	public String getBirthDate() {
		return this.birthDate;
	}

	public String getFrom() {
		return this.from;
	}

	public String getFromTime() {
		return this.fromtime;
	}

	public String getToTime() {
		return this.totime;
	}

	public String getModality() {
		return this.modality;
	}

	public String getPatientId() {
		return this.patientId;
	}

	public String getPatientName() {
		return this.patientName;
	}

	public String getTo() {
		return this.to;
	}

	public String getAccessionNo() {
		return this.accessionNo;
	}

	public void setAccessionNo(String accessionNo) {
		this.accessionNo = accessionNo;
	}

	public void setSeriesId(String seriesId) {
		if (seriesId == null)
			this.seriesId = "";
		else
			this.seriesId = seriesId;
	}

	public void setStudyId(String studyId) {
		if (studyId == null)
			this.studyId = "";
		else
			this.studyId = studyId;
	}

	public String getstudyId() {
		return this.studyId;
	}

	public String getFromDate() {
		return this.fromdate;
	}

	public String getToDate() {
		return this.todate;
	}

	public String getBirthdate() {
		return this.birthDate;
	}
}