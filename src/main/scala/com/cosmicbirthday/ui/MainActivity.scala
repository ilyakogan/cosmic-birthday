package com.cosmicbirthday.ui

import android.widget.{Button, ListView}
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.UpcomingBirthdayListBuilder
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime
import org.scaloid.common._

class MainActivity extends SActivity with AddOrEditPersonDialogTrait {
  val context = this
  lazy val listView = find[ListView](R.id.listView)
  lazy val dateTextView = new STextView()

  def today = new DateTime().withTimeAtStartOfDay()

  val peopleDataSource = new PeopleDataSource(this)

  val personAdder = new PersonAdder(this, () => showBirthdays(peopleDataSource.getAll))

  def showBirthdays(people: Seq[Person]) = {
    val items = new UpcomingBirthdayListBuilder().getBirthdayListItems(people, today)
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
