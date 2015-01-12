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
      override def onDateSet(view: DatePicker, year: Int, monthZeroBased: Int, day: Int): Unit =
      // Workaround because gets fired twice
        if (view.isShown)
          addFriend(Me(), year, monthZeroBased, day)
    }, 1982, 0, 0)
    dialog.setTitle(R.string.when_were_you_born)
    dialog.show()
  }

  def addFriend(name: String, year: Int, monthZeroBased: Int, day: Int) = {
    if (!name.isEmpty) {
      val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
      peopleDataSource.insertPerson(new Person(name, date))
      showBirthdays(peopleDataSource.getAll)
    }
    else toast(R.string.no_name)
  }

  def askForFriendsDateOfBirth(): Unit = {
    lazy val friendName = new SEditText()
    lazy val datePicker = new SDatePicker()
    new AlertDialogBuilder()
      .setView(
        new SVerticalLayout {
          STextView(R.string.friend_name).textSize(22.sp).gravity(Gravity.CENTER_HORIZONTAL)
          this += friendName
          this += datePicker
          datePicker.setCalendarViewShown(false)
        }
      )
      .setTitle(R.string.add_friend)
      .setPositiveButton(android.R.string.ok, addFriend(friendName.text.toString.trim, datePicker.getYear, datePicker.getMonth, datePicker.getDayOfMonth))
      .setNegativeButton(android.R.string.cancel, null)
      .show()
  }
}
