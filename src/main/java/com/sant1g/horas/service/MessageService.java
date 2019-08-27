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

  public SlackMessageRequest getNoUserFoundRequest() {
    return new SlackMessageRequest.Builder(
        "Oops, it seems you are not registered yet. Please use the */register* command followed by your API Key from https://clockify.me/user/settings :relieved:")
        .build();
  }

  public SlackMessageRequest getInvalidSignatureVerificationRequest() {
    return new SlackMessageRequest.Builder(
        "Error! We couldn't verify the request signature :red-card:")
        .build();
  }
}
