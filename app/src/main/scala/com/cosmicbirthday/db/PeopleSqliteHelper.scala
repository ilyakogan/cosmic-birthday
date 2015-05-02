package com.cosmicbirthday.db

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.util.Log

object PeopleSqliteHelper {
    val dbName = "people.db"
    val dbVersion = 5
}

class PeopleSqliteHelper(context: Context)
    extends SQLiteOpenHelper(context, PeopleSqliteHelper.dbName, null, PeopleSqliteHelper.dbVersion) {
    private val createSql =
        "create table " + PeopleTable.tableName + " (" +
            " " + PeopleTable.Col.id + " text primary key, " +
            " " + PeopleTable.Col.name + " text not null, " +
            " " + PeopleTable.Col.avatarUrl + " text, " +
            " " + PeopleTable.Col.dateOfBirthIso + " text not null);"

    override def onCreate(db: SQLiteDatabase) = db.execSQL(createSql)

    override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {
        Log.w(classOf[PeopleSqliteHelper].getName,
            "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS " + PeopleTable.tableName)
        onCreate(db)
    }

}
