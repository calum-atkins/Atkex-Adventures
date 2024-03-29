package com.example.atkex

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * Adapter for points of interests
 */
class PointsOfInterestAdapter (private val context: Context, private val imageModelArrayList: MutableList<PointsOfInterestModel>,
                               private val collectionIDList: ArrayList<String>, private val userDocumentID : String) : RecyclerView.Adapter<PointsOfInterestAdapter.ViewHolder>() {
    /**
     * Inflate our views using the layout defined in row_layout.xml
     */
    private val storageRef = Firebase.storage.getReferenceFromUrl("gs://atkex-project.appspot.com")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Bind the data to the child views of the ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]

        val storageReference = storageRef.child("images/" + info.getImages())

        storageReference.downloadUrl.addOnSuccessListener {
            Glide.with(this.context)
                .load(it)
                .into(holder.imgView)
        }

        holder.txtTitle.text = info.getNames()
    }

    /**
     * Number of models in the array
     */
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    /**
     * The parent class that handles layout inflation and child view use
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {

        var imgView = itemView.findViewById<View>(R.id.image) as ImageView
        var txtTitle = itemView.findViewById<View>(R.id.title) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val mess =
                txtTitle.text.toString()
            val snackbar = Snackbar.make(v, mess, Snackbar.LENGTH_LONG)
            snackbar.show()

            /** Intent of which poi to go to */
            val intent = Intent(v.getContext(), PointOfInterestActivity::class.java)
            intent.putExtra("name", txtTitle.text)

            var count = 0
            for (item in imageModelArrayList) {
                if (item.name == txtTitle.text) {
                    intent.putExtra("info", item.info)
                    intent.putExtra("lat", item.lat)
                    intent.putExtra("long", item.long)
                    intent.putExtra("id", collectionIDList.get(count))
                    intent.putExtra("userDocumentID", userDocumentID)
                }
                count += 1

            }

            imgView.buildDrawingCache()
            val bitmap: Bitmap = imgView.getDrawingCache()
            intent.putExtra("BitmapImage", bitmap)

            v.getContext().startActivity(intent)
        }
    }
}