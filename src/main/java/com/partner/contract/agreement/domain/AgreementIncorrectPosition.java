package com.partner.contract.agreement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class AgreementIncorrectPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String position;

    private Integer page;

    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_incorrect_text_id", nullable = false)
    private AgreementIncorrectText agreementIncorrectText;

    @Builder
    public AgreementIncorrectPosition(String position, Integer page, Integer orderIndex, AgreementIncorrectText agreementIncorrectText) {
        this.position = position;
        this.page = page;
        this.orderIndex = orderIndex;
        this.agreementIncorrectText = agreementIncorrectText;
    }
}
