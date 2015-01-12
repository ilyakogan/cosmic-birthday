package com.cosmicbirthday.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.text.Html
import android.view.Gravity
import android.widget.DatePicker
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.BirthdaysFinder
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.{Me, Person}
import org.joda.time.DateTime
import org.scaloid.common._

class MainActivity extends SActivity {
  val context = this
  lazy val listView = new SListView()
  lazy val dateTextView = new STextView()

  def today = new DateTime()

  val peopleDataSource = new PeopleDataSource(this)

  def showBirthdays(people: Seq[Person]) = {
    val nextBirthdays = new BirthdaysFinder().findUpcomingBirthdays(people, today)
    listView.setAdapter(new BirthdayListAdapter(context, nextBirthdays.toArray))
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(Html.fromHtml("<u>" + getString(R.string.add_friend) + "</u>")).gravity(Gravity.CENTER_HORIZONTAL).textSize(20.sp).textColor(Color.CYAN)
        .onClick(askForFriendsDateOfBirth())
      STextView(R.string.upcoming_birthdays).textSize(22.sp).<<.marginTop(25).marginBottom(10).>>.gravity(Gravity.CENTER_HORIZONTAL).textColor(Color.WHITE)
      this += listView
    }
  }

  onResume {
    val people = peopleDataSource.getAll
    if (!people.exists(p => p.name == Me())) askForMyDateOfBirth()
    else showBirthdays(people)
  }

  def askForMyDateOfBirth() {
    val dialog = new DatePickerDialog(context, new OnDateSetListener {
      override def onDateSet(view: DatePicker, year: Int, monthZeroBased: Int, day: Int): Unit = {
        // Workaround because gets fired twice
        if (!view.isShown) return

        val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
        peopleDataSource.insertPerson(new Person(Me(), date))
        showBirthdays(peopleDataSource.getAll)
      }
    }, 1982, 0, 0)
    dialog.setTitle(R.string.when_were_you_born)
    dialog.show()
  }

  def askForFriendsDateOfBirth(): Unit = {
    val dialog = new DatePickerDialog(context, new OnDateSetListener {
      override def onDateSet(view: DatePicker, year: Int, monthZeroBased: Int, day: Int): Unit = {
        // Workaround because gets fired twice
        if (!view.isShown) return

        val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
        peopleDataSource.insertPerson(new Person("Friend " + Math.random(), date))
        showBirthdays(peopleDataSource.getAll)
      }
    }, 1982, 0, 0)
    dialog.setTitle(R.string.add_friend)
    dialog.show()
  }
}
