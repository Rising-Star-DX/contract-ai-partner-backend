package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    List<Agreement> findAllByOrderByCreatedAtDesc();
}
