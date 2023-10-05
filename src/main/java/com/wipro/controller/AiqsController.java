package com.wipro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.wipro.repository.InsuranceProviderRepository;
import com.wipro.repository.UserProfileRepository;

@RestController
public class AiqsController {
	
	@Autowired
	private UserProfileRepository userRepo;
	
	@Autowired
	private InsuranceProviderRepository insuranceRepo;

	@GetMapping("home")
	public ModelAndView home(Model model) {
		return new ModelAndView("userhome");
	}
	
//	@GetMapping("/admin/quoteManagement")
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@Secured("ROLE_ADMIN")
//	@RolesAllowed("ROLE_ADMIN")
	@GetMapping("/userFolder/showUserProfiles")
	public ModelAndView getUserProfiles() {
		ModelAndView mv = new ModelAndView("userFolder/show_user_profile");
		mv.addObject("userProfiles", userRepo.findAll());
		return mv;
	}
	
	@GetMapping("/public/insuranceProviders")
	public ModelAndView getInsuranceProviders() {
		ModelAndView mv = new ModelAndView("insurance_providers");
		mv.addObject("insuranceProvidersDetails", insuranceRepo.findAll());
		return mv;
	}
	
	@GetMapping("/public/userFolder/registration")
	public ModelAndView addUserProfile() {
		return new ModelAndView("userFolder/registration");
	}
	
	@GetMapping({"hii", "welcome"})
	public ModelAndView welcome() {
		return new ModelAndView("welcome");
	}
}
