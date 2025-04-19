package com.partner.contract.standard.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberStandard is a Querydsl query type for MemberStandard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberStandard extends EntityPathBase<MemberStandard> {

    private static final long serialVersionUID = 2012136537L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberStandard memberStandard = new QMemberStandard("memberStandard");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.partner.contract.member.domain.QMember member;

    public final QStandard standard;

    public QMemberStandard(String variable) {
        this(MemberStandard.class, forVariable(variable), INITS);
    }

    public QMemberStandard(Path<? extends MemberStandard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberStandard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberStandard(PathMetadata metadata, PathInits inits) {
        this(MemberStandard.class, metadata, inits);
    }

    public QMemberStandard(Class<? extends MemberStandard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.partner.contract.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.standard = inits.isInitialized("standard") ? new QStandard(forProperty("standard"), inits.get("standard")) : null;
    }

}

