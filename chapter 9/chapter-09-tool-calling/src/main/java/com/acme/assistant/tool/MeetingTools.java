package com.acme.assistant.tool;

import com.acme.assistant.model.Meeting;
import com.acme.assistant.model.MeetingRequest;
import com.acme.assistant.service.MeetingService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class MeetingTools {

    private final MeetingService meetingService;

    public MeetingTools(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @Tool(description = "Schedule an enterprise meeting with organizer, attendee, title, and start time.")
    public Meeting scheduleMeeting(MeetingRequest request) {
        return meetingService.scheduleMeeting(request);
    }
}
