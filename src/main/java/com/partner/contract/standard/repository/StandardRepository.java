package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {
    @Query("select s from Standard s join s.category c order by s.createdAt desc")
    List<Standard> findAllWithCategoryByOrderByCreatedAtDesc();
    @Query("select s from Standard s join s.category c where s.name like %:name%")
    List<Standard> findWithCategoryByNameContaining(@Param("name") String name);
    @Query("select s from Standard s join s.category c where s.category.id = :categoryId")
    List<Standard> findWithCategoryByCategoryId(@Param("categoryId") Long categoryId);
}
