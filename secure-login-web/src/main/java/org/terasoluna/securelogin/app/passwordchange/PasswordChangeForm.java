package org.terasoluna.securelogin.app.passwordchange;

import org.terasoluna.securelogin.app.common.validation.Confirm;
import org.terasoluna.securelogin.app.common.validation.ProhibitReuse;
import org.terasoluna.securelogin.app.common.validation.StrongPassword;
import org.terasoluna.securelogin.app.common.validation.ConfirmOldPassword;

import lombok.Data;

@Data
@Confirm(field = "newPassword")
@StrongPassword(idField = "username", newPasswordField = "newPassword")
@ProhibitReuse(idField = "username", newPasswordField = "newPassword")
@ConfirmOldPassword(idField = "username", oldPasswordField = "oldPassword")
public class PasswordChangeForm {

	private String username;

	private String oldPassword;

	private String newPassword;

	private String confirmNewPassword;

}
