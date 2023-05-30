package me.nogari.nogari.api.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.nogari.nogari.common.JWTDto;
import me.nogari.nogari.entity.Authority;
import me.nogari.nogari.entity.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TistoryCateDto {

	String id;
	String name;
	String parent;
	String label;
	String entries;
}
