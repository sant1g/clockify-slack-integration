package com.sant1g.horas.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Date> {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

  public int compare(Date d1, Date d2) {
    return DATE_FORMAT.format(d1).compareTo(DATE_FORMAT.format(d2));
  }
}
