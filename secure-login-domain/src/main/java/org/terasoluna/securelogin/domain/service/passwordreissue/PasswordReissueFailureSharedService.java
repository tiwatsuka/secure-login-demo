package org.terasoluna.securelogin.domain.service.passwordreissue;

public interface PasswordReissueFailureSharedService {

	public void resetFailure(String username, String token);

}
