package com.cosmicbirthday.dbentities

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class Person(val id: String, val name: String, val avatarUrl: Option[String], val dateOfBirthIso: String) {

    def this(id: String, name: String, avatarUrl: Option[String], dateOfBirth: DateTime) =
        this(id, name, avatarUrl, ISODateTimeFormat.dateTime().print(dateOfBirth))

    def this(name: String, avatarUrl: Option[String], dateOfBirth: DateTime) =
        this(UUID.randomUUID().toString, name, avatarUrl, dateOfBirth)

    def dateOfBirth = ISODateTimeFormat.dateTime().parseDateTime(dateOfBirthIso)

    def isMe = name == Person.Me
}

object Person {
    def Me = "me"
}
