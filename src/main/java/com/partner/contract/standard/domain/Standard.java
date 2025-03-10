package com.partner.contract.standard.domain;

import com.partner.contract.agreement.domain.FileType;
import com.partner.contract.agreement.domain.UploadStatus;
import com.partner.contract.category.domain.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private UploadStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public Standard(String name, FileType type, String url, UploadStatus status, LocalDateTime createdAt, Category category) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.category = category;
    }
}
