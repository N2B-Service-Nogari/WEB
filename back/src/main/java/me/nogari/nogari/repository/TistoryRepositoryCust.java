package me.nogari.nogari.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import me.nogari.nogari.api.request.PaginationDto;
import me.nogari.nogari.api.response.QTistoryContentResponseDto;
import me.nogari.nogari.api.response.TistoryContentResponseDto;
import me.nogari.nogari.api.service.QueryDslUtil;
import me.nogari.nogari.entity.Member;
import me.nogari.nogari.entity.QTistory;

@RequiredArgsConstructor
@Repository
public class TistoryRepositoryCust {

	private final JPAQueryFactory query;
	QTistory tistory = QTistory.tistory;

	public List<TistoryContentResponseDto> tistoryPaginationNoOffset(PaginationDto paginationDto, Member member) {

		List<OrderSpecifier> ORDERS = getAllOrdersSpecifier(paginationDto);

		String searchWord = Objects.isNull(paginationDto.getWord()) ? "" : paginationDto.getWord();
		List<String> statusList = Objects.isNull(paginationDto.getStatusList()) ? Collections.emptyList() : paginationDto.getStatusList();

		return query
			.select(
				new QTistoryContentResponseDto(
				tistory.tistoryId,
				tistory.blogName,
				tistory.title,
				tistory.categoryName,
				tistory.visibility,
				tistory.status,
				tistory.requestLink,
				tistory.responseLink,
				tistory.tagList,
				tistory.modifiedDate
				)
			)
			.from(tistory)
			.where(
				tistory.title.contains(searchWord),
				tistory.member.eq(member),
				ltTistoryId(paginationDto),  // 마지막 id보다 크거나 작은 것
				statusContain(statusList)		// 다중선택한 상태에 맞는 것
			)
			.orderBy(ORDERS.stream().toArray(OrderSpecifier[]::new))
			.limit(paginationDto.getPageSize())            // 조회할 size
			.fetch();
	}

	// 다중 선택한 발행상태에 맞는것
	private BooleanBuilder statusContain(List<String> statusList) {
		if(statusList.isEmpty()) return null;
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		for(String s : statusList){
			booleanBuilder.or(tistory.status.eq(s));
		}
		return booleanBuilder;
	}

	// 정렬 필터 : modifiedDate 최신순, 오래된순
	private List<OrderSpecifier> getAllOrdersSpecifier(PaginationDto paginationDto) {

		List<OrderSpecifier> ORDERS = new ArrayList<>();
		// System.out.println(paginationDto.getFilter());

		if(!Objects.isNull(paginationDto.getFilter())) {
			// 최신순, 오래된순
			Order direction = paginationDto.getFilter().equals("최신순") ? Order.DESC : Order.ASC;

			// tistory 발행 순을 기준으로
			OrderSpecifier<?> tistoryId= QueryDslUtil.getSortedColumn(direction, QTistory.tistory, "tistoryId");

			ORDERS.add(tistoryId);
		}

		return ORDERS;
	}

	// 마지막으로 조회된 Id보다 작은 값들을 선택하는 조건
	private BooleanExpression ltTistoryId(PaginationDto paginationDto) {
		Long lastTistoryId = paginationDto.getLastTistoryId();
		String filter = paginationDto.getFilter();

		if (lastTistoryId == null) {
			return null; // BooleanExpression 자리에 null이 반환되면 조건문에서 자동으로 제거된다
		}

		// lastTistoryId 보다 큰 데이터 리턴
		return tistory.tistoryId.gt(lastTistoryId);
	}
}