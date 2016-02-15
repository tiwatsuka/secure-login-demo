package org.terasoluna.securelogin.app.passwordreissue;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;

import org.terasoluna.securelogin.domain.service.passwordreissue.PasswordReissueService;

@Controller
@RequestMapping("/reissue")
public class PasswordReissueController {

	@Inject
	PasswordReissueService passwordReissueService;

	@RequestMapping(value = "create", params = "form")
	public String showCreateReissueInfoForm(CreateReissueInfoForm form) {
		return "passwordreissue/createReissueInfoForm";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String createReissueInfo(@Validated CreateReissueInfoForm form,
			BindingResult bindingResult, Model model,
			RedirectAttributes attributes) {
		if (bindingResult.hasErrors()) {
			return showCreateReissueInfoForm(form);
		}

		try {
			String rawSecret = passwordReissueService.createAndSendReissueInfo(form.getUsername());
			attributes.addFlashAttribute("secret", rawSecret);
			return "redirect:/reissue/create?complete";
		} catch (ResourceNotFoundException e) {
			model.addAttribute(e.getResultMessages());
			return showCreateReissueInfoForm(form);
		}
	}

	@RequestMapping(value = "create", params = "complete", method = RequestMethod.GET)
	public String createReissueInfoComplete() {
		return "passwordreissue/createReissueInfoComplete";
	}

	@RequestMapping(value = "resetpassword", params = "form")
	public String showPasswordResetForm(PasswordResetForm form, Model model,
			@RequestParam("username") String username,
			@RequestParam("token") String token) {

		passwordReissueService.findOne(username, token); // existence check

		form.setUsername(username);
		form.setToken(token);
		model.addAttribute("passwordResetForm", form);
		return "passwordreissue/passwordResetForm";
	}

	@RequestMapping(value = "resetpassword", method = RequestMethod.POST)
	public String resetPassword(@Validated PasswordResetForm form,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return showPasswordResetForm(form, model, form.getUsername(),
					form.getToken());
		}

		try {
			passwordReissueService.resetPassword(form.getUsername(),
					form.getToken(), form.getSecret(), form.getNewPassword());
			return "redirect:/reissue/resetpassword?complete";
		} catch (BusinessException e) {
			model.addAttribute(e.getResultMessages());
			return showPasswordResetForm(form, model, form.getUsername(),
					form.getToken());
		}
	}

	@RequestMapping(value = "resetpassword", params = "complete", method = RequestMethod.GET)
	public String resetPasswordComplete() {
		return "passwordreissue/passwordResetComplete";
	}

	@ModelAttribute("createReissueInfoForm")
	public CreateReissueInfoForm setupReissueForm() {
		return new CreateReissueInfoForm();
	}

	@ModelAttribute("passwordResetForm")
	public PasswordResetForm setupResetForm() {
		return new PasswordResetForm();
	}
}
