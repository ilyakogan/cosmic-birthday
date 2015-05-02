package com.cosmicbirthday.calc

import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.AbsoluteBirthday
import org.joda.time.DateTime


class UpcomingBirthdayFinder {
    private def findBirthdaysInRange(birthdays: Stream[AbsoluteBirthday], from: DateTime, maxBirthdaysPerStream: Int) = {
        birthdays
            .dropWhile(b => b.date.isBefore(from))
            .take(maxBirthdaysPerStream)
    }

    def getUpcomingBirthdayStreams(people: Seq[Person], today: DateTime): Seq[Stream[AbsoluteBirthday]] = {
        val relativeBirthdayStreams = new BirthdayData().periodStreams

        val birthdayStreams = people.flatMap(person => relativeBirthdayStreams.map(_.map(_.makeAbsolute(person))))
        birthdayStreams.map(stream => findBirthdaysInRange(stream, today, 2))
    }
}
