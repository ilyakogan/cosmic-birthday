package com.cosmicbirthday.ui
import android.widget.ArrayAdapter
import com.cosmicbirthday.db.PeopleDataSource
import com.cosmicbirthday.dbentities.Person
import org.joda.time.DateTime
import org.scaloid.common.{SActivity, SButton, SListView, SVerticalLayout}

import scala.collection.JavaConverters._

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
    dataSource.open()
    reloadList
  }

  def reloadList {
    val people = dataSource.getAll
    val adapter = new ArrayAdapter[String](this, android.R.layout.simple_list_item_1, people.map(p => p.name + " " + p.dateOfBirth).toList.asJava)
    listView.setAdapter(adapter)
  }

  onPause {
    dataSource.close()
  }
}
