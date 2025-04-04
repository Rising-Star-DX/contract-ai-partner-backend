package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.IncorrectTextResponseDto;
import com.partner.contract.common.enums.AiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    @Query("select a from Agreement a join fetch a.category c where a.aiStatus is not null and a.name like %:name% order by a.createdAt desc")
    List<Agreement> findWithCategoryByNameContainingOrderByCreatedAtDesc(@Param("name") String name);

    @Query("select a from Agreement a join fetch a.category c where a.aiStatus is not null and a.name like %:name% and a.category.id = :categoryId order by a.createdAt desc")
    List<Agreement> findAgreementListOrderByCreatedAtDesc(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Query("""
        select new com.partner.contract.agreement.dto.IncorrectTextResponseDto(
            ait.id, aip.page, ait.accuracy, ait.incorrectText, ait.proofText, ait.correctedText, aip.position
        )
        from AgreementIncorrectText ait
        join AgreementIncorrectPosition aip
        on ait.id = aip.agreementIncorrectText.id
        where ait.agreement.id = :agreementId
        order by aip.page, aip.orderIndex
    """)
    List<IncorrectTextResponseDto> findIncorrectTextByAgreementId(@Param("agreementId") Long agreementId);

    List<Agreement> findByAiStatusAndCreatedAtBefore(AiStatus aiStatus, LocalDateTime fiveMinutesAgo);
}
