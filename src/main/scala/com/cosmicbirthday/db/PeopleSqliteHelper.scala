package com.cosmicbirthday.db

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.util.Log

object DbProperties {
  val dbName = "people.db"
  val dbVersion = 2
}

object PeopleTableColumns {
  val tableName = "people"
  val id = "id"
  val name = "name"
  val dateOfBirthIso = "dateOfBirthIso"

  def allColumns = Array(id, name, dateOfBirthIso)
}

class PeopleSqliteHelper(context: Context) extends SQLiteOpenHelper(context, DbProperties.dbName, null, DbProperties.dbVersion) {
  private val createSql =
    "create table " + PeopleTableColumns.tableName + " (" +
    " " + PeopleTableColumns.id + " text primary key, " +
    " " + PeopleTableColumns.name + " text not null, " +
    " " + PeopleTableColumns.dateOfBirthIso + " text not null);"

  override def onCreate(db: SQLiteDatabase) = db.execSQL(createSql)

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {
    Log.w(classOf[PeopleSqliteHelper].getName,
      "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data")
    db.execSQL("DROP TABLE IF EXISTS " + PeopleTableColumns.tableName)
    onCreate(db)
  }

}
