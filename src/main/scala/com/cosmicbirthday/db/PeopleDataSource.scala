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

  def insertPerson(person: Person) =
    withDatabase { database =>
      database.insert(PeopleTable.tableName, null, createValues(person))
    }

  def deletePerson(person: Person) =
    withDatabase { database =>
      database.delete(PeopleTable.tableName, getQueryById(person.id), null)
    }

  def updatePerson(person: Person) =
    withDatabase { database =>
      database.update(PeopleTable.tableName, createValues(person), getQueryById(person.id), null)
    }

  def getAll: List[Person] =
    select(null)

  def getById(id: String) =
    select(getQueryById(id)).headOption

  def containsName(name: String) =
    select(PeopleTable.Col.name + " = '" + name + "'").nonEmpty

  private def getQueryById(id: String) =
    PeopleTable.Col.id + " = '" + id + "'"

  private def select(query: String): List[Person] =
    withDatabase { database =>
      database.query(PeopleTable.tableName, PeopleTable.allColumns, query, null, null, null, null).orm(
        c => new Person(c.getString(0), c.getString(1), Option(c.getString(2)), c.getString(3)))
    }

  private def createValues(person: Person): ContentValues = {
    val values = new ContentValues()
    values.put(PeopleTable.Col.id.toString, person.id)
    values.put(PeopleTable.Col.name.toString, person.name)
    person.avatarUrl.foreach(avatarUrl => values.put(PeopleTable.Col.avatarUrl.toString, avatarUrl))
    values.put(PeopleTable.Col.dateOfBirthIso.toString, person.dateOfBirthIso)
    values
  }
}
