package org.terasoluna.securelogin.domain.common.scheduled;

import javax.inject.Inject;

import org.terasoluna.gfw.common.date.jodatime.JodaTimeDateFactory;

import org.terasoluna.securelogin.domain.service.passwordreissue.PasswordReissueService;

public class UnnecessaryReissueInfoCleaner {

	@Inject
	PasswordReissueService passwordReissueService;

	@Inject
	JodaTimeDateFactory dateFactory;

	public void cleanup() {
		passwordReissueService.removeExpired(dateFactory.newDateTime());
	}

}
