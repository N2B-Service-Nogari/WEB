package me.nogari.nogari.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tistory extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tistory_id")
	private Long tistoryId;

	@Column(name = "post_id")
	private Long postId;

	@Column(name = "blog_name", length = 300, nullable = false)
	private String blogName;

	@Column(name = "title", length = 300)
	private String title;

	@Column(name = "category_name", length = 50)
	private String categoryName;

	@Column(name = "visibility", nullable = false)
	private Byte visibility;

	@Column(name = "status", length = 50, nullable = false)
	private String status;

	@Column(name = "request_link", length = 300, nullable = false)
	private String requestLink;

	@Column(name = "response_link", length = 300, unique=true)
	private String responseLink;

	@Column(name = "tag_list", length = 300)
	private String tagList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
}
