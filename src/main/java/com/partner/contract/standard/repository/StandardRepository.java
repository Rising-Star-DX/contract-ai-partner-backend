package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {
    @Query("select s from Standard s join fetch s.category c where s.aiStatus is not null and s.name like %:name% order by s.createdAt desc")
    List<Standard> findWithCategoryByNameContainingOrderByCreatedAtDesc(@Param("name") String name);

    @Query("select s from Standard s join fetch s.category c where s.aiStatus is not null and s.name like %:name% and s.category.id = :categoryId order by s.createdAt desc")
    List<Standard> findStandardListOrderByCreatedAtDesc(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Query("select s from Standard s join fetch s.category c where s.id = :id")
    Optional<Standard> findWithCategoryById(@Param("id") Long id);


}
