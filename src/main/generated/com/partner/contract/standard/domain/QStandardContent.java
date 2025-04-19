package com.partner.contract.standard.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStandardContent is a Querydsl query type for StandardContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStandardContent extends EntityPathBase<StandardContent> {

    private static final long serialVersionUID = -2097333670L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStandardContent standardContent = new QStandardContent("standardContent");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> page = createNumber("page", Integer.class);

    public final QStandard standard;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStandardContent(String variable) {
        this(StandardContent.class, forVariable(variable), INITS);
    }

    public QStandardContent(Path<? extends StandardContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStandardContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStandardContent(PathMetadata metadata, PathInits inits) {
        this(StandardContent.class, metadata, inits);
    }

    public QStandardContent(Class<? extends StandardContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.standard = inits.isInitialized("standard") ? new QStandard(forProperty("standard"), inits.get("standard")) : null;
    }

}

