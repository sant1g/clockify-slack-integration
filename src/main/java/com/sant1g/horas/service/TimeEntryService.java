package com.sant1g.horas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sant1g.horas.model.ClockifyTimeEntry;
import com.sant1g.horas.model.History;
import com.sant1g.horas.model.Project;
import com.sant1g.horas.model.SlackUser;
import com.sant1g.horas.model.TimeEntry;
import com.sant1g.horas.repository.HistoryRepository;
import com.sant1g.horas.repository.TimeEntryRepository;
import com.sant1g.horas.request.ClockifyEntryRequest;
import com.sant1g.horas.response.SlackOptionResponse;
import com.sant1g.horas.util.DateComparator;
import com.sant1g.horas.util.Util;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

  @Value("${clockify.entries.url}")
  private String clockifyEntriesUrl;

  private TimeEntryRepository timeEntryRepository;
  private HistoryRepository historyRepository;

  public TimeEntryService(TimeEntryRepository timeEntryRepository,
      HistoryRepository historyRepository) {
    this.timeEntryRepository = timeEntryRepository;
    this.historyRepository = historyRepository;
  }

  public void save(TimeEntry timeEntry) {
    this.timeEntryRepository.save(timeEntry);
  }

  public TimeEntry findTimeEntryBySlackUserId(Long id) {
    return timeEntryRepository.findTimeEntryBySlackUserId(id);
  }

  public void saveHistory(TimeEntry entry, Project project) {
    History history = new History(entry, project);
    historyRepository.save(history);
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
          new ParameterizedTypeReference<String>() {
          },
          headers);
    } catch (HttpClientErrorException e) {
      // TODO: Log Message
    }
  }

  private List<ClockifyTimeEntry> getClockifyEntries(SlackUser user) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Api-Key", user.getApiKey());

    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    try {
      ResponseEntity<List<ClockifyTimeEntry>> response = restTemplate.exchange(
          clockifyEntriesUrl.concat(user.getClockifyId()).concat("/time-entries"),
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<List<ClockifyTimeEntry>>() {
          },
          headers);

      return response.getBody();
    } catch (HttpClientErrorException e) {
      return new ArrayList<>();
    }
  }

  @Transactional
  public void clearEntries(Long userId) {
    timeEntryRepository.deleteAllBySlackUserId(userId);
  }

  public ClockifyEntryRequest generateFirstRequest(TimeEntry timeEntry,
      SlackOptionResponse option) {
    return getClockifyEntryRequest(timeEntry, option, START_DATE2, END_DATE2);
  }

  public ClockifyEntryRequest generateSecondRequest(TimeEntry timeEntry,
      SlackOptionResponse option) {
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

  public Date parseDateString(String date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT).parse(date);
  }

  public List<Date> getDatesWithoutEntries(SlackUser user) {
    List<ClockifyTimeEntry> entries = this.getClockifyEntries(user);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<Date> dates = new ArrayList<>();
    entries.forEach(entry -> {
      try {
        dates.add(sdf.parse(entry.getTimeInterval().getStart()));
      } catch (ParseException e) {
        // TODO: Handle this
      }
    });

    return Util.getWeekDaysFromMonth().stream()
        .filter(day -> Collections.binarySearch(dates, day, new DateComparator()) < 0)
        .collect(Collectors.toList());
  }
}
