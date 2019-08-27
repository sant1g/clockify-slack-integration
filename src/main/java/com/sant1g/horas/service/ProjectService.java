package com.sant1g.horas.service;

import com.sant1g.horas.model.Project;
import com.sant1g.horas.request.SlackMessageRequest;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProjectService {

  public List<Project> getAllProjects(String apiKey) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Api-Key", apiKey);

    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    try {
      ResponseEntity<List<Project>> response = restTemplate.exchange(
          "https://api.clockify.me/api/v1/workspaces/5d4c1feeac685f40379c4c28/projects",
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<List<Project>>(){},
          headers);

      return response.getBody();
    } catch (HttpClientErrorException e) {
      // TODO: Log Message
      return null;
    }
  }

  public SlackMessageRequest generateRequest(List<Project> projects) {
    return new SlackMessageRequest.Builder(projects).build();
  }
}
