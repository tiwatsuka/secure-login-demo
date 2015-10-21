package org.terasoluna.securelogin.app.passwordreissue;

import org.terasoluna.securelogin.app.common.validation.NotReused;
import org.terasoluna.securelogin.app.common.validation.StrongPassword;
import org.terasoluna.securelogin.app.common.validation.Confirm;

import lombok.Data;

@Data
@Confirm(propertyName = "newPassword")
@StrongPassword(idPropertyName = "username", newPasswordPropertyName = "newPassword")
@NotReused(idPropertyName = "username", newPasswordPropertyName = "newPassword")
public class PasswordResetForm {

	private String username;

	private String token;

	private String secret;

	private String newPassword;

	private String confirmNewPassword;
}
