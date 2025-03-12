package com.partner.contract.standard.domain;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileType;
import com.partner.contract.category.domain.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
public class Standard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private FileType type;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private AiStatus aiStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<MemberStandard> memberStandardList;

    @Builder
    public Standard(String name, FileType type, String url, AiStatus aiStatus, LocalDateTime createdAt, Category category, List<MemberStandard> memberStandardList) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
        this.category = category;
        this.memberStandardList = memberStandardList;
    }
}
