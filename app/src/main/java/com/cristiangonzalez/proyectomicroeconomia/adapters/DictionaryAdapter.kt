package com.cristiangonzalez.proyectomicroeconomia.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.utils.inflate
import com.cristiangonzalez.proyectomicroeconomia.models.Word
import com.cristiangonzalez.proyectomicroeconomia.utils.CircleTransform
import com.cristiangonzalez.proyectomicroeconomia.interfaces.DictionaryClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dictionary_item.view.*
import kotlin.collections.ArrayList

class DictionaryAdapter(private val items: ArrayList<Word>, private val dictionaryClickListener: DictionaryClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layout = R.layout.fragment_dictionary_item

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(word: Word) = with(itemView){
            val position = adapterPosition + 1
            val space = resources.getString(R.string.word_space)
            val wordPosition = position.toString() + space + word.word
            textViewWord.text = wordPosition
            textViewDescription.text = word.description

            if (word.profileImageURL.isEmpty()) {
                Picasso.get().load(R.drawable.ic_account_s).placeholder(R.drawable.ic_account_s).resizeDimen(R.dimen.dictionary_profile_image_size, R.dimen.dictionary_profile_image_size)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            } else {
                Picasso.get().load(word.profileImageURL).placeholder(R.drawable.ic_account_s).resizeDimen(R.dimen.dictionary_profile_image_size, R.dimen.dictionary_profile_image_size)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflate(layout))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])

        val data = items[position]
        holder.itemView.setOnClickListener {
            dictionaryClickListener.onDictionaryClickListener(data)
        }
    }

    override fun getItemCount() = items.size

}