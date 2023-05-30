package me.nogari.nogari.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAccessTokenResponse {

	@JsonProperty("access_token")
	private String accessToken;
	private String bot_id;
	private String workspace_id;
	private String workspace_name;
}

// {
// 	"access_token": "e202e8c9-0990-40af-855f-ff8f872b1ec6c",
// 	"bot_id": "b3414d659-1224-5ty7-6ffr-cc9d8773drt601288f",
// 	"duplicated_template_id": null,
// 	"owner": {
// 	"workspace": true
// 	},
// 	"workspace_icon": "https://website.domain/images/image.png",
// 	"workspace_id": "j565j4d7x3-2882-61bs-564a-jj9d9ui-c36hxfr7x",
// 	"workspace_name": "Ada's Notion Workspace"
// 	}
