package com.sant1g.horas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.model.TimeEntry;
import com.sant1g.horas.repository.TimeEntryRepository;
import com.sant1g.horas.request.ClockifyEntryRequest;
import com.sant1g.horas.response.SlackOptionResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TimeEntryService {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String START_DATE1 = "T12:00:00.000Z";
  private static final String END_DATE1 = "T16:00:00.000Z";
  private static final String START_DATE2 = "T17:00:00.000Z";
  private static final String END_DATE2 = "T21:00:00.000Z";

  @Value("${clockify.entry.url}")
  private String clockifyUrl;

  private TimeEntryRepository timeEntryRepository;

  public TimeEntryService(TimeEntryRepository timeEntryRepository) {
    this.timeEntryRepository = timeEntryRepository;
  }

  public TimeEntry findTimeEntryBySlackUserId(Long id) {
    return timeEntryRepository.findTimeEntryBySlackUserId(id);
  }

  public void createTimeEntry(SlackUser user, String message) {
    TimeEntry timeEntry = new TimeEntry();
    timeEntry.setMessage(message);
    timeEntry.setSlackUser(user);
    timeEntry.setDate(new Date());
    timeEntryRepository.save(timeEntry);
  }

  public void sendRequest(ClockifyEntryRequest request, String apiKey) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Api-Key", apiKey);

    HttpEntity<ClockifyEntryRequest> entity = new HttpEntity<>(request, headers);

    try {
      restTemplate.exchange(
          clockifyUrl,
          HttpMethod.POST,
          entity,
          new ParameterizedTypeReference<String>(){},
          headers);
    } catch (HttpClientErrorException e) {
      // TODO: Log Message
    }
  }

  @Transactional
  public void clearEntries(Long userId) {
    timeEntryRepository.deleteAllBySlackUserId(userId);
  }

  public ClockifyEntryRequest generateFirstRequest(TimeEntry timeEntry, SlackOptionResponse option) {
    return getClockifyEntryRequest(timeEntry, option, START_DATE2, END_DATE2);
  }

  public ClockifyEntryRequest generateSecondRequest(TimeEntry timeEntry, SlackOptionResponse option) {
    return getClockifyEntryRequest(timeEntry, option, START_DATE1, END_DATE1);
  }

  private ClockifyEntryRequest getClockifyEntryRequest(TimeEntry timeEntry,
      SlackOptionResponse option, String startDate2, String endDate2) {
    ClockifyEntryRequest request = new ClockifyEntryRequest();
    request.setDescription(timeEntry.getMessage());
    request.setProjectId(option.getActions().get(0).getValue());
    request.setStart(generateDate(timeEntry.getDate(), startDate2));
    request.setEnd(generateDate(timeEntry.getDate(), endDate2));

    return request;
  }

  public SlackOptionResponse parsePayload(String payload) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(payload, SlackOptionResponse.class);
  }

  private String generateDate(Date date, String concatString) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    return sdf.format(date).concat(concatString);
  }
}
