package com.partner.contract.agreement.domain;

import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.category.domain.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType type;

    private String url;

    @Enumerated(EnumType.STRING)
    private FileStatus fileStatus;

    @Enumerated(EnumType.STRING)
    private AiStatus aiStatus;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer totalPage;

    private Integer totalChunks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL)
    private List<AgreementIncorrectText> agreementIncorrectTextList;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL)
    private List<MemberAgreement> memberAgreementList;

    @Builder
    public Agreement(String name, FileType type, String url, FileStatus fileStatus, AiStatus aiStatus, LocalDateTime createdAt, Integer totalPage, Integer totalChunks, Category category, List<AgreementIncorrectText> agreementIncorrectTextList, List<MemberAgreement> memberAgreementList) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.fileStatus = fileStatus;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
        this.totalPage = totalPage;
        this.totalChunks = totalChunks;
        this.category = category;
        this.agreementIncorrectTextList = agreementIncorrectTextList;
        this.memberAgreementList = memberAgreementList;
    }

    public void updateFileStatus(String url, FileStatus fileStatus) {
        this.url = url;
        this.fileStatus = fileStatus;
    }

    public void updateAiStatus(AiStatus aiStatus) {
        this.aiStatus = aiStatus;
    }

    public void updateAnalysisInfomation(Integer totalPage, Integer totalChunks) {
        this.totalPage = totalPage;
        this.totalChunks = totalChunks;
    }
}
