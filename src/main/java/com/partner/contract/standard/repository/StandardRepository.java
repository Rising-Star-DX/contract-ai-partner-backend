package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {
    List<Standard> findAllByOrderByCreatedAtDesc();
    List<Standard> findByNameContaining(String name);
    List<Standard> findByCategoryId(Long categoryId);
}
