package org.example.pfeback.repository;

import org.example.pfeback.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository  extends JpaRepository<Meeting, Long> {

}
