package com.cosmicbirthday.entities

import com.cosmicbirthday.dbentities.Person
import org.joda.time.{Duration, Period}

abstract class RelativeBirthday(description: BirthdayDescription) {
  def makeAbsolute(person: Person): AbsoluteBirthday
}

class PeriodBasedRelativeBirthday(val period: Period, description: BirthdayDescription)
  extends RelativeBirthday(description) {
  override def makeAbsolute(person: Person) = new AbsoluteBirthday(person, person.dateOfBirth.plus(period), description)
}

class DurationBasedRelativeBirthday(val duration: Duration, description: BirthdayDescription)
  extends RelativeBirthday(description) {
  override def makeAbsolute(person: Person) = new AbsoluteBirthday(person, person.dateOfBirth.plus(duration), description)
}
