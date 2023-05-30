package me.nogari.nogari.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.nogari.nogari.api.response.TistoryResponseInterface;
import me.nogari.nogari.entity.Github;
import me.nogari.nogari.entity.Tistory;

public interface GithubRepository extends JpaRepository<Github, Long> {
	Github findByGithubId(Long githubId);
}
