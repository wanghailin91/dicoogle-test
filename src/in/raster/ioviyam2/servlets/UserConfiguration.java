package in.raster.ioviyam2.servlets;

import in.raster.ioviyam2.xml.handler.QueryParamHandler;
import in.raster.ioviyam2.xml.handler.UserHandler;
import in.raster.ioviyam2.xml.model.Button;
import in.raster.ioviyam2.xml.model.User;
import org.json.JSONArray;

import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class UserConfiguration extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String settings = request.getParameter("settings");
            String actionToDo = request.getParameter("todo");
            String settingsValue = request.getParameter("settingsValue");

            InitialContext ctx = new InitialContext();
            Subject subject = (Subject) ctx.lookup("java:comp/env/security/subject");

            List prinList = new ArrayList(subject.getPrincipals());

            Principal p = (Principal) prinList.get(0);
            String userName = p.getName();
            UserHandler uh = new UserHandler();
            String str = null;
            out = response.getWriter();
            User user = uh.findUserByName(userName);

            if (user == null) {
                user = new User();
                user.setUserName(userName);
                Button btn = new Button();
                btn.setLabel("Today CT");
                btn.setDateCrit("t");
                btn.setModality("CT");
                uh.addNewUser(btn, userName);
            }

            if (user != null)
                if (actionToDo.equalsIgnoreCase("READ")) {
                    if (settings.equals("theme")) {
                        str = user.getTheme();
                    } else if (settings.equals("sessTimeout")) {
                        str = user.getSessTimeout();
                    } else if (settings.equals("userName")) {
                        str = user.getUserName();
                        String sessTimeout = user.getSessTimeout();
                        if (sessTimeout != null) {
                            HttpSession session = request.getSession(false);
                            session.setMaxInactiveInterval(Integer.parseInt(sessTimeout));
                        }
                    } else if (settings.equals("viewerSlider")) {
                        str = user.getViewerSlider();
                    } else if (settings.equals("roles")) {
                        Principal pTmp = (Principal) prinList.get(1);
                        str = pTmp.toString();
                    } else if (settings.equals("buttons")) {
                        QueryParamHandler qph = new QueryParamHandler();
                        List butList = qph.getAllButtons(userName);
                        JSONArray jsonArray = new JSONArray(butList);
                        str = jsonArray.toString();
                    }
                    out.print(str);
                } else if (actionToDo.equalsIgnoreCase("UPDATE")) {
                    if (settings.equals("theme"))
                        user.setTheme(settingsValue);
                    else if (settings.equals("sessTimeout"))
                        user.setSessTimeout(settingsValue);
                    else if (settings.equals("viewerSlider")) {
                        user.setViewerSlider(settingsValue);
                    }
                    uh.updateUser(user);
                    out.println("Success");
                }
        } catch (Exception ex) {
            // Logger.getLogger(UserConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (null != out) {
                out.close();
            }
        }
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