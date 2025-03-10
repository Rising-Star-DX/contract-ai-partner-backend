package com.partner.contract.category.domain;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.standard.domain.Standard;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Standard> standardList;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Agreement> agreementList;

    @Builder
    public Category(String name, LocalDateTime createdAt, LocalDateTime updatedAt, List<Standard> standardList, List<Agreement> agreementList) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.standardList = standardList;
        this.agreementList = agreementList;
    }
}
