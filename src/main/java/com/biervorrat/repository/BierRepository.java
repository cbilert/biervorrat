package com.biervorrat.repository;


import com.biervorrat.entity.Bier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BierRepository extends JpaRepository<Bier, Long> {

    Optional<Bier> findByName(String name);
}
