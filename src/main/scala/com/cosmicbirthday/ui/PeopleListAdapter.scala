package com.cosmicbirthday.ui

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, ImageView, TextView}
import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Person
import org.joda.time.format.DateTimeFormat

class PeopleListAdapter(val context: Context, val values: Array[Person])
  extends ArrayAdapter[Person](context, R.layout.birthday_list_row, values) {
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
    val rowView =
      if (convertView == null) inflater.inflate(R.layout.person_list_row, parent, false)
      else convertView

    val person = values(position)

    val nameTextView = rowView.findViewById(R.id.name).asInstanceOf[TextView]
    val dateTextView = rowView.findViewById(R.id.date).asInstanceOf[TextView]
    val imageView = rowView.findViewById(R.id.thumbnail).asInstanceOf[ImageView]

    nameTextView.setText(person.name)
    dateTextView.setText(DateTimeFormat.longDate().print(person.dateOfBirth))
    //imageView.setImageResource(birthday.imageResource)
    rowView
  }

}
