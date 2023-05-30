package me.nogari.nogari.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
// import me.nogari.nogari.entity.QTistory;
import me.nogari.nogari.entity.Tistory;

@RequiredArgsConstructor
@Repository
public class SearchRepository {

	// private final JPAQueryFactory query;
	//
	// public List<Tistory> SearchTistoryContents(Long userNo) {
	// 	QTistory tistory = QTistory.tistory;
	//
	// 	List<Tistory> fetched = query
	// 		.select(tistory)
	// 		.from(tistory)
	// 		.where()
	// 		.fetch();
	//
	// 	return fetched;
	// }
}
