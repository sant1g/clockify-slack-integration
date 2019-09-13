package com.sant1g.horas.repository;

import com.sant1g.horas.model.SlackUser;
import javax.persistence.NoResultException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackUserRepository extends JpaRepository<SlackUser, Long> {
  SlackUser findUserByUserId(String userId) throws NoResultException;
}
