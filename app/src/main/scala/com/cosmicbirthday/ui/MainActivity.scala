package com.cosmicbirthday.ui

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.view.MenuItem.OnMenuItemClickListener
import android.view.{MenuItem, View}
import android.widget._
import com.cosmicbirthday.R
import com.cosmicbirthday.calc.UpcomingBirthdayListBuilder
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.{Person}
import com.cosmicbirthday.entities.{AbsoluteBirthday, BirthdayItem, BirthdayListItem, SectionItem}
import com.facebook.AppEventsLogger
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scaloid.common._

class MainActivity extends SActivity with AddOrEditPersonDialogTrait {
    val context = this
    lazy val listView = find[ListView](R.id.listView)
    lazy val dateTextView = new STextView()

    def today = new DateTime().withTimeAtStartOfDay()

    val peopleDataSource = new PeopleDataSource(this)

    val personAdder = new PersonAdder(this, () => {
        showBirthdays(peopleDataSource.getAll)
        showOrHideAddMyBirthdayAction()
    })

    def showBirthdays(people: Seq[Person]) = {
        val items = new UpcomingBirthdayListBuilder().getBirthdayListItems(people, today)
        listView.setAdapter(new BirthdayListAdapter(context, items.toArray))
        listView.onItemClick(onItemClick(items) _)
    }

    def onItemClick(items: Seq[BirthdayListItem])(parent: AdapterView[_], view: View, position: Int, id: Long) = {
        items(position) match {
            case BirthdayItem(birthday) => showShareMenu(view, birthday)
            case SectionItem(_) =>
        }
    }

    def showShareMenu(anchorView: View, birthday: AbsoluteBirthday) {
        val menu = new PopupMenu(this, anchorView)
        menu.getMenuInflater.inflate(R.menu.share_menu, menu.getMenu)

        val shareItem = menu.getMenu.findItem(R.id.menu_item_share)
        val shareActionProvider = shareItem.getActionProvider.asInstanceOf[ShareActionProvider]
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener {
            override def onMenuItemClick(item: MenuItem): Boolean = {
                val intent = new Intent(android.content.Intent.ACTION_SEND)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_SUBJECT, new BirthdayFormatter(context).format(birthday))
                intent.putExtra(Intent.EXTRA_TEXT, DateTimeFormat.mediumDate().print(birthday.date))
                shareActionProvider.setShareIntent(intent)
                true
            }
        })

        val addToCalendarItem = menu.getMenu.findItem(R.id.menu_item_add_to_calendar)
        addToCalendarItem.setOnMenuItemClickListener(new OnMenuItemClickListener {
            override def onMenuItemClick(item: MenuItem): Boolean = {
              val title = new BirthdayFormatter(context).format(birthday)
              val intent = new CalendarIntentCreator().createIntentToInsertAllDayEvent(title, birthday.date)
              startActivity(intent)
              true
            }
        })

        menu.show()
    }

    onCreate {
        setContentView(R.layout.birthdays)
        find[Button](R.id.edit_friends).onClick(startActivity[PeopleActivity])
        find[Button](R.id.add_my_birthday).onClick(personAdder.offerToAddYourself())
    }

    onResume {
        val people = peopleDataSource.getAll
        if (!people.exists(p => p.isMe)) personAdder.offerToAddYourself()
        showBirthdays(people)
        showOrHideAddMyBirthdayAction()

        AppEventsLogger.activateApp(this)
    }

    onPause {
        AppEventsLogger.deactivateApp(this)
    }

    def showOrHideAddMyBirthdayAction() =
        find[Button](R.id.add_my_birthday).setVisibility(
            if (peopleDataSource.containsName(Person.Me)) View.GONE else View.VISIBLE)
}
