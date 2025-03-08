package org.example.sedol.domain.account.dto.validator;

import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueUserIdValidator implements ConstraintValidator<UniqueUserId, String> {

	private final AccountRepository accountRepository;

	public UniqueUserIdValidator(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public boolean isValid(String userId, ConstraintValidatorContext constraintValidatorContext) {
		if (userId == null || userId.isBlank()) {
			return true;
		}

		return !accountRepository.existsByUserId(userId);
	}
}
