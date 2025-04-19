package com.partner.contract.standard.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStandard is a Querydsl query type for Standard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStandard extends EntityPathBase<Standard> {

    private static final long serialVersionUID = -1748508385L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStandard standard = new QStandard("standard");

    public final EnumPath<com.partner.contract.common.enums.AiStatus> aiStatus = createEnum("aiStatus", com.partner.contract.common.enums.AiStatus.class);

    public final com.partner.contract.category.domain.QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<com.partner.contract.common.enums.FileStatus> fileStatus = createEnum("fileStatus", com.partner.contract.common.enums.FileStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberStandard, QMemberStandard> memberStandardList = this.<MemberStandard, QMemberStandard>createList("memberStandardList", MemberStandard.class, QMemberStandard.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<StandardContent, QStandardContent> standardContentList = this.<StandardContent, QStandardContent>createList("standardContentList", StandardContent.class, QStandardContent.class, PathInits.DIRECT2);

    public final NumberPath<Integer> totalPage = createNumber("totalPage", Integer.class);

    public final EnumPath<com.partner.contract.common.enums.FileType> type = createEnum("type", com.partner.contract.common.enums.FileType.class);

    public final StringPath url = createString("url");

    public QStandard(String variable) {
        this(Standard.class, forVariable(variable), INITS);
    }

    public QStandard(Path<? extends Standard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStandard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStandard(PathMetadata metadata, PathInits inits) {
        this(Standard.class, metadata, inits);
    }

    public QStandard(Class<? extends Standard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.partner.contract.category.domain.QCategory(forProperty("category")) : null;
    }

}

