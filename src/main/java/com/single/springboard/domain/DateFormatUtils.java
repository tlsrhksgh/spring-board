package com.single.springboard.domain;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDateTime;

public class DateFormatUtils {
    public static Expression<String> formatDateTime(DateTimePath<LocalDateTime> date) {
        return Expressions.stringTemplate(
                "DATE_FORMAT({0}, '%y-%m-%d %H:%i:%s')",
                date
        );
    }
}
