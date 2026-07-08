package com.acme.assistant.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeaveAgentTest {

    private final ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-07-03T00:00:00Z"), ZoneOffset.UTC);
    private final LeaveAgent leaveAgent = new LeaveAgent(chatClient, fixedClock);

    @Test
    void shouldParseModelJsonDecision() {
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn("{\"intent\":\"SUBMIT_LEAVE_REQUEST\",\"targetDate\":\"2026-07-10\",\"reason\":\"User asked to book leave\"}");

        AgentDecision decision = leaveAgent.decide("Book annual leave for next Friday", "Policy context");

        assertEquals("SUBMIT_LEAVE_REQUEST", decision.intent());
        assertEquals("2026-07-10", decision.targetDate());
    }

    @Test
    void shouldFallbackWhenModelResponseIsInvalid() {
        when(chatClient.prompt().user(anyString()).call().content())
                .thenThrow(new RuntimeException("Model unavailable"));

        AgentDecision decision = leaveAgent.decide("Book annual leave for next Friday", "Policy context");

        assertEquals("SUBMIT_LEAVE_REQUEST", decision.intent());
        assertEquals("2026-07-10", decision.targetDate());
        assertTrue(decision.reason().contains("Fallback"));
    }

    @Test
    void shouldNormalizeInvalidDateToUnknown() {
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn("{\"intent\":\"SUBMIT_LEAVE_REQUEST\",\"targetDate\":\"next friday\",\"reason\":\"informal date\"}");

        AgentDecision decision = leaveAgent.decide("Please process annual leave", "Policy context");

        assertEquals("SUBMIT_LEAVE_REQUEST", decision.intent());
        assertEquals("UNKNOWN", decision.targetDate());
    }
}
