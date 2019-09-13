package com.sant1g.horas.service;

import com.sant1g.horas.request.SlackMessageRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageService {

  public void sendMessage(SlackMessageRequest request, String url) {
    RestTemplate restTemplate = new RestTemplate();
    try {
      restTemplate.postForEntity(url, request, String.class);
    } catch (HttpClientErrorException e) {
      System.out.println(e.getMessage());
    }
  }

  public SlackMessageRequest getSimpleTextRequest(String text) {
    return new SlackMessageRequest.Builder(text).build();
  }
}
