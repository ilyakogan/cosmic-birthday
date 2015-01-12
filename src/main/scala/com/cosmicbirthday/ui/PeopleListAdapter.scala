package com.cosmicbirthday.ui

import android.content.Context
import android.net.Uri
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, ImageView, TextView}
import com.cosmicbirthday.R
import com.cosmicbirthday.dbentities.Person
import org.joda.time.format.DateTimeFormat

class PeopleListAdapter(val context: Context, val values: Array[Person])
  extends ArrayAdapter[Person](context, R.layout.birthday_list_row, values) {

  class ViewHolder(val nameTextView: TextView, val dateTextView: TextView, val imageView: ImageView)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    val rowView =
      if (convertView == null) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
        val newRowView = inflater.inflate(R.layout.person_list_row, parent, false)
        val newHolder = new ViewHolder(
          newRowView.findViewById(R.id.name).asInstanceOf[TextView],
          newRowView.findViewById(R.id.date).asInstanceOf[TextView],
          newRowView.findViewById(R.id.image).asInstanceOf[ImageView])
        newRowView.setTag(R.id.TAG_VIEW_HOLDER, newHolder)
        newRowView
      }
      else convertView

    val viewHolder = rowView.getTag(R.id.TAG_VIEW_HOLDER).asInstanceOf[ViewHolder]

    val person = values(position)

    viewHolder.nameTextView.setText(person.name)
    viewHolder.dateTextView.setText(DateTimeFormat.longDate().print(person.dateOfBirth))
    person.avatarUrl match {
      case Some(url) => viewHolder.imageView.setImageURI(Uri.parse(url))
      case None => viewHolder.imageView.setImageResource(R.drawable.persondefault)
    }
    rowView.setTag(R.id.TAG_PERSON_ID, person.id)
    rowView
  }
}
