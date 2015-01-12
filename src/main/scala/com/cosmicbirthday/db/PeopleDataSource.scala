package com.cosmicbirthday.db

import android.content.{ContentValues, Context}
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.cosmicbirthday.dbentities.Person
import org.scaloid.common.RichCursor


class PeopleDataSource(context: Context) {

  private implicit def cursor2RichCursor(c: Cursor): RichCursor = new RichCursor(c)

  private def withDatabase[A](f: SQLiteDatabase => A) = {
    val dbHelper = new PeopleSqliteHelper(context)
    val database = dbHelper.getWritableDatabase
    try f(database)
    finally dbHelper.close()
  }

  def insertPerson(person: Person) = {
    withDatabase { database =>
      val values = new ContentValues()
      values.put(PeopleTableColumns.id, person.id)
      values.put(PeopleTableColumns.name, person.name)
      values.put(PeopleTableColumns.dateOfBirthIso, person.dateOfBirthIso)
      database.insert(PeopleTableColumns.tableName, null, values)
    }
  }

  def deletePerson(person: Person) {
    withDatabase { database =>
      database.delete(PeopleTableColumns.tableName, PeopleTableColumns.id + " = " + person.id, null)
    }
  }

  def getAll: List[Person] = {
    withDatabase { database =>
      database.query(PeopleTableColumns.tableName, PeopleTableColumns.allColumns, null, null, null, null, null).orm(
        c => new Person(c.getString(0), c.getString(1), c.getString(2)))
    }
  }
}
