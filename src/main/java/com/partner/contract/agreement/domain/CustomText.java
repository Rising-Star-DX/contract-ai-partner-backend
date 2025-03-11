package com.partner.contract.agreement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
public class CustomText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @OneToMany(mappedBy = "customText", cascade = CascadeType.ALL)
    private List<AgreementIncorrectText> agreementIncorrectTextList;

    @Builder
    public CustomText(String content, LocalDateTime createdAt, Agreement agreement, List<AgreementIncorrectText> agreementIncorrectTextList) {
        this.content = content;
        this.createdAt = createdAt;
        this.agreement = agreement;
        this.agreementIncorrectTextList = agreementIncorrectTextList;
    }
}
