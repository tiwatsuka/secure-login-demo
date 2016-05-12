package org.terasoluna.securelogin.app.account;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;
import org.terasoluna.gfw.common.validator.constraints.Compare;
import org.terasoluna.gfw.common.validator.constraints.Compare.Operator;
import org.terasoluna.securelogin.app.common.validation.DomainRestrictedEmail;
import org.terasoluna.securelogin.app.common.validation.DomainRestrictedURL;
import org.terasoluna.securelogin.app.common.validation.FileExtension;
import org.terasoluna.securelogin.app.common.validation.NotContainControlChars;
import org.terasoluna.securelogin.app.common.validation.UploadFileMaxSize;
import org.terasoluna.securelogin.app.common.validation.UploadFileNotEmpty;
import org.terasoluna.securelogin.app.common.validation.UploadFileRequired;

import lombok.Data;

@Data
@Compare(left="email", right="confirmEmail", operator=Operator.EQUAL, requireBoth=true, node=Compare.Node.ROOT_BEAN)
public class AccountCreateForm implements Serializable {

	public static interface Confirm{};
	
	public static interface CreateAccount{};
	
	private static final long serialVersionUID = 1L;

	@NotNull
	@NotContainControlChars
	@Size(min=4, max=128)
	private String username;
	
	@NotNull
	@Size(min=1, max=128)
	private String firstName;
	
	@NotNull
	@Size(min=1, max=128)
	private String lastName;
	
	@NotNull
	@Size(min=1, max=128)
	@DomainRestrictedEmail(allowedDomains={"ntt.co.jp", "nttdata.co.jp"}, allowSubDomain=true)
	private String email;
	
	@NotNull
	@Size(min=1, max=128)
	@DomainRestrictedEmail(allowedDomains={"ntt.co.jp", "nttdata.co.jp"}, allowSubDomain=true)
	private String confirmEmail;
	
	@NotNull
	@DomainRestrictedURL(allowedDomains={"jp"})
	private String url;

	@UploadFileRequired.List(@UploadFileRequired(groups=Confirm.class))
	@UploadFileNotEmpty.List(@UploadFileNotEmpty(groups=Confirm.class))
	@UploadFileMaxSize
	@FileExtension(extensions={"jpg", "png", "gif"})
	private MultipartFile image;
	
	@NotNull.List(@NotNull(groups=CreateAccount.class))
	@Size(max=40)
	private String imageId;
	
}
