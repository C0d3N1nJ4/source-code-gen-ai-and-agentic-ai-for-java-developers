package com.acme.assistant.service;

import com.acme.assistant.entity.MeetingEntity;
import com.acme.assistant.model.Meeting;
import com.acme.assistant.model.MeetingRequest;
import com.acme.assistant.repository.MeetingRepository;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MeetingService {

    private final EmployeeDirectoryService employeeDirectoryService;
    private final MeetingRepository meetingRepository;

    public MeetingService(EmployeeDirectoryService employeeDirectoryService, MeetingRepository meetingRepository) {
        this.employeeDirectoryService = employeeDirectoryService;
        this.meetingRepository = meetingRepository;
    }

    public Meeting scheduleMeeting(MeetingRequest request) {
        employeeDirectoryService.requireEmployee(request.organizerId());

        String meetingId = "MTG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        MeetingEntity entity = new MeetingEntity(
            meetingId,
            request.organizerId(),
            request.attendee(),
            request.title(),
            request.startTime(),
            request.durationMinutes() == null ? 30 : request.durationMinutes(),
            "SCHEDULED");

        meetingRepository.save(entity);

        return new Meeting(
                entity.getId(),
                entity.getOrganizerId(),
                entity.getAttendee(),
                entity.getTitle(),
                entity.getStartTime(),
                entity.getDurationMinutes(),
                entity.getStatus());
    }
}
