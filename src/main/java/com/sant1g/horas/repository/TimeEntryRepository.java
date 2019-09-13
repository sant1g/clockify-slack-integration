package com.sant1g.horas.repository;

import com.sant1g.horas.model.TimeEntry;
import javax.persistence.NoResultException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
  TimeEntry findTimeEntryBySlackUserId(Long userId) throws NoResultException;
  void deleteAllBySlackUserId(Long userId);
}
