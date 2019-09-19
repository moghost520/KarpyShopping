package com.web.store.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.web.store.exception.MemberNotFoundException;
import com.web.store.model.CreditCardBean;
import com.web.store.model.ManagerBean;
import com.web.store.model.MemberBean;
import com.web.store.service.MemberService;

import _00_init.util.SystemUtils2019;

@Controller
public class MemberController {

	// TODO: Input String check -> should not include space or special symbol which
	// likes SQL command (pending)

	@Autowired
	MemberService service;

	@Autowired
	ServletContext context;

	@InitBinder
	public final void initBinderUsuariosFormValidator(final WebDataBinder binder, final Locale locale) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@ExceptionHandler({ MemberNotFoundException.class })
	public ModelAndView handleError(HttpServletRequest request, MemberNotFoundException exception) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("invalidAccount", exception.getAccount());
		mv.addObject("exception", exception);
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("url", request.getRequestURL() + "?" + request.getQueryString());
		mv.setViewName("errorPage/memberNotFound");
		return mv;
	}

	@RequestMapping("/members")
	public String list(Model model) {
		List<MemberBean> list = service.getAllMember();
		model.addAttribute("members", list);
		return "members";
	}

	@RequestMapping("/member")
	public String getMemberById(@RequestParam("account") String account, Model model) {
		model.addAttribute("member", service.getMemberByAccount(account));
		return "member";
	}

	@RequestMapping(value = "/idExists", method = RequestMethod.GET)
	public String idExists(Model model, Integer mId) {
		MemberBean mb = new MemberBean();
		model.addAttribute("MemberBean", mb);
		return "idcheck/idExists";
	}

	@RequestMapping(value = "/idExists", method = RequestMethod.POST)
	public String idExists(@ModelAttribute("MemberBean") MemberBean mb) {
		System.out.println(mb.getAccount());
		service.idExists(mb.getmId());
		return "idcheck/idFound";
	}

//登入控制器
	@RequestMapping(value = "/memberLogin", method = RequestMethod.GET)
	public String getManagerForm(Model model1, HttpServletRequest request) {
		HttpSession session = request.getSession();
		MemberBean mb = new MemberBean();
		model1.addAttribute("memberBean", mb);
		char[][] pairs = { { 'a', 'z' }, { 'A', 'Z' }, { '0', '9' } };
		ThirdPartyLoginController.RANDOM_STRING = new RandomStringGenerator.Builder().withinRange(pairs).build()
				.generate(ThirdPartyLoginController.RANDOM_STRING_LENGTH);
		System.out.println("Random String:" + ThirdPartyLoginController.RANDOM_STRING);
		session.setAttribute("state", ThirdPartyLoginController.RANDOM_STRING);

		return "member/memberLogin";
	}

	@RequestMapping(value = "/memberLogin", method = RequestMethod.POST)
	public String processManagerForm(@ModelAttribute("memberBean") MemberBean mb, @RequestParam("form") boolean form,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		System.out.println("form :" + form);

		if (!form) {
			int mId = service.addMember(mb);
			mb.setmId(mId);
			System.out.println(mb.toString());
			session.setAttribute("memberLoginOK", mb);
		} else {
			MemberBean Member = new MemberBean();
			Member = service.checkIdPassword(mb.getAccount(), mb.getPassword());
			System.out.println(Member.toString());
			session.setAttribute("memberLoginOK", Member);
		}
		String uri = (String) session.getAttribute("requestURI");
		System.out.println("uri : " + uri);
		if (uri == null) {
			return "redirect:/home";
		} else {
			session.removeAttribute("requestURI");
			return "redirect:/" + uri.substring(15);
		}
	}

//註冊會員控制器
	@RequestMapping(value = "/member/add", method = RequestMethod.GET)
	public String getAddNewMemberForm(Model model) {
		MemberBean mb = new MemberBean();
		model.addAttribute("MemberBean", mb);
		return "registration/addMember";
	}

	@RequestMapping(value = "/member/add", method = RequestMethod.POST)
	public String processAddNewMemberForm(@ModelAttribute("MemberBean") MemberBean mb, BindingResult result,
			HttpServletRequest request) {
		service.addMember(mb);
		return "redirect:/members";
	}

//修改會員控制器

	@RequestMapping(value = "/updatemember", method = RequestMethod.GET)
	public String Changemamber(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		MemberBean mb = (MemberBean) session.getAttribute("memberLoginOK");
		model.addAttribute("MemberBean", mb);
		return "account/updatemember";
	}

	@RequestMapping(value = "/updatemember", method = RequestMethod.POST)
	public String Changemember(@ModelAttribute("MemberBean") MemberBean mb, @RequestParam("county") String county,
			@RequestParam("city") String city, @RequestParam("addr") String addr, @RequestParam("gender") String gender,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy/MM/dd") Date date, BindingResult result,
			HttpServletRequest request) {
		System.out.println("=====date = " + date);
		HttpSession session = request.getSession();
		MemberBean memberBean = (MemberBean) session.getAttribute("memberLoginOK");
//		memberBean.setName(mb.getName());
//		memberBean.setMemberImage(mb.getMemberImage());
//		memberBean.setEmail(mb.getEmail());
//		memberBean.setTel(mb.getTel());
//		memberBean.setBirthday(mb.getBirthday());
		memberBean.setBirthday(new java.sql.Timestamp(date.getTime()));
		memberBean.setGender(gender);
		memberBean.setAddr(county + city + addr);
		service.updateMember(memberBean);
		return "redirect:/home";
	}

//變更密碼控制器
	@RequestMapping(value = "/member/change", method = RequestMethod.GET)
	public String getChangeMemberForm(Model model) {
		return "account/changeMemberPassword";
	}

	@RequestMapping(value = "/member/change", method = RequestMethod.POST)
	public String processChangeMemberForm(@RequestParam("oldPW") String oldPW, @RequestParam("newPW") String newPW,
			@RequestParam("renewPW") String renewPW, HttpServletRequest request) {
		HttpSession session = request.getSession();
		MemberBean mb = (MemberBean) session.getAttribute("memberLoginOK");
		service.changePassword(service.checkIdPassword(mb.getAccount(), oldPW), newPW);
		return "redirect:/members";
	}

	// 變更密碼控制器測試
	@RequestMapping(value = "/member/changetest", method = RequestMethod.GET)
	public String getChangeMemberFormTest(Model model) {
		MemberBean mb = new MemberBean();
		model.addAttribute("MemberBean", mb);
		return "account/changeMemberPasswordTest";
	}

	@RequestMapping(value = "/member/changetest", method = RequestMethod.POST)
	public String processChangeMemberFormTest(@ModelAttribute("MemberBean") MemberBean mb,
			@RequestParam("newPW") String newPW, BindingResult result, HttpServletRequest request) {
		service.changePassword(service.checkIdPassword(mb.getAccount(), mb.getPassword()), newPW);
		return "redirect:/members";
	}

	// 刪除會員控制器
	@RequestMapping(value = "/member/delete", method = RequestMethod.GET)
	public String deleteMember(Model model) {
		MemberBean mb = new MemberBean();
		model.addAttribute("MemberBean", mb);
		return "account/deleteMember";
	}

	@RequestMapping(value = "/member/delete", method = RequestMethod.POST)
	public String deleteMember(@ModelAttribute("MemberBean") MemberBean mb, BindingResult result,
			HttpServletRequest request) {
		service.deleteMember(mb);
		return "redirect:/members";
	}

	// 登出控制器
	@RequestMapping("/memberLogout")
	public String manageLogout(Model model) {
		System.out.println("Mout");
		return "member/memberLogout";
	}

	@RequestMapping(value = "addCreditCard", method = RequestMethod.GET)
	public String addCreditCard(Model model) {
		CreditCardBean cb = new CreditCardBean();
		model.addAttribute("CreditCardBean", cb);
		return "addCreditCard";
	}

	@RequestMapping(value = "addCreditCard", method = RequestMethod.POST)
	public String addCreditCard(@ModelAttribute("CreditCardBean") CreditCardBean cb, BindingResult result,
			HttpServletRequest request, @RequestParam("date") @DateTimeFormat(pattern = "yyyy/MM/dd") Date date) {

		System.out.println("DATE:" + date);
		cb.setVdate(new java.sql.Timestamp(date.getTime()));
		System.out.println("cb:" + cb.toString());
		HttpSession session = request.getSession();
		MemberBean mb = (MemberBean) session.getAttribute("memberLoginOK");
		cb.setmId(mb.getmId());
		service.addCreditCard(cb);
		return "redirect:/home";
	}

	@RequestMapping("/CreditCardList")
	public String getCreditCardsBymId(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		MemberBean mb = (MemberBean) session.getAttribute("memberLoginOK");
		if (mb != null) {
			List<CreditCardBean> list = service.getCreditCardsBymId(mb.getmId());
			model.addAttribute("creditCard", list);
		}

		return "creditCards";
	}

	@RequestMapping("/CreditCard")
	public String getCreditCardBycId(@RequestParam("cId") Integer cId, Model model, HttpServletRequest request) {
		model.addAttribute("card", service.getCreditCardBycId(cId));
		return "creditCard";
	}

//	@RequestMapping(value = "/membertest", method = RequestMethod.GET)
//	public String addCreditCard1(Model model) {
//		CreditCardBean cb = new CreditCardBean();
//		model.addAttribute("CreditCardBean", cb);
//		return "membertest";
//	}

	@RequestMapping(value = "/memberchange", method = RequestMethod.GET)
	public String Changemamber1(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		CreditCardBean cb = new CreditCardBean();
		MemberBean member = (MemberBean) session.getAttribute("memberLoginOK");
		SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("mid:" + member.getmId());
		Blob blob = null;
		byte[] imageData = null;

		if (member.getBirthday() != null) {
			System.out.println(member.getBirthday());
			String bd = x.format(member.getBirthday());
			model.addAttribute("Birthday", bd);
			System.out.println(bd);
		}

		if (member != null && member.getMemberImage() != null) {
			System.out.println("both true");
			if (member.getMemberImage() != null) {
				blob = member.getMemberImage();
				try {
					imageData = blob.getBytes(1, (int) blob.length());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session.setAttribute("memberImage", Base64.getEncoder().encodeToString(imageData));
			}

		}

		model.addAttribute("memberBean", member);
		System.out.println("member Id" + member.getmId());
		model.addAttribute("CreditCardBean", cb);
		return "member/memberchange";
	}

	@RequestMapping(value = "/memberchange", method = RequestMethod.POST)

	public String Changemamber1(@ModelAttribute("CreditCardBean") CreditCardBean cb,
			@ModelAttribute("memberBean") MemberBean mb, BindingResult result, HttpServletRequest request,
			@RequestParam("form") String form, @RequestParam("oldPW") String oldPW, @RequestParam("newPW") String newPW,
			@RequestParam("renewPW") String renewPW, @RequestParam("county") String county,
			@RequestParam("city") String city, @RequestParam("addr") String addr, @RequestParam("gender") String gender,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy/MM/dd") Date date,
			@RequestParam("cnumber1") String cnumber1, @RequestParam("cnumber2") String cnumber2,
			@RequestParam("cnumber3") String cnumber3, @RequestParam("cnumber4") String cnumber4) {
		HttpSession session = request.getSession();
		MemberBean memberbean = (MemberBean) session.getAttribute("memberLoginOK");
		if (form.equals("1")) {
			MultipartFile file = mb.getFile();
			mb.setmId(memberbean.getmId());
			mb.setBirthday(new java.sql.Timestamp(date.getTime()));
			mb.setGender(gender);
			mb.setAddr(county + city + addr);
			if (!file.isEmpty()) {
				try {
					mb.setMemberImage(SystemUtils2019.fileToBlob(file.getInputStream(), file.getSize()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			service.updateMember(mb);
			session.setAttribute("memberLoginOK", mb);
		}
		// 修改密碼
		else if (form.equals("2")) {
			service.changePassword(service.checkIdPassword(memberbean.getAccount(), oldPW), newPW);
		}
		// 新增會員信用卡
		else if (form.equals("3")) {
			cb.setVdate(new java.sql.Timestamp(date.getTime()));
			System.out.println("form :" + form);
			MemberBean db = (MemberBean) session.getAttribute("memberLoginOK");
			cb.setCnumber(cnumber1 + cnumber2 + cnumber3 + cnumber4);
			cb.setmId(db.getmId());
			service.addCreditCard(cb);
		}
		return "redirect:/home";
	}

	// 上傳會員圖片測試
	@RequestMapping(value = "/uploadImage", method = RequestMethod.GET)
	public String addImage(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession();
		MemberBean member = (MemberBean) session.getAttribute("memberLoginOK");
		MemberBean mb = null;
		if (member != null && member.getMemberImage() != null) {
			System.out.println("both true");
			mb = service.getMemberBymId(member.getmId());
			try {
				session.setAttribute("memberImage", SystemUtils2019.Blob2Base64String(mb.getMemberImage()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mb = new MemberBean();
		}
		model.addAttribute("memberBean", mb);
		System.out.println("HelloWorld");
		return "account/uploadImage";

	}

	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	public String addImage(@ModelAttribute("memberBean") MemberBean mb, BindingResult result,
			HttpServletRequest request) {

		MultipartFile file = mb.getFile();
		HttpSession session = request.getSession();
		MemberBean member = (MemberBean) session.getAttribute("memberLoginOK");

		if (!file.isEmpty()) {
			try {
				member.setMemberImage(SystemUtils2019.fileToBlob(file.getInputStream(), file.getSize()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		session.setAttribute("memberLoginOK", member);
		service.updateMember(member);
		return "redirect:/uploadImage";
	}
}
