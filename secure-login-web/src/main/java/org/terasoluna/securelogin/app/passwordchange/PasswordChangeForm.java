package org.terasoluna.securelogin.app.passwordchange;

import org.terasoluna.securelogin.app.common.validation.Confirm;
import org.terasoluna.securelogin.app.common.validation.NotReused;
import org.terasoluna.securelogin.app.common.validation.StrongPassword;
import org.terasoluna.securelogin.app.common.validation.ConfirmOldPassword;

import lombok.Data;

@Data
@Confirm(propertyName = "newPassword")
@StrongPassword(idPropertyName = "username", newPasswordPropertyName = "newPassword")
@NotReused(idPropertyName = "username", newPasswordPropertyName = "newPassword")
@ConfirmOldPassword(idPropertyName = "username", oldPasswordPropertyName = "oldPassword")
public class PasswordChangeForm {

	private String username;

	private String oldPassword;

	private String newPassword;

	private String confirmNewPassword;

}
