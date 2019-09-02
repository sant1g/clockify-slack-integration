package com.sant1g.horas.controller;

import com.sant1g.horas.model.Project;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.model.TimeEntry;
import com.sant1g.horas.request.ClockifyEntryRequest;
import com.sant1g.horas.request.SlackMessageRequest;
import com.sant1g.horas.response.SlackOptionResponse;
import com.sant1g.horas.security.RequestValidator;
import com.sant1g.horas.service.HistoryService;
import com.sant1g.horas.service.MessageService;
import com.sant1g.horas.service.ProjectService;
import com.sant1g.horas.service.SlackUserService;
import com.sant1g.horas.service.TimeEntryService;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HorasController {

  private static final Logger logger = LoggerFactory.getLogger(HorasController.class);

  private final ProjectService projectService;
  private final MessageService messageService;
  private final SlackUserService slackUserService;
  private final TimeEntryService timeEntryService;
  private final HistoryService historyService;
  private final RequestValidator validator;

  @Value("${app.error.not_registered}")
  private String NOT_REGISTERED;

  @Value("${app.error.invalid_signature}")
  private String INVALID_SIGNATURE;

  @Value("${app.error.no_projects}")
  private String NO_PROJECTS;

  @Value("${app.error.entry_already_sent_for_date}")
  private String ENTRY_ALREADY_SENT;

  @Value("${app.error.already_registered}")
  private String ALREADY_REGISTERED;

  @Value("${app.success.api_key_saved}")
  private String API_KEY_SAVED;

  @Value("${app.success.proyects_updated}")
  private String PROJECTS_UPDATED;

  @Value("${app.success.entry_sent}")
  private String ENTRY_SENT;

  @Value("${app.default.processing_request}")
  private String PROCESSIG_REQUEST;

  @Value("${app.default.not_implemented}")
  private String NOT_IMPLEMENTED;

  public HorasController(ProjectService projectService, MessageService messageService,
      SlackUserService slackUserService, TimeEntryService timeEntryService,
      RequestValidator validator, HistoryService historyService) {
    this.projectService = projectService;
    this.messageService = messageService;
    this.slackUserService = slackUserService;
    this.timeEntryService = timeEntryService;
    this.historyService = historyService;
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
    if (validator.validateSignature(requestTimestamp, body, requestSignature)) {
      SlackUser user = slackUserService.save(userId, apiKey, userName);
      if (user != null) {
        List<Project> projects = projectService.getAllProjects(user.getApiKey());
        slackUserService.updateUserProjects(user, projects);
        return API_KEY_SAVED;
      } else {
        return ALREADY_REGISTERED;
      }
    } else {
      return INVALID_SIGNATURE;
    }
  }

  @PostMapping("/fetch")
  public String fetch(@RequestParam("user_id") String userId) {
    logger.info("[POST /fetch] user_id={}", userId);
    SlackUser user = slackUserService.findOrFail(userId);
    List<Project> projects = projectService.getAllProjects(user.getApiKey());
    slackUserService.updateUserProjects(user, projects);

    return PROJECTS_UPDATED;
  }

  @PostMapping("/horas")
  @ResponseStatus(value = HttpStatus.OK)
  public void horas(
      @RequestParam("user_id") String userId,
      @RequestParam("text") String message,
      @RequestParam("response_url") String responseUrl,
      @RequestHeader("X-Slack-Request-Timestamp") String requestTimestamp,
      @RequestHeader("X-Slack-Signature") String requestSignature,
      @RequestBody String body) {
    logger.info("[POST /horas] user_id={}, message={}", userId, message);
    SlackMessageRequest tempMessage = new SlackMessageRequest.Builder(PROCESSIG_REQUEST).build();
    messageService.sendMessage(tempMessage, responseUrl);
    if (validator.validateSignature(requestTimestamp, body, requestSignature)) {
      try {
        SlackUser user = slackUserService.findOrFail(userId);

        if (user.getProjects().size() == 0) {
          messageService.sendMessage(messageService.getSimpleTextRequest(NO_PROJECTS), responseUrl);
        } else {
          SlackMessageRequest request = projectService.generateRequest(user.getProjects());
          timeEntryService.clearEntries(user.getId());
          timeEntryService.createTimeEntry(user, message);

          messageService.sendMessage(request, responseUrl);
        }
      } catch (NoResultException e) {
        messageService.sendMessage(messageService.getSimpleTextRequest(NOT_REGISTERED), responseUrl);
      }
    } else {
      messageService.sendMessage(messageService.getSimpleTextRequest(INVALID_SIGNATURE), responseUrl);
    }
  }

  @PostMapping("/request")
  @ResponseStatus(value = HttpStatus.OK)
  public void project(
      @RequestParam("payload") String message,
      @RequestHeader("X-Slack-Request-Timestamp") String requestTimestamp,
      @RequestHeader("X-Slack-Signature") String requestSignature,
      @RequestBody String body) {
    try {
      SlackOptionResponse optionResponse = timeEntryService.parsePayload(message);
      SlackUser slackUser = slackUserService.findOrFail(optionResponse.getUser().getId());
      TimeEntry timeEntry = timeEntryService.findTimeEntryBySlackUserId(slackUser.getId());
      Project project = projectService.getById(optionResponse.getActions().get(0).getValue());
      String selectedDate = optionResponse.getActions().get(0).getSelectedDate();

      if (selectedDate != null) {
        timeEntryService.clearEntries(slackUser.getId());
        timeEntry.setDate(timeEntryService.parseDateString(selectedDate));
        timeEntryService.save(timeEntry);
      } else {
        timeEntryService.saveHistory(timeEntry, project);

        ClockifyEntryRequest firstRequest = timeEntryService
            .generateFirstRequest(timeEntry, optionResponse);
        timeEntryService.sendRequest(firstRequest, slackUser.getApiKey());

        ClockifyEntryRequest secondRequest = timeEntryService
            .generateSecondRequest(timeEntry, optionResponse);
        timeEntryService.sendRequest(secondRequest, slackUser.getApiKey());

        timeEntryService.clearEntries(slackUser.getId());
        SlackMessageRequest request = validator.validateSignature(requestTimestamp, body, requestSignature) ?
            new SlackMessageRequest.Builder(ENTRY_SENT).build() :
            messageService.getSimpleTextRequest(INVALID_SIGNATURE);

        messageService.sendMessage(request, optionResponse.getResponseUrl());
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @PostMapping("/history")
  @ResponseStatus(value = HttpStatus.OK)
  public void history(@RequestParam("user_id") String userId,
      @RequestParam("response_url") String responseUrl) {
    logger.info("[POST /history] user_id={}", userId);
    SlackUser user = slackUserService.findOrFail(userId);
    SlackMessageRequest request = historyService.generateHistoryMessage(user);
    messageService.sendMessage(request, responseUrl);
  }

  @PostMapping("/month")
  @ResponseStatus(value = HttpStatus.OK)
  public void dias(@RequestParam("user_id") String userId,
      @RequestParam("response_url") String responseUrl) {
    logger.info("[POST /month] user_id={}", userId);
    SlackMessageRequest tempMessage = new SlackMessageRequest.Builder(PROCESSIG_REQUEST).build();
    messageService.sendMessage(tempMessage, responseUrl);
    SlackUser user = slackUserService.findOrFail(userId);
    List<Date> dates = timeEntryService.getDatesWithoutEntries(user);
    SlackMessageRequest request = new SlackMessageRequest.Builder(dates, 0).build();
    messageService.sendMessage(request, responseUrl);
  }
}
