package com.partner.contract.member.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN("ADMIN", "관리자"),
    USER("USER", "사용자");

    private final String key;
    private final String value;
}
