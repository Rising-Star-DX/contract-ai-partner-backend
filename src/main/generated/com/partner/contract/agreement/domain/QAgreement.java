package com.partner.contract.agreement.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgreement is a Querydsl query type for Agreement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgreement extends EntityPathBase<Agreement> {

    private static final long serialVersionUID = -786161683L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgreement agreement = new QAgreement("agreement");

    public final ListPath<AgreementIncorrectText, QAgreementIncorrectText> agreementIncorrectTextList = this.<AgreementIncorrectText, QAgreementIncorrectText>createList("agreementIncorrectTextList", AgreementIncorrectText.class, QAgreementIncorrectText.class, PathInits.DIRECT2);

    public final EnumPath<com.partner.contract.common.enums.AiStatus> aiStatus = createEnum("aiStatus", com.partner.contract.common.enums.AiStatus.class);

    public final com.partner.contract.category.domain.QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<com.partner.contract.common.enums.FileStatus> fileStatus = createEnum("fileStatus", com.partner.contract.common.enums.FileStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberAgreement, QMemberAgreement> memberAgreementList = this.<MemberAgreement, QMemberAgreement>createList("memberAgreementList", MemberAgreement.class, QMemberAgreement.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> totalChunks = createNumber("totalChunks", Integer.class);

    public final NumberPath<Integer> totalPage = createNumber("totalPage", Integer.class);

    public final EnumPath<com.partner.contract.common.enums.FileType> type = createEnum("type", com.partner.contract.common.enums.FileType.class);

    public final StringPath url = createString("url");

    public QAgreement(String variable) {
        this(Agreement.class, forVariable(variable), INITS);
    }

    public QAgreement(Path<? extends Agreement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgreement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgreement(PathMetadata metadata, PathInits inits) {
        this(Agreement.class, metadata, inits);
    }

    public QAgreement(Class<? extends Agreement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.partner.contract.category.domain.QCategory(forProperty("category")) : null;
    }

}

