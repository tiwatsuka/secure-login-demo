package org.terasoluna.securelogin.app.common.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Email;

@Documented
@Constraint(validatedBy = {DomainRestrictedEmailValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Email
public @interface DomainRestrictedEmail {
	String message() default "{org.terasoluna.securelogin.app.common.validation.DomainRestrictedEmail.message}";

	Class<?>[] groups() default {};

	String[] allowedDomains() default {};
	
	boolean allowSubDomain() default false;

	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		DomainRestrictedEmail[] value();
	}

	Class<? extends Payload>[] payload() default {};
}
