package org.terasoluna.securelogin.app.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.terasoluna.gfw.common.codepoints.CodePoints;
import org.terasoluna.gfw.common.codepoints.catalog.ASCIIControlChars;
import org.terasoluna.gfw.common.codepoints.catalog.CRLF;

public class NotContainControlCharsValidator implements
		ConstraintValidator<NotContainControlChars, String> {

	private CodePoints codePoints;

	@Override
	public void initialize(NotContainControlChars constraintAnnotation) {
		this.codePoints = CodePoints.of(ASCIIControlChars.class);
		if (constraintAnnotation.allowCRLF()) {
			CodePoints crlf = new CRLF();
			this.codePoints = this.codePoints.subtract(crlf);
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		int len = value.length();
		for (int i = 0; i < len; i++) {
			if (codePoints.firstExcludedCodePoint(value.substring(i, i + 1)) == CodePoints.NOT_FOUND) {
				return false;
			}
		}

		return true;
	}

}
