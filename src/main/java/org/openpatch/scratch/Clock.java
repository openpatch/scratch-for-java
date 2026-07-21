package org.openpatch.scratch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * The date and time of the computer the program runs on, the same values
 * Scratch's "current [year]" and "days since 2000" blocks report.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * this.setDirection(Clock.getSecond() * 6);
 * }</pre>
 *
 * @index-in-docs 5
 */
public final class Clock {

  private Clock() {
  }

  /**
   * Returns the current year, for example 2026.
   *
   * @return the year
   *
   * @scratchblock (current [year v])
   */
  public static int getYear() {
    return LocalDateTime.now().getYear();
  }

  /**
   * Returns the current month, from 1 for January to 12 for December.
   *
   * @return the month
   *
   * @scratchblock (current [month v])
   */
  public static int getMonth() {
    return LocalDateTime.now().getMonthValue();
  }

  /**
   * Returns the current day of the month, from 1 to 31.
   *
   * @return the day of the month
   *
   * @scratchblock (current [date v])
   */
  public static int getDay() {
    return LocalDateTime.now().getDayOfMonth();
  }

  /**
   * Returns the current day of the week, from 1 for Monday to 7 for Sunday.
   *
   * @return the day of the week
   *
   * @scratchblock (current [day of week v])
   */
  public static int getDayOfWeek() {
    return LocalDateTime.now().getDayOfWeek().getValue();
  }

  /**
   * Returns the current hour, from 0 to 23.
   *
   * @return the hour
   *
   * @scratchblock (current [hour v])
   */
  public static int getHour() {
    return LocalDateTime.now().getHour();
  }

  /**
   * Returns the current minute, from 0 to 59.
   *
   * @return the minute
   *
   * @scratchblock (current [minute v])
   */
  public static int getMinute() {
    return LocalDateTime.now().getMinute();
  }

  /**
   * Returns the current second, from 0 to 59.
   *
   * @return the second
   *
   * @scratchblock (current [second v])
   */
  public static int getSecond() {
    return LocalDateTime.now().getSecond();
  }

  /**
   * Returns the milliseconds of the current second, from 0 to 999.
   *
   * @return the millisecond
   */
  public static int getMillisecond() {
    return (int) Math.round(LocalDateTime.now().getNano() / 1000000.0);
  }

  /**
   * Returns the number of days that have passed since the first of January
   * 2000.
   *
   * @return the days since 2000
   *
   * @scratchblock (days since 2000)
   */
  public static int getDaysSince2000() {
    LocalDate then = LocalDate.of(2000, Month.JANUARY, 1);
    return (int) ChronoUnit.DAYS.between(then, LocalDate.now());
  }
}
