package com.cosmicbirthday.ui

import android.content.Context
import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.AbsoluteBirthday

class BirthdayFormatter(context: Context) {

    def format(birthday: AbsoluteBirthday) = {
        if (birthday.person.name == Person.Me)
            context.getString(R.string.format_your_age, birthday.mnemonic)
        else
            context.getString(R.string.format_friend_age, birthday.person.name, birthday.mnemonic)
    }
}