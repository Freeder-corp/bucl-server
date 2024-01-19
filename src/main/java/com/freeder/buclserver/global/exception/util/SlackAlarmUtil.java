package com.freeder.buclserver.global.exception.util;

import static com.slack.api.webhook.WebhookPayloads.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackAlarmUtil {

	private static String webhookUrl;
	private static final Slack slackClient = Slack.getInstance();

	@Value("${webhook.url.slack}")
	private void setWebhookUrl(String url) {
		webhookUrl = url;
	}

	public static void send(Exception ex, String errorMessage, String stackTrace) {
		try {
			slackClient.send(webhookUrl, payload(p -> p
				.text("\uD83D\uDD25  500 에러가 발생하였습니다")
				.attachments(createMessage(ex, errorMessage, stackTrace))
			));
		} catch (Exception e) {
			log.error("슬랙에 500 에러에 대한 알림을 보내는데 실패했습니다.");
		}
	}

	private static List<Attachment> createMessage(Exception ex, String errorMessage, String stackTrace) {
		String errorLocation = stackTrace.split("\\n")[1].trim();

		return List.of(Attachment.builder()
			.color("#FF0000")
			.fields(List.of(
				Field.builder()
					.title(ex.getClass() + " " + errorLocation)
					.value(errorMessage)
					.build()))
			.build());
	}
}
