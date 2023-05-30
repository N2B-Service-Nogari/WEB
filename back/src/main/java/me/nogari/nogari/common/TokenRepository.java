package me.nogari.nogari.common;

import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<JWT, Long> {
}