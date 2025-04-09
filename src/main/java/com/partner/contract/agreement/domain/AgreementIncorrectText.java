package com.partner.contract.agreement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AgreementIncorrectText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double accuracy;

    @CreatedDate
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

    @OneToMany(mappedBy = "agreementIncorrectText", cascade = CascadeType.ALL)
    private List<AgreementIncorrectPosition> agreementIncorrectPositionList;

    @Builder
    public AgreementIncorrectText(Double accuracy, LocalDateTime createdAt, String incorrectText, String proofText, String correctedText, Agreement agreement, List<AgreementIncorrectPosition> agreementIncorrectPositionList) {
        this.accuracy = accuracy;
        this.createdAt = createdAt;
        this.incorrectText = incorrectText;
        this.proofText = proofText;
        this.correctedText = correctedText;
        this.agreement = agreement;
        this.agreementIncorrectPositionList = agreementIncorrectPositionList;
    }
}
