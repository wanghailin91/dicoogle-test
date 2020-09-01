package in.raster.ioviyam2.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class Config extends HttpServlet {
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		File filelocation = new File(System.getProperty("user.dir"));
		String location = filelocation.getAbsolutePath() + File.separator;
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String ae = request.getParameter("ae");
		String hostname = request.getParameter("host");
		String port = request.getParameter("port");
		String wado = request.getParameter("wado");
		String type = request.getParameter("type");
		Properties prop = new Properties();
		JSONObject Objectjson = new JSONObject();
		File filedata = new File(location + "iOviyam.properties");
		try {
			if (type.equals("read")) {
				if (filedata.exists()) {
					prop.load(new FileInputStream(filedata));
					Objectjson.put("aetitle", prop.getProperty("AETitle"));
					Objectjson.put("hostname", prop.getProperty("Host"));
					Objectjson.put("port", prop.getProperty("Port"));
					Objectjson.put("wado", prop.getProperty("wado"));
					out.print(Objectjson);
				} else {
					out.print("false");
				}
			} else if (type.equals("write")) {
				prop.setProperty("AETitle", ae);
				prop.setProperty("Host", hostname);
				prop.setProperty("Port", port);
				prop.setProperty("wado", wado);
				prop.store(new FileOutputStream(filedata), null);
				out.print("updated");
			}

		} catch (Exception ex) {
			out.print("failed");
			ex.printStackTrace();
			out.close();
		}
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