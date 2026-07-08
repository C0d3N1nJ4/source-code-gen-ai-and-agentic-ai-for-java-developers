package com.acme.assistant.chapter11.memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.assistant.chapter11.advisors.PromptTelemetryEstimator;
import com.acme.assistant.chapter11.entities.ConversationMessageEntity;
import com.acme.assistant.chapter11.repositories.ConversationMessageRepository;
import com.acme.assistant.chapter11.repositories.ConversationRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationMemoryServiceTest {

    @Test
    void shouldReturnShortTermContext() {
        ConversationRepository conversationRepository = org.mockito.Mockito.mock(ConversationRepository.class);
        ConversationMessageRepository messageRepository = org.mockito.Mockito.mock(ConversationMessageRepository.class);
        PromptTelemetryEstimator estimator = new PromptTelemetryEstimator();

        ConversationMessageEntity user = new ConversationMessageEntity();
        user.setRole("user");
        user.setContent("Hello");
        user.setCreatedAt(OffsetDateTime.now().minusMinutes(1));

        ConversationMessageEntity assistant = new ConversationMessageEntity();
        assistant.setRole("assistant");
        assistant.setContent("Hi");
        assistant.setCreatedAt(OffsetDateTime.now());

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc("conv-1"))
                .thenReturn(List.of(user, assistant));
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConversationMemoryService service = new ConversationMemoryService(conversationRepository, messageRepository, estimator);
        List<String> context = service.shortTermContext("conv-1", 4);

        assertThat(context).containsExactly("user: Hello", "assistant: Hi");
    }
}
