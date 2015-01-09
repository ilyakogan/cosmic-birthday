package com.cosmicbirthday.ui

import android.view.Gravity
import android.widget.{ArrayAdapter, DatePicker}
import com.cosmicbirthday.R
import org.scaloid.common._

class MainActivity extends SActivity{
  val context = this
  lazy val listView = new SListView()
  lazy val datePicker = new SDatePicker()

  var onDateChangedListener = new DatePicker.OnDateChangedListener {
    override def onDateChanged(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int): Unit =
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, Array(dayOfMonth + "/" + monthOfYear + "/" + year)))
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.pick_birthday).gravity(Gravity.CENTER_HORIZONTAL)
      datePicker.setCalendarViewShown(false)
      datePicker.init(1982,1,1,onDateChangedListener)
      this += datePicker
      this += listView
    }
  }
}
