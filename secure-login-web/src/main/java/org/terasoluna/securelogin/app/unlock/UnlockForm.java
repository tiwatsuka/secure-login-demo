package org.terasoluna.securelogin.app.unlock;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class UnlockForm {
	@NotEmpty
	private String username;
}
