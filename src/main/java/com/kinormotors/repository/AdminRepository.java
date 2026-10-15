package com.kinormotors.repository;

import com.kinormotors.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmailIgnoreCase(String email);
}
