package org.terasoluna.securelogin.app.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DomainRestrictedEmailValidator implements
		ConstraintValidator<DomainRestrictedEmail, String> {

	private String[] allowedDomains;
	
	private boolean allowSubDomain; 
	
	@Override
	public void initialize(DomainRestrictedEmail constraintAnnotation) {
		allowedDomains = constraintAnnotation.allowedDomains();
		allowSubDomain = constraintAnnotation.allowSubDomain();
	}

	@Override
	public boolean isValid(String value,
			ConstraintValidatorContext context) {
		for(String domain : allowedDomains){
			if (value.endsWith("@"+domain) || 
					(allowSubDomain && value.endsWith("."+domain))){
				return true;
			}
		}
		return false;
	}

}
