package com.cosmicbirthday.ui

import android.app.DatePickerDialog.OnDateSetListener
import android.app.{Activity, DatePickerDialog}
import android.content.{Context, DialogInterface}
import android.view.inputmethod.InputMethodManager
import android.view.{Gravity, View}
import android.widget.DatePicker
import com.cosmicbirthday.R
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime
import org.scaloid.common._

import scala.util.{Failure, Success, Try}

trait AddOrEditPersonDialogTrait extends SActivity {
  this: Activity =>

  val thisActivity = this

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

    def onOkClicked() = callback(nameInput.text.toString.trim, datePicker.getYear, datePicker.getMonth, datePicker.getDayOfMonth)

    val dialog = new AlertDialogBuilder()
      .setView(view)
      .setTitle(if (person.isDefined) R.string.edit_person else R.string.add_person)
      .setPositiveButton(android.R.string.ok, null)
      .setNegativeButton(android.R.string.cancel, null)
      .create()

    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      override def onShow(dialogInterface: DialogInterface) = {
        val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        button.setOnClickListener(new View.OnClickListener() {
          override def onClick(view: View) =
            Try(onOkClicked()) match {
              case Success(_) => dialog.dismiss()
              case Failure(ex) => toast(ex.getMessage)(thisActivity)
            }
        })
      }
    })

    dialog.show()

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
          Try(callback(Person.Me, year, monthZeroBased, day))
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
    require(name.nonEmpty, activity.getString(R.string.no_name))
    require(name != Person.Me, activity.getString(R.string.name_already_exists))
    val peopleDataSource = new PeopleDataSource(activity)
    require(!peopleDataSource.containsName(name), activity.getString(R.string.name_already_exists))

    val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
    peopleDataSource.insertPerson(new Person(name, None, date))
    onPersonAdded()
  }
}

class PersonEditor(activity: AddOrEditPersonDialogTrait, person: Person, onPersonEdited: (() => Unit)) {
  def offerToEditPerson() =
    if (person.isMe)
      activity.showAddOrEditMyselfDialog(Some(person), editPerson)
    else
      activity.showAddOrEditFriendDialog(Some(person), editPerson)

  private def editPerson(name: String, year: Int, monthZeroBased: Int, day: Int) = {
    require(name.nonEmpty, activity.getString(R.string.no_name))
    val peopleDataSource = new PeopleDataSource(activity)
    if (name != person.name) {
      require(name != Person.Me, activity.getString(R.string.name_already_exists))
      require(!peopleDataSource.containsName(name), activity.getString(R.string.name_already_exists))
    }

    val date = new DateTime(year, monthZeroBased + 1, day, 0, 0)
    val newPerson = new Person(person.id, name, person.avatarUrl, date)
    peopleDataSource.updatePerson(newPerson)
    onPersonEdited()
  }

  final def require(requirement: Boolean, message: String) {
    if (!requirement)
      throw new IllegalArgumentException(message)
  }
}
