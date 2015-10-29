package org.terasoluna.securelogin.app.welcome;

import java.security.Principal;
import java.util.Locale;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.terasoluna.securelogin.domain.model.Account;
import org.terasoluna.securelogin.domain.service.account.AccountSharedService;
import org.terasoluna.securelogin.domain.service.userdetails.LoggedInUser;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Inject
	AccountSharedService accountSharedService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String home(Principal principal, Locale locale, Model model) {

		Authentication authentication = (Authentication) principal;
		LoggedInUser userDetails = (LoggedInUser) authentication.getPrincipal();
		Account account = userDetails.getAccount();

		DateTime lastLoginDate = userDetails.getLastLoginDate();

		model.addAttribute("account", account);
		model.addAttribute("isPasswordExpired", accountSharedService
				.isCurrentPasswordExpired(account.getUsername()));
		model.addAttribute("lastLoginDate", lastLoginDate);

		return "welcome/home";

	}

}
