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

    @Query(value = "SELECT " +
            "    c.id as id, " +
            "    c.name as name, " +
            "    COALESCE(CAST(s.count AS BIGINT), 0) AS count_of_standards, " +
            "    COALESCE(CAST(a.count AS BIGINT), 0) AS count_of_agreements, " +
            "    c.created_at as created_at " +
            "FROM category c " +
            "LEFT JOIN ( " +
            "    SELECT category_id, COUNT(*) AS count " +
            "    FROM standard " +
            "    GROUP BY category_id " +
            ") s ON c.id = s.category_id " +
            "LEFT JOIN ( " +
            "    SELECT category_id, COUNT(*) AS count " +
            "    FROM agreement " +
            "    GROUP BY category_id " +
            ") a ON c.id = a.category_id " +
            "WHERE c.name LIKE CONCAT('%', :name, '%') " +
            "ORDER BY c.name", nativeQuery = true)
    List<CategoryListResponseDto> findCategoryListOrderByName(@Param("name") String name);

    @Query("select c from Category c order by c.name")
    List<Category> findCategoryNameListOrderByName();

    @Query("select count(s.id) from Category c join c.standardList s where c.id=:id")
    Long findWithStandardById(@Param("id") Long id);

    Category findByName(String name);
}
