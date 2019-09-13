package com.sant1g.horas.service;

import com.sant1g.horas.model.ClockifyUser;
import com.sant1g.horas.model.Project;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.repository.SlackUserRepository;
import java.util.List;
import javax.persistence.NoResultException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackUserService {

  private SlackUserRepository slackUserRepository;

  public SlackUserService(SlackUserRepository slackUserRepository) {
    this.slackUserRepository = slackUserRepository;
  }

  public SlackUser save(String userId, String apiKey, String userName) {
    SlackUser user = slackUserRepository.findUserByUserId(userId);

    if (user != null) {
      return null;
    }

    ClockifyUser cUser = getUser(apiKey);

    user = new SlackUser();
    user.setUserId(userId);
    user.setApiKey(apiKey);
    user.setUserName(userName);
    if (cUser != null) {
      user.setClockifyId(cUser.getId());
    }
    slackUserRepository.save(user);

    return user;
  }

  private ClockifyUser getUser(String apiKey) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Api-Key", apiKey);

    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    try {
      ResponseEntity<ClockifyUser> response = restTemplate.exchange(
          "https://api.clockify.me/api/v1/user/",
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<ClockifyUser>(){},
          headers);

      return response.getBody();
    } catch (HttpClientErrorException e) {
      // TODO: Log Message
      return null;
    }
  }

  public SlackUser findOrFail(String userId) throws NoResultException {
    SlackUser user = slackUserRepository.findUserByUserId(userId);

    if (user == null) {
      throw new NoResultException();
    }

    return user;
  }

  public void updateUserProjects(SlackUser user, List<Project> projects) {
    user.setProjects(projects);
    slackUserRepository.save(user);
  }
}
