package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.IncorrectTextAnalysisReportResponseDto;
import com.partner.contract.agreement.dto.IncorrectTextResponseDto;
import com.partner.contract.common.enums.AiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long>, AgreementRepositoryCustom {
    @Query("select a from Agreement a join fetch a.category c where a.aiStatus is not null and a.name like %:name% order by a.createdAt desc")
    List<Agreement> findWithCategoryByNameContainingOrderByCreatedAtDesc(@Param("name") String name);

    @Query("select a from Agreement a join fetch a.category c where a.aiStatus is not null and a.name like %:name% and a.category.id = :categoryId order by a.createdAt desc")
    List<Agreement> findAgreementListOrderByCreatedAtDesc(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Query(value = """
        SELECT 
            ait.id AS id,
            aip.page AS page,
            ait.accuracy AS accuracy,
            ait.incorrect_text AS incorrectText,
            ait.proof_text AS proofText,
            ait.corrected_text AS correctedText,
            aip.position AS position
        FROM (
            SELECT * FROM agreement_incorrect_text 
            WHERE agreement_id = :agreementId
        ) ait
        JOIN agreement_incorrect_position aip 
            ON ait.id = aip.agreement_incorrect_text_id
        ORDER BY aip.page, aip.order_index
    """, nativeQuery = true)
    List<IncorrectTextResponseDto> findIncorrectTextByAgreementId(@Param("agreementId") Long agreementId);

    @Query(value = """
        SELECT 
            ait.id AS id,
            aip.page AS page,
            ait.accuracy AS accuracy,
            ait.incorrect_text AS incorrectText,
            ait.proof_text AS proofText,
            ait.corrected_text AS correctedText
        FROM (
            SELECT * FROM agreement_incorrect_text 
            WHERE agreement_id = :agreementId
        ) ait
        JOIN agreement_incorrect_position aip 
            ON ait.id = aip.agreement_incorrect_text_id
        ORDER BY aip.page, aip.order_index
    """, nativeQuery = true)
    List<IncorrectTextAnalysisReportResponseDto> findIncorrectTextAnalysisReportByAgreementId(@Param("agreementId") Long agreementId);

    List<Agreement> findByAiStatusAndCreatedAtBefore(AiStatus aiStatus, LocalDateTime fiveMinutesAgo);
}
