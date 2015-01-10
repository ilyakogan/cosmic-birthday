package com.cosmicbirthday.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.{ArrayAdapter, DatePicker}
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.BirthdaysFinder
import org.joda.time.DateTime
import org.scaloid.common._

class MainActivity extends SActivity {
  val context = this
  lazy val listView = new SListView()
  lazy val datePicker = new SDatePicker()

  var onDateChangedListener = new DatePicker.OnDateChangedListener {
    override def onDateChanged(view: DatePicker, year: Int, monthOfYearZeroBased: Int, dayOfMonth: Int): Unit = {
      val today = new DateTime()
      val dateOfBirth = new DateTime(year, monthOfYearZeroBased + 1, dayOfMonth, today.getHourOfDay, today.getMinuteOfHour)
      val nextBirthdays = new BirthdaysFinder().findBirthdaysSurroundingToday(dateOfBirth, today).map(_.nextBirthday)
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, nextBirthdays.toArray))
    }
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.pick_birthday).gravity(Gravity.CENTER_HORIZONTAL)
      datePicker.setCalendarViewShown(false)
      datePicker.init(1982, 0, 1, onDateChangedListener)
      this += datePicker
      this += listView
    }
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    outState.putInt("year", datePicker.year)
    outState.putInt("monthZeroBased", datePicker.month)
    outState.putInt("dayOfMonth", datePicker.dayOfMonth)
  }

  override def onRestoreInstanceState(savedInstanceState: Bundle): Unit = {
    super.onRestoreInstanceState(savedInstanceState)
    datePicker.updateDate(savedInstanceState.getInt("year"), savedInstanceState.getInt("monthZeroBased"), savedInstanceState.getInt("dayOfMonth"))
  }
}
