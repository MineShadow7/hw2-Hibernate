package org.hwmoodle.core.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hwmoodle.core.dto.UserEventOperation;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserNotificationPublisher {
    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @Value("${app.notification.topic:user-events}")
    private String topic;

    public void publishUserCreatedEvent(String email) {
        log.info("Publishing USER_CREATED event for email: {}", email);
        UserNotificationEvent event = new UserNotificationEvent(UserEventOperation.CREATED, email);
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("User created event published successfully for email: {}", email);
                    } else {
                        log.error("Failed to publish user created event for email: {}", email, ex);
                    }
                });
    }

    public void publishUserDeletedEvent(String email) {
        log.info("Publishing USER_DELETED event for email: {}", email);
        UserNotificationEvent event = new UserNotificationEvent(UserEventOperation.DELETED, email);
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("User deleted event published successfully for email: {}", email);
                    } else {
                        log.error("Failed to publish user deleted event for email: {}", email, ex);
                    }
                });
    }
}

