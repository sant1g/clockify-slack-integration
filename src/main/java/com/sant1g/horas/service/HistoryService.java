package com.sant1g.horas.service;

import com.sant1g.horas.model.History;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.repository.HistoryRepository;
import com.sant1g.horas.request.SlackMessageRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

  private final HistoryRepository historyRepository;

  public HistoryService(HistoryRepository historyRepository) {
    this.historyRepository = historyRepository;
  }

  public SlackMessageRequest generateHistoryMessage(SlackUser user) {
    List<History> entries = this.getLatestBySlackUser(user);
    return new SlackMessageRequest.Builder(entries, true).build();
  }

  private List<History> getLatestBySlackUser(SlackUser user) {
    return this.historyRepository.findTop10BySlackUserOrderByDateDesc(user);
  }
}
