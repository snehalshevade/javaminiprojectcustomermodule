
package com.cybage.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cybage.dao.CustomerDaoImpl;
import com.cybage.pojos.Batch;
import com.cybage.pojos.BatchInfo;
import com.cybage.pojos.Enrollment;
import com.cybage.pojos.Plans;
import com.cybage.pojos.Sports;
import com.cybage.pojos.Users;
import com.cybage.services.CustomerServiceI;
import com.cybage.services.CustomerServiceImpl;
import com.mysql.cj.Session;

//@ServletSecurity(
//		value = @HttpConstraint(
//				rolesAllowed = {"customer"}
//				)
//		)
public class CustomerServlet extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(CustomerServlet.class.getName());
	private static final long serialVersionUID = 1L;
	CustomerServiceI cs = new CustomerServiceImpl();

	public CustomerServlet() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
        //listsports method will return the list of sports to the jsp page:seesports.jsp
		if (path.equals("/listsports")) {
			try {
				List<Sports> sportlist = cs.getSports();
				request.setAttribute("sports", sportlist);
				request.getRequestDispatcher("/member/seesports.jsp").forward(request, response);
				logger.info("member have seen list of sports");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//listplans method will return the list of plans to the jsp page:seeplans.jsp
		if (path.equals("/listplans")) {
			try {
				List<Plans> planslist = cs.getplans();
				request.setAttribute("plans", planslist);
				request.getRequestDispatcher("/member/seeplans.jsp").forward(request, response);
				logger.info("member have seen list of plans");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//listbatches method will return the list of batches to the jsp page:seebatches.jsp
		if (path.equals("/listbatches")) {
			try {
				List<BatchInfo> batcheslist = cs.getbatches();
				request.setAttribute("batches", batcheslist);
				request.getRequestDispatcher("/member/seebatches.jsp").forward(request, response);
				logger.info("member have seen list of batches");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//listbatches1 method will return the list of batches according to particular plan id
		//to the jsp page:seesports.jsp
		if (path.equals("/listbatches1")) {
			try {
				int planid = Integer.parseInt(request.getParameter("planId"));
				List<BatchInfo> batcheslist = cs.getbatchesbyId(planid);
				request.setAttribute("batches", batcheslist);
				request.getRequestDispatcher("/member/seebatches.jsp").forward(request, response);
				logger.info("member have seen list of batches by selecting particular plan");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//adduser method will add the registered user in db and will redirect the user to the login page.
		if (path.equals("/adduser")) {
			try {
				cs.adduser(request.getParameter("username"), request.getParameter("address"),
						request.getParameter("password"), request.getParameter("phone"), request.getParameter("email"));
				request.getRequestDispatcher("/login.jsp").forward(request, response);
				logger.info("Registration successfull..!! with user name:" + request.getParameter("username"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//viewprofile method will return the user object of logged in user to the jsp page:viewprofile.jsp
		if (path.equals("/viewprofile")) {
			HttpSession session = request.getSession(true);
			String userName = (String) session.getAttribute("username");
			System.out.println("username" + userName);
			try {
				Users user = cs.getuser(userName);
				request.setAttribute("user", user);
				request.getRequestDispatcher("/member/viewprofile.jsp").forward(request, response);
				logger.info("Member have viewed his profile");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//updateuser method will return the updated user object to the jsp page:viewprofile.jsp
		if (path.equals("/updateuser")) {
			try {
				HttpSession session = request.getSession(true);
				String userName = (String) session.getAttribute("username");
				Users user = cs.update(userName, request.getParameter("address"), request.getParameter("password"),
						request.getParameter("phone"), request.getParameter("email"));
				String msg = "your profile updated successfully..";
				request.setAttribute("msg", msg);
				request.setAttribute("user", user);
				request.getRequestDispatcher("/member/viewprofile.jsp").forward(request, response);
				// response.sendRedirect("viewprofile");
				logger.info("member have successfully updated his profile.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//enrolluser method will enroll the user for selected batch and again to the jsp page:seesports.jsp
		if (path.equals("/enrolluser")) {
			System.out.println("servlet::enroll user");
			int batchId = Integer.parseInt(request.getParameter("batchId"));
			HttpSession session = request.getSession(true);
			String userName = (String) session.getAttribute("username");
			try {
				cs.enrolluser(batchId, userName);
				String msg = "you have successfully enrolled for batch..";
				request.setAttribute("msg", msg);
				request.getRequestDispatcher("/member/seebatches.jsp").include(request, response);
				logger.info("member have enrolled for batch with batchid::" + batchId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//viewenrollments method will return the list of enrollments to the jsp page:myenrollments.jsp
		if (path.equals("/viewenrollments")) {
			try {
				List<Enrollment> enlist = cs.getenrollments();
				request.setAttribute("enrollments", enlist);
				request.getRequestDispatcher("/member/myenrollments.jsp").forward(request, response);
				logger.info("member have seen his enrollments");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//renew method will renew the membership of user and forward the user to the jsp page:myenrollments.jsp
		if (path.equals("/renew")) {
			try {
				List<Enrollment> enlist = cs.renew(Integer.parseInt(request.getParameter("enrollId")));
				String msg = "your have renewed the enrollment";
				request.setAttribute("enrollments", enlist);
				request.setAttribute("msg", msg);
				request.getRequestDispatcher("/member/myenrollments.jsp").forward(request, response);
				logger.info("member have seen his enrollments");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
