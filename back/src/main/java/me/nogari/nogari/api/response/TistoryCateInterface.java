package me.nogari.nogari.api.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public interface TistoryCateInterface {

	String getId();
	String getName();
	String getParent();
	String getLabel();
	String getEntries();



}
