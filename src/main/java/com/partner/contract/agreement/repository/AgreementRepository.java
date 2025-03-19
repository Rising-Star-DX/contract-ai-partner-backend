package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    @Query("select a from Agreement a join fetch a.category c order by a.createdAt desc")
    List<Agreement> findAllWithCategoryByOrderByCreatedAtDesc();
    @Query("select a from Agreement a join fetch a.category c where a.name like %:name%")
    List<Agreement> findWithCategoryByNameContaining(@Param("name") String name);
    @Query("select a from Agreement a join fetch a.category c where a.category.id = :categoryId")
    List<Agreement> findWithCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
