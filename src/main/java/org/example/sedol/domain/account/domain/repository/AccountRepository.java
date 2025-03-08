package org.example.sedol.domain.account.domain.repository;

import java.util.Optional;

import org.example.sedol.domain.account.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	boolean existsByUserId(String userId);

	Optional<Account> findByUserId(String userId);
}
