package me.nogari.nogari.api.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nogari.nogari.entity.Tistory;

@Getter
@NoArgsConstructor
@Builder
public class TistoryContentResponseDto {
	private Long tistoryId;
	private String blogName;
	private String title;
	private String categoryName;
	private Byte visibility;
	private String status;
	private String requestLink;
	private String responseLink;
	private String tagList;
	private String modifiedDate;

	@QueryProjection
	public TistoryContentResponseDto(Tistory tistory) {
		this.tistoryId = tistory.getTistoryId();
		this.modifiedDate = tistory.getModifiedDate();
		this.blogName = tistory.getBlogName();
		this.title = tistory.getTitle();
		this.categoryName = tistory.getCategoryName();
		this.visibility = tistory.getVisibility();
		this.status = tistory.getStatus();
		this.requestLink = tistory.getRequestLink();
		this.responseLink = tistory.getResponseLink();
		this.tagList = tistory.getTagList();
	}
	@QueryProjection
	public TistoryContentResponseDto(Long tistoryId, String blogName, String title, String categoryName,
		Byte visibility, String status, String requestLink, String responseLink, String tagList, String modifiedDate) {
		this.tistoryId = tistoryId;
		this.modifiedDate = modifiedDate;
		this.blogName = blogName;
		this.title = title;
		this.categoryName = categoryName;
		this.visibility = visibility;
		this.status = status;
		this.requestLink = requestLink;
		this.responseLink = responseLink;
		this.tagList = tagList;
	}
}