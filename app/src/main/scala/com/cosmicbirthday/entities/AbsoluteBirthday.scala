package com.cosmicbirthday.entities

import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime

class AbsoluteBirthday(val person: Person, val date: DateTime, description: BirthdayDescription) {
    def mnemonic = description.mnemonic
    def imageResource = description.imageResource
}
