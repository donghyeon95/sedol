package org.example.sedol.domain.vod.domain.repository;

import org.example.sedol.domain.vod.domain.entity.Vod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VodRepository extends JpaRepository<Vod, Long> {
}
