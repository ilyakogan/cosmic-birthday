package com.cosmicbirthday.db

import android.content.{ContentValues, Context}
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.cosmicbirthday.dbentities.Person
import org.scaloid.common.RichCursor


class PeopleDataSource(context: Context) {

  implicit def cursor2RichCursor(c: Cursor) : RichCursor = new RichCursor(c)

  private val dbHelper = new PeopleSqliteHelper(context)

  private var database : SQLiteDatabase = null

  def open() {
    database = dbHelper.getWritableDatabase
  }

  def close() {
    dbHelper.close()
  }

  def insertPerson(person: Person) = {
    val values = new ContentValues()
    values.put(PeopleTableColumns.id, person.id)
    values.put(PeopleTableColumns.name, person.name)
    values.put(PeopleTableColumns.dateOfBirthIso, person.dateOfBirthIso)
    database.insert(PeopleTableColumns.tableName, null, values)
  }

  def deletePerson(person: Person) {
    database.delete(PeopleTableColumns.tableName, PeopleTableColumns.id + " = " + person.id, null)
  }

  def getAll: List[Person] = {
    database.query(PeopleTableColumns.tableName, PeopleTableColumns.allColumns, null, null, null, null, null).orm(
      c => new Person(c.getString(0), c.getString(1), c.getString(2)))
  }
}
