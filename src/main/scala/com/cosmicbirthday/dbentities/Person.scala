package com.cosmicbirthday.dbentities

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class Person(val id: String, val name: String, val dateOfBirthIso: String) {
  def this(name: String, dateOfBirth: DateTime) =
    this(UUID.randomUUID().toString, name, ISODateTimeFormat.dateTime().print(dateOfBirth))

  def dateOfBirth = ISODateTimeFormat.dateTime().parseDateTime(dateOfBirthIso)
}

object Me
{
  def apply(ignored: Unit) = "me"
  def unapply(str: String) = str == "me"
}
