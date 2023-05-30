package me.nogari.nogari.api.service;

import java.io.IOException;
import java.util.List;

import me.nogari.nogari.api.request.PaginationDto;
import me.nogari.nogari.api.request.PostNotionToGithubDto;
import me.nogari.nogari.api.request.PostNotionToTistoryDto;
import me.nogari.nogari.api.response.TistoryContentResponseDto;
import me.nogari.nogari.entity.Member;

public interface ContentService {

	List<Object> getTistoryList(PaginationDto paginationDto, Member member);

	Object getTistoryBlogName(List<String> blogNameList, Member member);

	Object getTistoryCates(List<String> blogNameList, List<Object> categories, Member member);
	// Object getTistoryCates(List<String> blogNameList, HashMap<String, List<Object>> categories, Member member);

	// [Single Thread] : 사용자가 N개의 발행 요청시, 작업이 순차적으로 수행되어 처리시간이 N개의 요청만큼 소요된다.
	// Object postNotionToTistory(List<PostNotionToTistoryDto> postNotionToTistoryDto, Member member);

	// [Multi Thread] : 사용자가 N개의 발행 요청시, 작업이 동시에 수행되어 처리시간이 기존 N초에서 (1/N)초 수준으로 단축된다.
	Object postNotionToTistoryMultiThread(List<PostNotionToTistoryDto> PostNotionToTistoryDtoList, Member member);

	Object postNotionToGithubMultiThread(List<PostNotionToGithubDto> postNotionToGithubDtoList, Member member);

	void upload(PostNotionToGithubDto githubPosting, Member member, String title, String content) throws IOException;

	List<Object> getGithubList(PaginationDto paginationDto, Member member);
}
