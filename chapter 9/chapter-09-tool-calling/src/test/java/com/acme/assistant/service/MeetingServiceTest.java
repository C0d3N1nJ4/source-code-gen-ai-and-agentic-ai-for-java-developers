package com.acme.assistant.service;

import com.acme.assistant.entity.EmployeeEntity;
import com.acme.assistant.model.MeetingRequest;
import com.acme.assistant.repository.MeetingRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeetingServiceTest {

    private final EmployeeDirectoryService employeeDirectoryService = mock(EmployeeDirectoryService.class);
    private final MeetingRepository meetingRepository = mock(MeetingRepository.class);
    private final MeetingService meetingService = new MeetingService(employeeDirectoryService, meetingRepository);

    @Test
    void shouldScheduleMeeting() {
        when(employeeDirectoryService.requireEmployee("EMP-1001"))
                .thenReturn(new EmployeeEntity("EMP-1001", "Sarah Connor", "sarah.connor@acme.com", "EMP-2001", 14));

        MeetingRequest request = new MeetingRequest(
                "EMP-1001",
                "Sarah",
                LocalDateTime.now().plusDays(1),
                45,
                "Project Kickoff");

        var result = meetingService.scheduleMeeting(request);

        assertThat(result.meetingId()).startsWith("MTG-");
        assertThat(result.status()).isEqualTo("SCHEDULED");
        assertThat(result.durationMinutes()).isEqualTo(45);
        verify(meetingRepository).save(any());
    }
}
