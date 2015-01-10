package com.cosmicbirthday.entities

import org.joda.time.{DateTime, Period}

class RelativeBirthday(val period: Period, val number: Int, val mnemonic: String) {
  override def toString = number + " " + mnemonic
  def makeAbsolute(dateOfBirth: DateTime) = new Birthday(dateOfBirth.plus(period), number, mnemonic)
}
