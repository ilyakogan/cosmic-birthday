package com.cosmicbirthday.entities

class BirthdayListItem

case class BirthdayItem(birthday: AbsoluteBirthday) extends BirthdayListItem

case class SectionItem(text: String) extends BirthdayListItem
