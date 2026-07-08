package com.acme.assistant.repository;

import com.acme.assistant.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<MeetingEntity, String> {
}
