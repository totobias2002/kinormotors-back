package com.kinormotors.repository;

import com.kinormotors.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByActivaTrueOrderByOrdenAsc();

    List<Promocion> findAllByOrderByOrdenAsc();
}
