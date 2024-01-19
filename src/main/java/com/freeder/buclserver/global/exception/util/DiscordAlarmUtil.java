package com.freeder.buclserver.global.exception.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DiscordAlarmUtil {

	private static String webhookUrl;

	@Value("${webhook.url.discord}")
	private void setWebhookUrl(String url) {
		webhookUrl = url;
	}

	public static void send(Exception ex, String errorMessage, String stackTrace) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		try {
			new RestTemplate().exchange(
				webhookUrl,
				HttpMethod.POST,
				new HttpEntity<>(createMessage(ex, errorMessage, stackTrace), headers),
				String.class
			);
		} catch (Exception e) {
			log.error("디스코드에 500 에러에 대한 알림을 보내는데 실패했습니다.");
		}
	}

	private static String createMessage(Exception ex, String errorMessage, String stackTrace) {
		JSONObject jsonMessage = new JSONObject();
		String errorLocation = stackTrace.split("\\n")[1].trim();

		try {
			jsonMessage.put("content", "\uD83D\uDD25  500 에러가 발생하였습니다");
			JSONObject embeds = new JSONObject()
				.put("title", ex.getClass() + " " + errorLocation)
				.put("description", errorMessage);

			JSONArray embedsArray = new JSONArray().put(embeds);

			jsonMessage.put("embeds", embedsArray);
		} catch (JSONException e) {
			log.error("디스코드에 보낼 내용을 만드는 과정에서 에러가 발생했습니다.");
		}
		return jsonMessage.toString();
	}
}