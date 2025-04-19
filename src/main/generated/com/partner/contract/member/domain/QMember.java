package com.partner.contract.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1657093927L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final QCompany company;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath identifier = createString("identifier");

    public final ListPath<com.partner.contract.agreement.domain.MemberAgreement, com.partner.contract.agreement.domain.QMemberAgreement> memberAgreementList = this.<com.partner.contract.agreement.domain.MemberAgreement, com.partner.contract.agreement.domain.QMemberAgreement>createList("memberAgreementList", com.partner.contract.agreement.domain.MemberAgreement.class, com.partner.contract.agreement.domain.QMemberAgreement.class, PathInits.DIRECT2);

    public final ListPath<com.partner.contract.standard.domain.MemberStandard, com.partner.contract.standard.domain.QMemberStandard> memberStandardList = this.<com.partner.contract.standard.domain.MemberStandard, com.partner.contract.standard.domain.QMemberStandard>createList("memberStandardList", com.partner.contract.standard.domain.MemberStandard.class, com.partner.contract.standard.domain.QMemberStandard.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<com.partner.contract.common.enums.Role> role = createEnum("role", com.partner.contract.common.enums.Role.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompany(forProperty("company")) : null;
    }

}

