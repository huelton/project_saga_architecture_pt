package com.saga.notification.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationConstantsTest {

    @Test
    void topicsAreDefined() {
        assertNotNull(NotificationConstants.TOPIC_NOTIFICATION_SEND);
        assertNotNull(NotificationConstants.TOPIC_NOTIFICATION_SENT);
    }

    @Test
    void topicValuesMatchExpected() {
        assertEquals(TestConstants.EXPECTED_TOPIC_SEND, NotificationConstants.TOPIC_NOTIFICATION_SEND);
        assertEquals(TestConstants.EXPECTED_TOPIC_SENT, NotificationConstants.TOPIC_NOTIFICATION_SENT);
    }
}
