package com.single.springboard.util;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDateTime;

public class JpaCommonUtils {
    public static Expression<String> formattedModifiedDate(DateTimePath<LocalDateTime> date) {
        return ExpressionUtils.as(
                Expressions.stringTemplate("FORMATDATETIME({0}, {1})", date, "yyyy-MM-dd HH:mm:ss"),
                "formattedModifiedDate"
        );
    }
}
