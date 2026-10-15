package com.kinormotors.repository;

import com.kinormotors.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    List<Modelo> findByActivoTrueOrderByOrdenAsc();

    List<Modelo> findAllByOrderByOrdenAsc();

    Optional<Modelo> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
