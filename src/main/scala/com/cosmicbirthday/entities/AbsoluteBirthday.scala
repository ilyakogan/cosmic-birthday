package com.cosmicbirthday.entities

import org.joda.time.DateTime

class AbsoluteBirthday(val date: DateTime, description: BirthdayDescription) {
  def mnemonic = description.mnemonic
  def imageResource = description.imageResource
}
