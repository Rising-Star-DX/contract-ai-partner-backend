package com.partner.contract.agreement.domain;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileType;
import com.partner.contract.category.domain.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType type;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AiStatus aiStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String summaryContent;

    @Column(nullable = false)
    private Integer totalPage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL)
    private List<AgreementIncorrectText> agreementIncorrectTextList;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL)
    private List<MemberAgreement> memberAgreementList;

    @Builder
    public Agreement(String name, FileType type, String url, AiStatus aiStatus, LocalDateTime createdAt, String summaryContent, Integer totalPage, Category category, List<AgreementIncorrectText> agreementIncorrectTextList, List<MemberAgreement> memberAgreementList) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
        this.summaryContent = summaryContent;
        this.totalPage = totalPage;
        this.category = category;
        this.agreementIncorrectTextList = agreementIncorrectTextList;
        this.memberAgreementList = memberAgreementList;
    }
}
