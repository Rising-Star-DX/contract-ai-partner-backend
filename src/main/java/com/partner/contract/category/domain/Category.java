package com.partner.contract.category.domain;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.standard.domain.Standard;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
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

    public void update(String name) {
        this.name = name;
    }
}
