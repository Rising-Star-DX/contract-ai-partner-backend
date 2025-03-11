package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {
}
