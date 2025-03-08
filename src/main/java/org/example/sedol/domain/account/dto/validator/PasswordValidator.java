package org.example.sedol.domain.account.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		return true;
	}
}
