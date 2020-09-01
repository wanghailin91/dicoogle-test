package in.raster.ioviyam2.servlets;

import in.raster.ioviyam2.services.DcmEcho;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EchoService extends HttpServlet {
	String ae;
	String host;
	String port;
	String wado;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			this.ae = request.getParameter("ae");
			this.host = request.getParameter("host");
			this.port = request.getParameter("port");
			this.wado = request.getParameter("wadoport");
			out.print(getDataInstance(this.ae, this.host, this.port));
		} finally {
			out.close();
		}
	}

	ArrayList<String> argumentConst(String ae, String host, String port) {
		ArrayList armnts = new ArrayList();
		armnts.add(ae + "@" + host + ":" + port);
		return armnts;
	}

	boolean getDataInstance(String ae, String host, String port){
    Object[] TEST = (Object[])argumentConst(ae, host, port).toArray();
    String[] argss = Arrays.copyOf(TEST, TEST.length, String[].class);

    DcmEcho echo = new DcmEcho("iOviyam2");
    boolean sucess = echo.echoStatus(argss);
    return sucess;
  }

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}