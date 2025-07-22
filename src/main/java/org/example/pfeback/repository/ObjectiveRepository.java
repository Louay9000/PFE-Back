package org.example.pfeback.repository;

import org.example.pfeback.model.Objective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {


}
