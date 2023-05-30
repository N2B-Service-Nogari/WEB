package me.nogari.nogari.api.response;

import java.time.LocalDateTime;

import javax.persistence.criteria.CriteriaBuilder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public interface TistoryResponseInterface {

	String getRequest_link();
	Integer getVisibility();
	String getTitle();
	String getResponse_link();
	String getCategory_name();
	String getTag_list();
	String getModified_at();
	String getStatus();
	String getBlog_name();

}
