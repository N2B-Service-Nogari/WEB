package me.nogari.nogari.api.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

public class QueryDslUtil {

	// Path 파라미터 : compileQuerydsl 빌드를 통해서 생성된 Q타입 클래스의 객체
	// 정렬 대상이 되는 Q타입 클래스 객체 전달
	public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
		Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
		return new OrderSpecifier(order, fieldPath);
	}

}
