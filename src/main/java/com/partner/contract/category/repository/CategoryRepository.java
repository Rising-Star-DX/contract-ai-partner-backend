package com.partner.contract.category.repository;

import com.partner.contract.category.domain.Category;
import com.partner.contract.category.dto.CategoryListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select new com.partner.contract.category.dto.CategoryListResponseDto(c.id, c.name, count(distinct s.id), count(distinct a.id), c.createdAt) " +
            "from Category c " +
            "left join c.standardList s " +
            "left join c.agreementList a " +
            "where c.name like %:name% " +
            "group by c.id, c.name, c.createdAt " +
            "order by c.name")
    List<CategoryListResponseDto> findCategoryListOrderByName(@Param("name") String name);

    @Query("select count(s.id) from Category c join c.standardList s where c.id=:id")
    Long findWithStandardById(@Param("id") Long id);

    Category findByName(String name);
}
