package me.nogari.nogari.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.nogari.nogari.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	// v2 fetch join
	@Query(value = "select m from Member m " +
		"join fetch m.token " +
		"join fetch m.roles " +
		"where m.email = :email")
	Optional<Member> findAllByEmail(@Param("email") String email);

	boolean existsByEmail(String email);
}
