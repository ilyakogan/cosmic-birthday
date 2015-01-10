package com.cosmicbirthday.entities

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class Birthday(val date: DateTime, val number: Int, val mnemonic: String) {
  override def toString = DateTimeFormat.shortDate().print(date) + ": " + number + " " + mnemonic
}
