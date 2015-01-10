package com.cosmicbirthday.calc

import com.cosmicbirthday.entities.BirthdayPair
import org.joda.time.DateTime


class BirthdaysFinder {
  private def findPairSurroundingDate(adjacentBirthdayPairs: Iterable[BirthdayPair], date: DateTime): Option[BirthdayPair] = {
    if (adjacentBirthdayPairs.head.previousBirthday.date.isAfter(date)) None
    else adjacentBirthdayPairs.find(pair => pair.nextBirthday.date.isAfter(date))
  }

  def findBirthdaysSurroundingToday(dateOfBirth: DateTime, today: DateTime): Iterable[BirthdayPair] = {
    val relativeBirthdayStreams = new RelativeBirthdayGenerator().periodStreams
    val birthdayStreams = relativeBirthdayStreams.map(_.map(_.makeAbsolute(dateOfBirth)))
    val adjacentPairStreams = birthdayStreams.map(stream =>
      (stream zip stream.tail).map { case (birthday1, birthday2) => new BirthdayPair(birthday1, birthday2)})
    val birthdaysSurroundingToday = adjacentPairStreams.map(pairs => findPairSurroundingDate(pairs, today))
    birthdaysSurroundingToday.flatten.sortBy(_.nextBirthday.date.getMillis)
  }
}
