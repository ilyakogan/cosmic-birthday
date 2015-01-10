package com.cosmicbirthday.calc

import com.cosmicbirthday.entities.AbsoluteBirthday
import org.joda.time.DateTime


class BirthdaysFinder {
  private def findBirthdaysInRange(birthdays: Iterable[AbsoluteBirthday], from: DateTime, to: DateTime, maxBirthdaysPerStream: Int) = {
    birthdays
      .dropWhile(b => b.date.isBefore(from))
      .takeWhile(b => b.date.isBefore(to))
      .take(maxBirthdaysPerStream)
  }

  def findUpcomingBirthdays(dateOfBirth: DateTime, today: DateTime): Iterable[AbsoluteBirthday] = {
    val relativeBirthdayStreams = new BirthdayData().periodStreams
    val birthdayStreams = relativeBirthdayStreams.map(_.map(_.makeAbsolute(dateOfBirth)))
    val nextBirthdays = birthdayStreams.map(stream => findBirthdaysInRange(stream, today, today.plusYears(5), 2))
    nextBirthdays.flatten.sortBy(_.date.getMillis)
  }
}
