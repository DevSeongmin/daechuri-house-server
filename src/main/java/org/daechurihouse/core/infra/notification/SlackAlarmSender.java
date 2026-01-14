package org.daechurihouse.core.infra.notification;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;

import org.daechurihouse.domain.alarm.Alarm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.slack.api.model.block.element.BlockElements.*;
import com.slack.api.Slack;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.webhook.Payload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackAlarmSender implements AlarmSender {

	@Value("${slack.webhook.url}")
	private String webhookUrl;

	private final Slack slack = Slack.getInstance();

	@Override
	public boolean send(Alarm alarm) {
		Payload payload = Payload.builder()
			.text(alarm.getType() + " 알림")  // 푸시 알림용
			.blocks(createBlocks(alarm))  // 실제 표시될 내용
			.build();

		try {
			slack.send(webhookUrl, payload);
			return true;
		} catch (IOException e) {
			log.warn("Failed to send alarm: {}", alarm.getId());
			return false;
		}
	}

	private java.util.List<LayoutBlock> createBlocks(Alarm alarm) {
		return asBlocks(
			header(header -> header.text(
				plainText("🔔 " + alarm.getType() + " 알림")
			)),
			divider(),
			section(section -> section.text(
				markdownText(alarm.getAlarmMessage())
			)),
			divider(),
			actions(actions -> actions
				.elements(asElements(
					button(button -> button
						.text(plainText("홈페이지 바로가기"))
						.url("https://daechuri-house.kr")
						.style("primary")
					)
				))
			)
		);
	}
}
