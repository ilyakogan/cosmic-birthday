package com.cosmicbirthday.calc

import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.AbsoluteBirthday
import org.joda.time.DateTime


class BirthdaysFinder {
  private def findBirthdaysInRange(birthdays: Iterable[AbsoluteBirthday], from: DateTime, to: DateTime, maxBirthdaysPerStream: Int) = {
    birthdays
      .dropWhile(b => b.date.isBefore(from))
      .takeWhile(b => b.date.isBefore(to))
      .take(maxBirthdaysPerStream)
  }

  def findUpcomingBirthdays(people: Seq[Person], today: DateTime): Iterable[AbsoluteBirthday] = {
    val relativeBirthdayStreams = new BirthdayData().periodStreams

    val birthdayStreams = people.flatMap(person => relativeBirthdayStreams.map(_.map(_.makeAbsolute(person))))
    val nextBirthdays = birthdayStreams.flatMap(stream => findBirthdaysInRange(stream, today, today.plusYears(5), 2))
    nextBirthdays.sortBy(_.date.getMillis)
  }
}
