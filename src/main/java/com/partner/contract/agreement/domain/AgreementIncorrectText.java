package com.partner.contract.agreement.domain;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
public class AgreementIncorrectText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String position;

    @Column(nullable = false)
    private Integer page;

    @Column(nullable = false)
    private Float accuracy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String incorrectText;

    @Column(nullable = false)
    private String proofText;

    @Column(nullable = false)
    private String correctedText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @Builder
    public AgreementIncorrectText(String position, Integer page, Float accuracy, LocalDateTime createdAt, String incorrectText, String proofText, String correctedText, Agreement agreement) {
        this.position = position;
        this.page = page;
        this.accuracy = accuracy;
        this.createdAt = createdAt;
        this.incorrectText = incorrectText;
        this.proofText = proofText;
        this.correctedText = correctedText;
        this.agreement = agreement;
    }
}
