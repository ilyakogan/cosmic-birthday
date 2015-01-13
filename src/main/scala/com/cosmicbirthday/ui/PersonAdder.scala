package com.cosmicbirthday.ui

import android.app.DatePickerDialog.OnDateSetListener
import android.app.{Activity, DatePickerDialog}
import android.content.Context
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import com.cosmicbirthday.R
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.{Person, Me}
import org.joda.time.DateTime
import org.scaloid.common._

trait AddOrEditPersonDialogTrait extends SActivity {
  this: Activity =>

  val defaultInitialDate = new DateTime(1982, 1, 1, 0, 0)

  def showAddOrEditFriendDialog(person: Option[Person], callback: (String, Int, Int, Int) => Unit) = {
    lazy val nameInput = new SEditText(person.map(_.name).getOrElse(""))
    lazy val datePicker = new SDatePicker()

    val initialDate = person.map(_.dateOfBirth).getOrElse(defaultInitialDate)

    val view = new SVerticalLayout {
      STextView(R.string.friend_name).textSize(22.sp).gravity(Gravity.CENTER_HORIZONTAL).<<.marginTop(7.sp)
      this += nameInput.textSize(22.sp).<<.margin(10.sp).>>
      this += datePicker
      datePicker.setCalendarViewShown(false)
      datePicker.updateDate(initialDate.getYear, initialDate.getMonthOfYear - 1, initialDate.getDayOfMonth)
    }

    new AlertDialogBuilder()
      .setView(view)
      .setTitle(if (person.isDefined) R.string.edit_person else R.string.add_friend)
      .setPositiveButton(android.R.string.ok, callback(nameInput.text.toString.trim, datePicker.getYear, datePicker.getMonth, datePicker.getDayOfMonth))
      .setNegativeButton(android.R.string.cancel, null)
      .show()

    if (!person.isDefined) {
      nameInput.requestFocus()
      val imm = getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
  }

  def showAddOrEditMyselfDialog(me: Option[Person], callback: (String, Int, Int, Int) => Unit) = {
    val initialDate = me.map(_.dateOfBirth).getOrElse(defaultInitialDate)

    val dialog = new DatePickerDialog(this, new OnDateSetListener {
      override def onDateSet(view: DatePicker, year: Int, monthZeroBased: Int, day: Int): Unit =
      // Workaround because gets fired twice
        if (view.isShown)
          callback(Me(), year, monthZeroBased, day)
    }, initialDate.getYear, initialDate.getMonthOfYear - 1, initialDate.getDayOfMonth)
    dialog.setTitle(R.string.when_were_you_born)
    dialog.show()
  }
}

class PersonAdder(activity: AddOrEditPersonDialogTrait, onPersonAdded: (() => Unit)) {
  def offerToAddFriend() =
    activity.showAddOrEditFriendDialog(None, addPerson)

  def offerToAddYourself() =
    activity.showAddOrEditMyselfDialog(None, addPerson)

  private def addPerson(name: String, year: Int, monthZeroBased: Int, day: Int) = {
    if (!name.isEmpty) { // todo: handle this && name != Me()) {
      val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
      new PeopleDataSource(activity).insertPerson(new Person(name, None, date))
      onPersonAdded()
    }
    else toast(activity.getString(R.string.no_name))(activity)
  }
}

class PersonEditor(activity: AddOrEditPersonDialogTrait, person: Person, onPersonEdited: (() => Unit)) {
  def offerToEditPerson() =
    if (person.isMe)
      activity.showAddOrEditMyselfDialog(Some(person), editPerson)
    else
      activity.showAddOrEditFriendDialog(Some(person), editPerson)

  private def editPerson(name: String, year: Int, monthZeroBased: Int, day: Int) = {
    if (!name.isEmpty) { // todo: handle this && name != Me()) {
      val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
      val newPerson = new Person(person.id, name, person.avatarUrl, date)
      new PeopleDataSource(activity).updatePerson(newPerson)
      onPersonEdited()
    }
    else toast(activity.getString(R.string.no_name))(activity)
  }

}
