package com.cosmicbirthday.db

object PeopleTable {
  val tableName = "people"
  val allColumns = Col.values.toArray.map(_.toString)

  object Col extends Enumeration {
    val id, name, avatarUrl, dateOfBirthIso = Value
  }

}
