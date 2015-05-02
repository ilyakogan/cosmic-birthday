package com.cosmicbirthday.calc

import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.{AbsoluteBirthday, BirthdayItem, BirthdayListItem, SectionItem}
import org.joda.time.DateTime

class Section(val title: String, val maxDate: DateTime)

class UpcomingBirthdayListBuilder {
    private val maxBirthdaysPerStream = 1

    def getBirthdayListItems(people: Seq[Person], today: DateTime): List[BirthdayListItem] = {
        val sections = makeSections(today)
        val nextBirthdays = getUpcomingBirthdays(people, today)
        val sectionsWithBirthdays = groupBirthdaysIntoSections(sections, nextBirthdays)
        buildListItems(sectionsWithBirthdays)
    }

    private def getUpcomingBirthdays(people: Seq[Person], today: DateTime): Seq[AbsoluteBirthday] = {
        val birthdayStreams = new UpcomingBirthdayFinder().getUpcomingBirthdayStreams(people, today)
        val nextBirthdays = birthdayStreams.flatMap(_.take(maxBirthdaysPerStream))
        nextBirthdays
    }

    private def makeSections(today: DateTime): List[Section] = {
        val endOfThisMonth = today.withDayOfMonth(1).plusMonths(1)
        val endOfNextMonth = endOfThisMonth.plusMonths(1)

        val endsOfMonths = Range(1, 36).map(x => today.plusWeeks(1).withDayOfMonth(1).plusMonths(x))
        val monthSections = endsOfMonths.map(endOfMonth => {
            val lastDayOfMonth = endOfMonth.minusDays(1)
            val title =
                if (endOfMonth == endOfThisMonth) "THIS MONTH"
                else if (endOfMonth == endOfNextMonth) "NEXT MONTH"
                else if (lastDayOfMonth.getYear == today.getYear) lastDayOfMonth.toString("MMMM").toUpperCase
                else lastDayOfMonth.toString("MMMM yyyy").toUpperCase
            new Section(title, endOfMonth)
        })

        List(
            new Section("TODAY", today.plusDays(1)),
            new Section("THIS WEEK", today.plusWeeks(1))) ++
            monthSections
    }

    private def groupBirthdaysIntoSections(sections: List[Section], nextBirthdays: Seq[AbsoluteBirthday]): List[(Section, Seq[AbsoluteBirthday])] = {
        val sectionsByBirthday = nextBirthdays.map(
            b => (b, sections.find(section => b.date.isBefore(section.maxDate)))).toMap
        val sectionsWithBirthdays = sections.flatMap(section => {
            val birthdaysInSection = nextBirthdays.filter(b => sectionsByBirthday(b) == Some(section))
            if (birthdaysInSection.isEmpty) None else Some((section, birthdaysInSection))
        })
        sectionsWithBirthdays
    }

    private def buildListItems(sectionsWithBirthdays: List[(Section, Seq[AbsoluteBirthday])]): List[BirthdayListItem] = {
        sectionsWithBirthdays.map {
            case (section, birthdays) =>
                List(SectionItem(section.title)) ++
                    birthdays.sortBy(b => b.date.getMillis).map(b => BirthdayItem(b))
        }.reduce(_ ++ _)
    }
}
