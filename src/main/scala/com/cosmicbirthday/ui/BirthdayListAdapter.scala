package com.cosmicbirthday.ui

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, ImageView, TextView}
import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Person
import com.cosmicbirthday.entities.{BirthdayItem, BirthdayListItem, SectionItem}
import org.joda.time.format.DateTimeFormat

class BirthdayListAdapter(val context: Context, val values: Array[BirthdayListItem])
  extends ArrayAdapter[BirthdayListItem](context, R.layout.birthday_list_row, values) {
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    val item = values(position)
    createViewForItem(parent, inflater, item)
  }

  def createViewForItem(parent: ViewGroup, inflater: LayoutInflater, item: BirthdayListItem): View = {
    item match {
      case BirthdayItem(birthday) =>
        val rowView = inflater.inflate(R.layout.birthday_list_row, parent, false)

        val nameTextView = rowView.findViewById(R.id.name).asInstanceOf[TextView]
        val dateTextView = rowView.findViewById(R.id.date).asInstanceOf[TextView]
        val imageView = rowView.findViewById(R.id.image).asInstanceOf[ImageView]

        val ageText: String =
          if (birthday.person.name == Person.Me)
            context.getString(R.string.format_your_age, birthday.mnemonic)
          else
            context.getString(R.string.format_friend_age, birthday.person.name, birthday.mnemonic)

        nameTextView.setText(ageText)
        dateTextView.setText(context.getString(R.string.format_on_date).
          format(DateTimeFormat.mediumDate().print(birthday.date)))
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
