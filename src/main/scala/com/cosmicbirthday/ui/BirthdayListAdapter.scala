package com.cosmicbirthday.ui

import android.content.Context
import android.view.{LayoutInflater, ViewGroup, View}
import android.widget.{ImageView, TextView, ArrayAdapter}
import com.cosmicbirthday.R
import com.cosmicbirthday.entities.AbsoluteBirthday
import org.joda.time.format.DateTimeFormat

class BirthdayListAdapter(val context: Context, val values: Array[AbsoluteBirthday])
  extends ArrayAdapter[AbsoluteBirthday](context, R.layout.birthday_list_row, values) {
  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
    val rowView = inflater.inflate(R.layout.birthday_list_row, parent, false)
    val birthday = values(position)

    val nameTextView = rowView.findViewById(R.id.name).asInstanceOf[TextView]
    val dateTextView = rowView.findViewById(R.id.date).asInstanceOf[TextView]
    val imageView = rowView.findViewById(R.id.image).asInstanceOf[ImageView]
    nameTextView.setText(birthday.mnemonic + " old")
    dateTextView.setText("on " + DateTimeFormat.mediumDate().print(birthday.date))
    imageView.setImageResource(birthday.imageResource)
    rowView
  }
}
