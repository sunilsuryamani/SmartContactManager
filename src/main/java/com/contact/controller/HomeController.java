package com.contact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.dao.UserRepository;
import com.contact.helper.Messages;
import com.contact.model.User;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String Homepage(Model model) {
		model.addAttribute("title", "Home -Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about-us")
	public String about(Model model) {
		model.addAttribute("title", "About -Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/singup")
	public String singup(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("title", "Sing-Up -Smart Contact Manager");
		return "singup";
	}

	@RequestMapping(value = "//do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResut,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed with terms and condition");
				throw new Exception("You have not agreed with terms and condition");
			}
			if (bindingResut.hasErrors()) {

				System.out.println("Error " + bindingResut.toString());
				model.addAttribute("user", user);
				return "singup";
			}
			System.out.println(agreement);
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("Default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User result = this.userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Messages("Successfully register !!", "alert-success"));
			return "singup";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Messages("something went wrong !!" + e.getMessage(), "alert-danger"));
			return "singup";

		}
//		return "singup";

	}

	// login custom handler
	@GetMapping("/singin")
	public String customLogin(Model model) {
		
		
		model.addAttribute("title","Sing In Smart contact manager");
		return "login";
	}

}
