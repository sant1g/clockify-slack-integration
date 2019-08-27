package com.sant1g.horas.service;

import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.repository.SlackUserRepository;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Service;

@Service
public class SlackUserService {

  private SlackUserRepository slackUserRepository;

  public SlackUserService(SlackUserRepository slackUserRepository) {
    this.slackUserRepository = slackUserRepository;
  }

  public String saveOrUpdate(String userId, String apiKey, String userName) {
    SlackUser user = slackUserRepository.findUserByUserId(userId);

    if (user != null) {
      return "Error! Already registered :red-card:";
    }

    user = new SlackUser();
    user.setUserId(userId);
    user.setApiKey(apiKey);
    user.setUserName(userName);
    slackUserRepository.save(user);

    return "Success! Your API Key has been saved :tada:";
  }

  public SlackUser findOrFail(String userId) throws NoResultException {
    SlackUser user = slackUserRepository.findUserByUserId(userId);

    if (user == null) {
      throw new NoResultException();
    }

    return user;
  }
}
