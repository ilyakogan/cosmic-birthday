package com.cosmicbirthday.entities

import org.joda.time.{Duration, DateTime, Period}

abstract class RelativeBirthday(description: BirthdayDescription) {
  def makeAbsolute(dateOfBirth: DateTime): AbsoluteBirthday
}

class PeriodBasedRelativeBirthday(val period: Period, description: BirthdayDescription)
  extends RelativeBirthday(description) {
  override def makeAbsolute(dateOfBirth: DateTime) = new AbsoluteBirthday(dateOfBirth.plus(period), description)
}

class DurationBasedRelativeBirthday(val duration: Duration, description: BirthdayDescription)
  extends RelativeBirthday(description) {
  override def makeAbsolute(dateOfBirth: DateTime) = new AbsoluteBirthday(dateOfBirth.plus(duration), description)
}
