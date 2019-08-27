package com.sant1g.horas.controller;

import com.sant1g.horas.model.Project;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.model.TimeEntry;
import com.sant1g.horas.request.ClockifyEntryRequest;
import com.sant1g.horas.request.SlackMessageRequest;
import com.sant1g.horas.response.SlackOptionResponse;
import com.sant1g.horas.security.RequestValidator;
import com.sant1g.horas.service.MessageService;
import com.sant1g.horas.service.ProjectService;
import com.sant1g.horas.service.SlackUserService;
import com.sant1g.horas.service.TimeEntryService;
import java.util.List;
import javax.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HorasController {

  private static final Logger logger = LoggerFactory.getLogger(HorasController.class);

  private final ProjectService projectService;
  private final MessageService messageService;
  private final SlackUserService slackUserService;
  private final TimeEntryService timeEntryService;
  private final RequestValidator validator;

  public HorasController(ProjectService projectService, MessageService messageService,
      SlackUserService slackUserService, TimeEntryService timeEntryService,
      RequestValidator validator) {
    this.projectService = projectService;
    this.messageService = messageService;
    this.slackUserService = slackUserService;
    this.timeEntryService = timeEntryService;
    this.validator = validator;
  }

  @PostMapping("/register")
  public String register(
      @RequestParam("user_id") String userId,
      @RequestParam("text") String apiKey,
      @RequestParam("user_name") String userName,
      @RequestHeader("X-Slack-Request-Timestamp") String requestTimestamp,
      @RequestHeader("X-Slack-Signature") String requestSignature,
      @RequestBody String body) {
    logger.info("[POST /register] user_id={}, api_key={}, user_name={}", userId, apiKey, userName);
    return validator.validateSignature(requestTimestamp, body, requestSignature) ?
        slackUserService.saveOrUpdate(userId, apiKey, userName) : "Error! We couldn't verify the request signature :red-card:";
  }

  @PostMapping("/horas")
  public SlackMessageRequest horas(
      @RequestParam("user_id") String userId,
      @RequestParam("text") String message,
      @RequestHeader("X-Slack-Request-Timestamp") String requestTimestamp,
      @RequestHeader("X-Slack-Signature") String requestSignature,
      @RequestBody String body) {
    logger.info("[POST /horas] user_id={}, message={}", userId, message);
    if (validator.validateSignature(requestTimestamp, body, requestSignature)) {
      try {
        SlackUser user = slackUserService.findOrFail(userId);
        List<Project> projects = projectService.getAllProjects(user.getApiKey());
        SlackMessageRequest request = projectService.generateRequest(projects);
        timeEntryService.clearEntries(user.getId());
        timeEntryService.createTimeEntry(user, message);

        return request;
      } catch (NoResultException e) {
        return messageService.getNoUserFoundRequest();
      }
    } else {
      return messageService.getInvalidSignatureVerificationRequest();
    }
  }

  @PostMapping("/project")
  public void project(
      @RequestParam("payload") String message,
      @RequestHeader("X-Slack-Request-Timestamp") String requestTimestamp,
      @RequestHeader("X-Slack-Signature") String requestSignature,
      @RequestBody String body) {
    try {
      SlackOptionResponse optionResponse = timeEntryService.parsePayload(message);
      SlackUser slackUser = slackUserService.findOrFail(optionResponse.getUser().getId());
      TimeEntry timeEntry = timeEntryService.findTimeEntryBySlackUserId(slackUser.getId());

      ClockifyEntryRequest firstRequest = timeEntryService
          .generateFirstRequest(timeEntry, optionResponse);
      timeEntryService.sendRequest(firstRequest, slackUser.getApiKey());

      ClockifyEntryRequest secondRequest = timeEntryService
          .generateSecondRequest(timeEntry, optionResponse);
      timeEntryService.sendRequest(secondRequest, slackUser.getApiKey());

      timeEntryService.clearEntries(slackUser.getId());
      SlackMessageRequest request = validator.validateSignature(requestTimestamp, body, requestSignature) ?
        new SlackMessageRequest.Builder("Time entry sent! :tada:").build() :
          messageService.getInvalidSignatureVerificationRequest();

      messageService.sendMessage(request, optionResponse.getResponseUrl());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
