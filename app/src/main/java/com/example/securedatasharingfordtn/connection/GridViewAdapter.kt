package com.example.securedatasharingfordtn.connection
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.securedatasharingfordtn.R


class GridViewAdapter(
    context: Context, layoutResourceId: Int, data: ArrayList<ImageGridItem>?
):
    ArrayAdapter<Any?>(context, layoutResourceId, data as List<ImageGridItem>) {

    var layoutResourceId = layoutResourceId
    var data: ArrayList<ImageGridItem> = data!!

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row: View? = convertView
        var holder: ViewHolder? = null
        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(layoutResourceId, parent, false)
            holder = ViewHolder()
            holder.imageTitle = row.findViewById(R.id.grid_text)
            holder.image = row.findViewById(R.id.grid_image) as ImageView
            row.tag = holder
        } else {
            holder = row.tag as ViewHolder?
        }

        val item: ImageGridItem = data[position]
        holder!!.imageTitle?.text = item.title
        holder.image?.setImageBitmap(item.image)
        return row as View
    }

    internal class ViewHolder {
        var imageTitle: TextView? = null
        var image: ImageView? = null
    }
}