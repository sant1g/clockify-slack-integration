package com.sant1g.horas.repository;

import com.sant1g.horas.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  Project getById(String id);
}
