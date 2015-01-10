package com.cosmicbirthday.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.DatePicker
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.BirthdaysFinder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scaloid.common._

class MainActivity extends SActivity {
  val context = this
  lazy val listView = new SListView()
  lazy val dateTextView = new STextView()

  def today = new DateTime()

  private var _dateOfBirth: Option[DateTime] = None

  def getDateOfBirth = _dateOfBirth

  def setDateOfBirth(year: Int, month: Int, dayOfMonth: Int): Unit = {
    val newDate: DateTime = new DateTime(year, month, dayOfMonth, today.getHourOfDay, today.getMinuteOfHour)
    _dateOfBirth = Some(newDate)
    updateUi(newDate)
  }

  def updateUi(dateOfBirth: DateTime) = {
    dateTextView.text = DateTimeFormat.longDate().print(dateOfBirth)
    val nextBirthdays = new BirthdaysFinder().findUpcomingBirthdays(dateOfBirth, today)
    listView.setAdapter(new BirthdayListAdapter(context, nextBirthdays.toArray))
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.date_of_birth_is).textSize(22.sp).<<.marginBottom(10).>>.gravity(Gravity.CENTER_HORIZONTAL).textColor(Color.WHITE)
      this += dateTextView.textSize(35.sp).gravity(Gravity.CENTER_HORIZONTAL).backgroundResource(R.drawable.date_text_shape).textColor(Color.WHITE)
        .onClick(askForDate())
      STextView(Html.fromHtml("<u>" + getString(R.string.change) + "</u>")).gravity(Gravity.CENTER_HORIZONTAL).textSize(18.sp).textColor(Color.CYAN)
        .onClick(askForDate())
      STextView(R.string.upcoming_birthdays).textSize(22.sp).<<.marginTop(25).marginBottom(10).>>.gravity(Gravity.CENTER_HORIZONTAL).textColor(Color.WHITE)
      this += listView
    }
  }

  onResume {
    getDateOfBirth match {
      case None => askForDate()
      case _ =>
    }
  }

  def askForDate() {
    val dialog = new DatePickerDialog(context, new OnDateSetListener {
      override def onDateSet(view: DatePicker, year: Int, monthZeroBased: Int, day: Int): Unit = {
        setDateOfBirth(year, monthZeroBased + 1, day)
      }
    },
      getDateOfBirth match { case None => 1982; case Some(date) => date.getYear},
      getDateOfBirth match { case None => 0; case Some(date) => date.getMonthOfYear - 1},
      getDateOfBirth match { case None => 0; case Some(date) => date.getDayOfMonth})
    dialog.setTitle(R.string.date_of_birth_picker_title)
    dialog.show()
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    getDateOfBirth match {
      case Some(date) =>
        outState.putInt("year", date.getYear)
        outState.putInt("month", date.getMonthOfYear)
        outState.putInt("dayOfMonth", date.getDayOfMonth)
      case _ =>
    }
  }

  override def onRestoreInstanceState(savedInstanceState: Bundle): Unit = {
    super.onRestoreInstanceState(savedInstanceState)
    val year = savedInstanceState.getInt("year", 0)
    if (year != 0) {
      setDateOfBirth(year, savedInstanceState.getInt("month"), savedInstanceState.getInt("dayOfMonth"))
    }
  }
}
