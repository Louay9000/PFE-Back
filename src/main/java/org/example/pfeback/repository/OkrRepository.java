package org.example.pfeback.repository;

import org.example.pfeback.model.Okr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OkrRepository extends JpaRepository <Okr,Long> {
}
