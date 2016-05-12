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

import org.hibernate.validator.constraints.URL;

@Documented
@Constraint(validatedBy = {DomainRestrictedURLValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@URL
public @interface DomainRestrictedURL {

	String message() default "{org.terasoluna.securelogin.app.common.validation.DomainRestrictedURL.message}";

	Class<?>[] groups() default {};

	String[] allowedDomains() default {};
	
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		DomainRestrictedURL[] value();
	}

	Class<? extends Payload>[] payload() default {};
	
}
