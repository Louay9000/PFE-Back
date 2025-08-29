package org.example.pfeback.service;



import org.example.pfeback.model.Meeting;
import org.example.pfeback.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Optional<Meeting> getMeetingById(Long id) {
        return meetingRepository.findById(id);
    }

    public Meeting createMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Long id, Meeting meetingDetails) {
        return meetingRepository.findById(id).map(meeting -> {
            meeting.setTitle(meetingDetails.getTitle());
            meeting.setDescription(meetingDetails.getDescription());
            meeting.setPlatform(meetingDetails.getPlatform());
            meeting.setLink(meetingDetails.getLink());
            meeting.setStartTime(meetingDetails.getStartTime());
            meeting.setEndTime(meetingDetails.getEndTime());

            return meetingRepository.save(meeting);
        }).orElseThrow(() -> new RuntimeException("Meeting not found with id " + id));
    }

    public void deleteMeeting(Long id) {
        meetingRepository.deleteById(id);
    }
}
