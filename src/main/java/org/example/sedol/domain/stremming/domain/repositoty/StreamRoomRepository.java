package org.example.sedol.domain.stremming.domain.repositoty;

import java.util.Optional;

import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamRoomRepository extends JpaRepository<StreamRoom, Long> {
	Optional<StreamRoom> findByAccount_UserId(String userId);
	Optional<StreamRoom> findByStreamKey(String streamKey);
}