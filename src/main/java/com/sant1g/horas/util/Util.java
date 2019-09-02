package com.sant1g.horas.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

  public static List<Date> getWeekDaysFromMonth() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    LocalDate end = LocalDate.now();
    //LocalDate end = LocalDate.of(2019, 9, 30);
    LocalDate start = LocalDate.of(end.getYear(), end.getMonth(), 1);
    List<LocalDate> localDates = getDatesBetween(start, end);
    List<Date> dates = transformLocalDates(localDates);

    return dates.stream().filter(Util::isWeekDay).collect(Collectors.toList());
  }

  private static Boolean isWeekDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return !(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
  }

  private static List<Date> transformLocalDates(List<LocalDate> localDates) {
    ArrayList<Date> dates = new ArrayList<>();
    localDates.forEach(localDate -> dates
        .add(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
    return dates;
  }

  private static List<LocalDate> getDatesBetween(LocalDate startDate,
      LocalDate endDate) {
    List<LocalDate> dates = startDate.datesUntil(endDate)
        .collect(Collectors.toList());
    dates.add(endDate);

    return dates;
  }
}
