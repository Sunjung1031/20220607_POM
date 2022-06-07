package user.command;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.model.Edu;
import main.model.Paging;
import main.model.WorkExperience;
import mvc.command.CommandHandler;
import personnel.model.Personnel;
import personnel.service.PersonnelService;
import user.model.License;
import user.model.User;
import user.service.LicenseService;
import user.service.UserService;

public class UserHandler implements CommandHandler {
	private final static String MAIN_FORM = "WEB-INF/view/main.jsp";
	private UserService userService = new UserService();
	private User user = new User();
	private PersonnelService personalService = new PersonnelService();

	private LicenseService licenseService = new LicenseService();
	private ArrayList<License> licenseList = null;
	private int no;

	@Override
	public String process(HttpServletRequest rq, HttpServletResponse rp) throws Exception {
		ArrayList<Personnel> personnelList = new ArrayList<Personnel>();
		if (rq.getParameter("init") != null)
			return MAIN_FORM;

		if (rq.getParameter("searchField") != null && rq.getParameter("searchField") != "") {
			int search = Integer.parseInt(rq.getParameter("search"));
			String searchField = rq.getParameter("searchField");
			int startPage = 1;
			if (rq.getParameter("startPage") != null)
				startPage = Integer.parseInt(rq.getParameter("startPage"));

			int currentPage = 1;
			if (rq.getParameter("currentPage") != null)
				currentPage = Integer.parseInt(rq.getParameter("currentPage"));

			int total = personalService.getTotalPage(search, searchField);

			Paging paging = new Paging(total, startPage, currentPage);

			rq.setAttribute("search", search);
			rq.setAttribute("searchField", searchField);
			rq.setAttribute("paging", paging);
			if (Integer.parseInt(rq.getParameter("search")) == 9) {
				search = 9;
				rq.setAttribute("searchError2", Boolean.TRUE);
				return MAIN_FORM;
			}
			personnelList = personalService.getSearchPersonnel(search, searchField, paging);
		}
		rq.setAttribute("personnelList", personnelList);

		if (rq.getParameter("choose") != null) {
			if (rq.getParameter("choose").equalsIgnoreCase("personnel"))
				rq.setAttribute("choose", Boolean.TRUE);
			else
				rq.setAttribute("choose", Boolean.FALSE);
		}

		if (rq.getParameter("modify1") != null)
			rq.setAttribute("modify1", Boolean.TRUE);

		if (rq.getParameter("no") != null && rq.getParameter("no") != "") {
			no = Integer.parseInt(rq.getParameter("no"));
			rq.setAttribute("no", no);
			user = userService.getUserByNo(user, no);
			String uploadPath = rq.getRealPath("/upload/2022_상반기_증명사진");
			String filename = user.getFilename();
			rq.setAttribute("img", filename);
			rq.setAttribute("user", user);
		} else if (rq.getParameter("NoSummit") != null) {
			System.out.println("NoSummit");
		} else {
//			rq.setAttribute("wrongRoute", Boolean.TRUE);
//			return MAIN_FORM;
		}
		
		System.out.println("test3");
		ArrayList<License> licenseList = new ArrayList<License>();
		licenseList = licenseService.GetAllLicenseList(no);
		System.out.println("test4");
		
		rq.setAttribute("licenseList", licenseList);

		if (rq.getMethod().equalsIgnoreCase("POST"))
			return processSubmit(rq, rp);
		else if (rq.getMethod().equalsIgnoreCase("GET"))
			return processForm(rq, rp);
		else
			return null;
	}

	private String processForm(HttpServletRequest rq, HttpServletResponse rs) throws SQLException {

		return MAIN_FORM;
	}

	private String processSubmit(HttpServletRequest rq, HttpServletResponse rs) throws SQLException {
		licenseService = new LicenseService();
		licenseList = new ArrayList<License>();

		Date license_day = null;
		for (int i = 1; i <= 5; i++) {
			if (rq.getParameter("lic_no" + i) != null) {
				license_day = Datering(rq.getParameter("license_day" + i));
				licenseList.add(new License(Integer.parseInt(rq.getParameter("no")),
						Integer.parseInt(rq.getParameter("lic_no" + i)), rq.getParameter("license" + i), license_day));
			}
		}
		licenseService.InsertAllLicense(licenseList, no);
		licenseList = licenseService.GetAllLicenseList(no);
		rq.setAttribute("licenseList", licenseList);

		Date school_out = null;
		if (rq.getParameter("school_out") != null && rq.getParameter("school_out") != "")
			school_out = Datering(rq.getParameter("school_out"));

		System.out.println("test4");

		String disabled = null;
		int disabled_grade = 0;
		Date disabled_day = null;
		if (rq.getParameter("disabled") != null) {
			if (rq.getParameter("disabled").equalsIgnoreCase("N")) {
				disabled = rq.getParameter("disabled");
				disabled_grade = 0;
				disabled_day = null;
			} else {
				disabled = rq.getParameter("disabled");
				disabled_grade = Integer.parseInt(rq.getParameter("disabled_grade"));
				disabled_day = Datering(rq.getParameter("disabled_day"));
			}
		}
		System.out.println("test5");

		System.out.println("disabled_day :" + disabled_day);
		user = new User(no, rq.getParameter("filename"), rq.getParameter("filerealname"), rq.getParameter("name"),
				rq.getParameter("reg_num"), rq.getParameter("phone"), rq.getParameter("addr"),
				rq.getParameter("email"), rq.getParameter("school_name"), rq.getParameter("school_major"), school_out,
				Integer.parseInt(rq.getParameter("tall")), Integer.parseInt(rq.getParameter("weight")),
				rq.getParameter("eye_l"), rq.getParameter("eye_r"), rq.getParameter("gender"), rq.getParameter("marry"),
				disabled, disabled_grade, disabled_day);
		user = userService.modifyUser(user);

		rq.setAttribute("user", user);
		return MAIN_FORM;
	}

	private Date Datering(Object value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dating = null;
		try {
			String param = (String) value;
			dating = new Date(sdf.parse(param).getTime());
		} catch (Exception e) {
			System.out.println("error : UserHandler.Datering()");
			System.out.println(e.getMessage());
		}
		return dating;
	}
}
