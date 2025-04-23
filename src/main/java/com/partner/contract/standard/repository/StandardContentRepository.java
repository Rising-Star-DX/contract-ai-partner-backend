package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.StandardContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandardContentRepository extends JpaRepository<StandardContent, Long> {
    Boolean existsByStandardId(Long id);
}
