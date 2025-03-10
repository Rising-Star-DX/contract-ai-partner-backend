package com.partner.contract.standard.domain;

import com.partner.contract.agreement.domain.FileType;
import com.partner.contract.agreement.domain.UploadStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Standard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

}
