package com.cosmicbirthday.ui

import android.graphics.Color
import android.text.Html
import android.view.Gravity
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.BirthdaysFinder
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime
import org.scaloid.common._

class MainActivity extends SActivity with AddOrEditPersonDialogTrait {
  val context = this
  lazy val listView = new SListView()
  lazy val dateTextView = new STextView()

  def today = new DateTime().withTimeAtStartOfDay()

  val peopleDataSource = new PeopleDataSource(this)

  val personAdder = new PersonAdder(this, () => showBirthdays(peopleDataSource.getAll))

  def showBirthdays(people: Seq[Person]) = {
    val nextBirthdays = new BirthdaysFinder().findUpcomingBirthdays(people, today)
    listView.setAdapter(new BirthdayListAdapter(context, nextBirthdays.toArray))
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(Html.fromHtml("<u>" + getString(R.string.add_friend) + "</u>")).gravity(Gravity.CENTER_HORIZONTAL).textSize(20.sp).textColor(Color.CYAN)
        .onClick(personAdder.offerToAddFriend())
      STextView(Html.fromHtml("<u>Edit friends</u>")).gravity(Gravity.CENTER_HORIZONTAL).textSize(20.sp).textColor(Color.CYAN)
        .onClick(startActivity[PeopleActivity])
      STextView(R.string.upcoming_birthdays).textSize(22.sp).<<.marginTop(25).marginBottom(10).>>.gravity(Gravity.CENTER_HORIZONTAL).textColor(Color.WHITE)
      this += listView
    }
  }

  onResume {
    val people = peopleDataSource.getAll
    if (!people.exists(p => p.isMe)) personAdder.offerToAddYourself()
    else showBirthdays(people)
  }
}
