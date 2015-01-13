package com.cosmicbirthday.ui

import android.widget.{Button, ListView}
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.BirthdaysFinder
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.{BirthdayItem, SectionItem}
import org.joda.time.DateTime
import org.scaloid.common._

class MainActivity extends SActivity with AddOrEditPersonDialogTrait {
  val context = this
  lazy val listView = find[ListView](R.id.listView)
  lazy val dateTextView = new STextView()

  def today = new DateTime().withTimeAtStartOfDay()

  val peopleDataSource = new PeopleDataSource(this)

  val personAdder = new PersonAdder(this, () => showBirthdays(peopleDataSource.getAll))

  class Section(val title: String, val maxDate: DateTime)

  def showBirthdays(people: Seq[Person]) = {
    val nextBirthdays = new BirthdaysFinder().findUpcomingBirthdays(people, today)
    val sections = List(new Section("TODAY", today.plusDays(1)),
      new Section("THIS WEEK", today.plusWeeks(1)),
      new Section("THIS MONTH", today.plusMonths(1)),
      new Section("THIS YEAR", today.plusYears(1)),
      new Section("NEXT FEW YEARS", today.plusYears(5)))
    val sectionsByBirthday = nextBirthdays.map(
      b => (b, sections.find(section => b.date.isBefore(section.maxDate)))).toMap
    val items = sections.flatMap(section => {
      val birthdaysInSection = nextBirthdays.filter(b => sectionsByBirthday(b) == Some(section))
      if (birthdaysInSection.isEmpty) None else Some((section, birthdaysInSection))
    }).map {
      case (section, birthdays) => List(SectionItem(section.title)) ++ birthdays.map(b => BirthdayItem(b))
    }.reduce(_ ++ _)
    listView.setAdapter(new BirthdayListAdapter(context, items.toArray))
  }

  onCreate {
    setContentView(R.layout.birthdays)
    find[Button](R.id.add_friend).onClick(personAdder.offerToAddFriend())
    find[Button](R.id.edit_friends).onClick(startActivity[PeopleActivity])
  }

  onResume {
    val people = peopleDataSource.getAll
    if (!people.exists(p => p.isMe)) personAdder.offerToAddYourself()
    else showBirthdays(people)
  }
}
