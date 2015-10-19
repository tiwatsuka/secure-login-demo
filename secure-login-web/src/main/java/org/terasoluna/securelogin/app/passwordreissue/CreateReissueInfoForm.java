package org.terasoluna.securelogin.app.passwordreissue;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class CreateReissueInfoForm {
	@NotEmpty
	String username;
}
