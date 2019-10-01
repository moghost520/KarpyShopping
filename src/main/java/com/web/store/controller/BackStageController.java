package com.web.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.web.store.model.ManagerBean;
import com.web.store.service.HotSearchService;
import com.web.store.service.ManagerService;
import com.web.store.service.MemberService;
import com.web.store.service.OrderService;
import com.web.store.service.ProductService;

@Controller
public class BackStageController {
	@Autowired
	MemberService mservice;
	@Autowired
	ManagerService adminservice;
	@Autowired
	ProductService pservice;
	@Autowired
	OrderService oservice;
	@Autowired
	HotSearchService hservice;

	// dashboard
	@RequestMapping("/admin")
	public String adminPage(Model model, HttpSession session) {
		Integer memberCount = mservice.getAllMember().size();
//		Integer orderCount  =  oservice.select().size();
		Integer productCount = pservice.getAllProducts().size();
		Integer adminCount = adminservice.getAllManager().size();

		session.setAttribute("memberCount", memberCount);
//		session.setAttribute("orderCount", orderCount);
		session.setAttribute("productCount", productCount);
		session.setAttribute("adminCount", adminCount);

		return "backstage/dashboard";
	}

	// products
	@RequestMapping("/adminProducts")
	public String productsPage(Model model, HttpSession session) {
		return "backstage/products";
	}

	// orders
	@RequestMapping("/adminOrders")
	public String ordersPage(Model model, HttpSession session) {
		return "backstage/orders";
	}

	// members
	@RequestMapping("/adminMembers")
	public String membersPage(Model model, HttpSession session) {
		return "backstage/members";
	}

	// managers
	@RequestMapping("/adminManagers")
	public String managersPage(Model model, HttpSession session) {
		return "backstage/managers";
	}

	// vendors
	@RequestMapping("/adminVendors")
	public String vendorsPage(Model model, HttpSession session) {
		return "backstage/vendors";
	}

	// login
	@RequestMapping(value = "/adminlogin", method = RequestMethod.GET)
	public String login(Model model) {
		ManagerBean mb = new ManagerBean();
		model.addAttribute("managerBean", mb);
		return "backstage/login";
	}

	@RequestMapping(value = "/adminlogin", method = RequestMethod.POST)
	public String processlogin(@ModelAttribute("managerBean") ManagerBean mb, Model model, HttpSession session) {

		ManagerBean manager = null;
		System.out.println("admin post test");
		manager = adminservice.checkIdPassword(mb.getAccount(), mb.getPassword());
		if (manager == null) {
			session.setAttribute("errorMsg", "帳號或密碼錯誤");
			return "redirect:/adminlogin";
		}
		session.setAttribute("LoginOK", manager);
		System.out.println(manager.toString());
		return "redirect:/admin";
	}

}
