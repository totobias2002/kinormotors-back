package com.kinormotors.repository;

import com.kinormotors.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    @Query("select l.tipoAccion, count(l) from Lead l where l.creadoEn >= :desde and l.creadoEn < :hasta group by l.tipoAccion")
    List<Object[]> contarPorTipo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select coalesce(l.modeloSlug, '(sin modelo)'), count(l) from Lead l where l.creadoEn >= :desde and l.creadoEn < :hasta group by l.modeloSlug order by count(l) desc")
    List<Object[]> contarPorModelo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select coalesce(l.utmSource, '(directo)'), count(l) from Lead l where l.creadoEn >= :desde and l.creadoEn < :hasta group by l.utmSource order by count(l) desc")
    List<Object[]> contarPorOrigen(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select coalesce(l.seccion, '(sin seccion)'), count(l) from Lead l where l.creadoEn >= :desde and l.creadoEn < :hasta group by l.seccion order by count(l) desc")
    List<Object[]> contarPorSeccion(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
