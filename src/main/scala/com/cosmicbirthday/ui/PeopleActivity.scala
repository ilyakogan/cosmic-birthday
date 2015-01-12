package com.cosmicbirthday.ui
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime
import org.scaloid.common.{SActivity, SButton, SListView, SVerticalLayout}

class PeopleActivity extends SActivity {

  val dataSource = new PeopleDataSource(this)

  lazy val listView = new SListView()

  onCreate {
    contentView = new SVerticalLayout {
      SButton("Add new").onClick({
        dataSource.insertPerson(new Person("david", new DateTime()))
        reloadList
      })
      this += listView
    }
  }

  onResume {
    reloadList
  }

  def reloadList {
    val people = dataSource.getAll
    val adapter = new PeopleListAdapter(this, people.toArray)
    listView.setAdapter(adapter)
  }
}
