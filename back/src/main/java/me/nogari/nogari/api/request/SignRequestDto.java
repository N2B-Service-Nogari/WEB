package me.nogari.nogari.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignRequestDto {
	private String email;
	private String password;
}
