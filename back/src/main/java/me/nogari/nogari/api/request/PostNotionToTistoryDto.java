package me.nogari.nogari.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostNotionToTistoryDto {
	// AWS Lambda
	private String type;

	// Tistory
	private String tistoryId;
	private String blogName;
	private String requestLink;
	private Byte visibility;
	private String categoryName;
	private String tagList;
	private String status;
}
