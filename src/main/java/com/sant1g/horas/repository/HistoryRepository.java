package com.sant1g.horas.repository;

import com.sant1g.horas.model.History;
import com.sant1g.horas.model.SlackUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
  List<History> findTop10BySlackUserOrderByDateDesc(SlackUser slackUser);
}
