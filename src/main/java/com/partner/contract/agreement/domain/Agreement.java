package com.partner.contract.agreement.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Agreement {

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

    @Column(nullable = false)
    private String summaryContent;

    @Column(nullable = false)
    private Integer totalPage;

}
