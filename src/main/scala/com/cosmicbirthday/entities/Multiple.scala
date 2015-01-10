package com.cosmicbirthday.entities

class Multiple(val number: Int, val alias: String) {
  def this(number: Int) = this(number, number.toString)
}

