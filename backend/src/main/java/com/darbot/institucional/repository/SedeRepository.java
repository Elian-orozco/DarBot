package com.darbot.institucional.repository;

import com.darbot.institucional.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByActivaTrue();
}