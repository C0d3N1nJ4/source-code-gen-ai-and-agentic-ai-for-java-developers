package com.acme.assistant.config;

import com.acme.assistant.tool.CustomerTools;
import com.acme.assistant.tool.EmployeeTools;
import com.acme.assistant.tool.ExpenseTools;
import com.acme.assistant.tool.KnowledgeTools;
import com.acme.assistant.tool.LeaveTools;
import com.acme.assistant.tool.MeetingTools;
import com.acme.assistant.tool.SupportTicketTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {

    @Bean
    ChatClient chatClient(
            ChatModel chatModel,
            LeaveTools leaveTools,
            MeetingTools meetingTools,
            EmployeeTools employeeTools,
            ExpenseTools expenseTools,
            CustomerTools customerTools,
            SupportTicketTools supportTicketTools,
            KnowledgeTools knowledgeTools) {
        return ChatClient.builder(chatModel)
                .defaultTools(
                        leaveTools,
                        meetingTools,
                        employeeTools,
                        expenseTools,
                        customerTools,
                        supportTicketTools,
                        knowledgeTools)
                .build();
    }
}
