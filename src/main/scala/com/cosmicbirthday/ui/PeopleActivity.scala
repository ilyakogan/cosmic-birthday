package com.cosmicbirthday.ui

import android.view.{MenuItem, View}
import android.widget.{AdapterView, PopupMenu}
import com.cosmicbirthday.R
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.scaloid.common.{SActivity, SButton, SListView, SVerticalLayout}

class PeopleActivity extends SActivity with AddOrEditPersonDialogTrait {
  val activity = this
  val dataSource = new PeopleDataSource(this)

  val personAdder = new PersonAdder(this, () => reloadList())
  lazy val listView = new SListView()

  onCreate {
    contentView = new SVerticalLayout {
      SButton(getString(R.string.add_friend)).onClick(personAdder.offerToAddFriend())
      this += listView
      listView.onItemClick(onPersonClick _)
      getActionBar.setDisplayHomeAsUpEnabled(true)
    }
  }

  onResume {
    reloadList()
  }

  def reloadList() {
    val people = dataSource.getAll
    val adapter = new PeopleListAdapter(this, people.toArray)
    listView.setAdapter(adapter)
  }

  def onPersonClick(parent: AdapterView[_], view: View, position: Int, id: Long) = {
    val personId = view.getTag(R.id.TAG_PERSON_ID).asInstanceOf[String]
    val person = dataSource.getById(personId)
    person match {
      case Some(p) => showPersonPopupMenu(view, p)
      case None =>
    }
  }

  def showPersonPopupMenu(menuAnchor: View, person: Person) {
    val popupMenu = new PopupMenu(this, menuAnchor)
    val menu = popupMenu.getMenu
    menu.clear()
    menu.add(R.string.edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener {
      override def onMenuItemClick(item: MenuItem): Boolean = {
        new PersonEditor(activity, person, () => reloadList()).offerToEditPerson()
        true
      }
    })
    if (!person.isMe)
      menu.add(R.string.delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener {
        override def onMenuItemClick(item: MenuItem): Boolean = {
          dataSource.deletePerson(person)
          reloadList()
          true
        }
      })
    popupMenu.show()
  }
}
