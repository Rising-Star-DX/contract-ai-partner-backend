package com.partner.contract.standard.domain;

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
public class Standard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType type;

    @Column
    private String url;

    @Column
    @Enumerated(EnumType.STRING)
    private FileStatus fileStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private AiStatus aiStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<MemberStandard> memberStandardList;

    @Builder
    public Standard(String name, FileType type, String url, FileStatus fileStatus, AiStatus aiStatus, LocalDateTime createdAt, Category category, List<MemberStandard> memberStandardList) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.fileStatus = fileStatus;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
        this.category = category;
        this.memberStandardList = memberStandardList;
    }

    public void updateFileStatus(String url, FileStatus fileStatus) {
        this.url = url;
        this.fileStatus = fileStatus;
    }

    public void updateAiStatus(AiStatus aiStatus) {
        this.aiStatus = aiStatus;
    }
}
