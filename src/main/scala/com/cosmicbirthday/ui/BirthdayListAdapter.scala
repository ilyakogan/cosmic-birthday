package com.cosmicbirthday.ui

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, ImageView, TextView}
import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Me
import com.cosmicbirthday.entities.{BirthdayItem, BirthdayListItem, SectionItem}
import org.joda.time.format.DateTimeFormat

class BirthdayListAdapter(val context: Context, val values: Array[BirthdayListItem])
  extends ArrayAdapter[BirthdayListItem](context, R.layout.birthday_list_row, values) {
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    values(position) match {
      case BirthdayItem(birthday) =>
        val rowView = inflater.inflate(R.layout.birthday_list_row, parent, false)

        val nameTextView = rowView.findViewById(R.id.name).asInstanceOf[TextView]
        val dateTextView = rowView.findViewById(R.id.date).asInstanceOf[TextView]
        val imageView = rowView.findViewById(R.id.image).asInstanceOf[ImageView]

        val nameIs: String = birthday.person.name match {
          case Me() => "You are "
          case name => '\u200E' + name + " is "
        }
        nameTextView.setText(nameIs + birthday.mnemonic + " old")
        dateTextView.setText("on " + DateTimeFormat.mediumDate().print(birthday.date))
        imageView.setImageResource(birthday.imageResource)
        rowView

      case SectionItem(text) =>
        val rowView = inflater.inflate(R.layout.birthday_list_section, parent, false)

        val textView = rowView.findViewById(R.id.text).asInstanceOf[TextView]
        textView.setText(text)
        rowView
    }
  }
}
