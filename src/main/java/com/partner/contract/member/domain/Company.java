package com.partner.contract.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Member> memberList;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Department> departmentList;

    @Builder
    public Company(String name, LocalDateTime createdAt, LocalDateTime updatedAt, List<Member> memberList, List<Department> departmentList) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberList = memberList;
        this.departmentList = departmentList;
    }
}
