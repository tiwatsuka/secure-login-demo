package org.terasoluna.securelogin.app.passwordreissue;

import org.terasoluna.securelogin.app.common.validation.ProhibitReuse;
import org.terasoluna.securelogin.app.common.validation.StrongPassword;
import org.terasoluna.securelogin.app.common.validation.Confirm;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@StrongPassword(idField = "username", newPasswordField = "newPassword")
@ProhibitReuse(idField = "username", newPasswordField = "newPassword")
public class PasswordResetForm {

	private String username;

	private String token;

	private String secret;

	private String newPassword;

	private String confirmNewPassword;
}
