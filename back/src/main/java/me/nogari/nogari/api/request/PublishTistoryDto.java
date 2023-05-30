package me.nogari.nogari.api.request;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishTistoryDto {

	private String title;
	private String category;
	private String tag;
	private String link;

}
