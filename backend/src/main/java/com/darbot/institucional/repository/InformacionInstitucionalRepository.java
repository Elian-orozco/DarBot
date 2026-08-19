package com.darbot.institucional.repository;

import com.darbot.institucional.entity.InformacionInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InformacionInstitucionalRepository extends JpaRepository<InformacionInstitucional, Long> {
}