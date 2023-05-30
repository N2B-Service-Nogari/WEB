package me.nogari.nogari.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.nogari.nogari.entity.Token;

public interface MemberTokenRepository extends JpaRepository<Token, Long> {

}
