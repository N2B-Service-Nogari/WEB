package me.nogari.nogari.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.nogari.nogari.api.response.TistoryResponseInterface;
import me.nogari.nogari.entity.Tistory;

public interface TistoryRepository extends JpaRepository<Tistory, Long> {

	@Modifying
	@Query(
		nativeQuery = true,
		value = "select t.request_link, t.visibility, t.title, t.response_link, t.category_name, t.tag_list, t.modified_at, t.status, t.blog_name "
		+ "from Tistory t "
		+ "where t.member_id = :memberId "
		+ "order by t.modified_at desc")
	Optional<List<TistoryResponseInterface>> sortTistoryByNewest(@Param(("memberId")) Long memberId);

	@Modifying
	@Query(
		nativeQuery = true,
		value = "select t.request_link, t.visibility, t.title, t.response_link, t.category_name, t.tag_list, t.modified_at, t.status, t.blog_name "
			+ "from Tistory t "
			+ "where t.member_id = :memberId "
			+ "order by t.modified_at asc")
	Optional<List<TistoryResponseInterface>> sortTistoryByOldest(@Param(("memberId")) Long memberId);

	Tistory findByTistoryId(Long tistoryId);
}
