package com.cristiangonzalez.proyectomicroeconomia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.utils.toast
import com.cristiangonzalez.proyectomicroeconomia.utils.CircleTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dictionaryDBRef: CollectionReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_profile, container, false)

        setUpCurrentUser()
        setUpDictionaryDB()
        subscribeToTotalWords()
        setUpUI()

        return _view
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpDictionaryDB() {
        dictionaryDBRef = store.collection(getString(R.string.word_collection))
    }

    private fun subscribeToTotalWords() {
        dictionaryDBRef.get()
            .addOnSuccessListener { result ->
                var totalWords = 0
                for (document in result) {
                    val wordEmail = document.get(getString(R.string.word_email)) as String
                    val currentEmail = currentUser.email!!

                    if (currentEmail == wordEmail) totalWords++

                    _view.wordsTextView.text = totalWords.toString()
                }
            }.addOnFailureListener{
                requireActivity().toast(R.string.profile_exception)
            }
    }

    private fun setUpUI(){
        _view.emailTextView.text = currentUser.email
        _view.nameTextView.text = currentUser.displayName ?: run { getString(R.string.profile_name_not_available)}
        currentUser.photoUrl?.let {
            Picasso.get().load(currentUser.photoUrl).placeholder(R.drawable.ic_account_xl).resizeDimen(R.dimen.profile_image_size, R.dimen.profile_image_size)
                .centerCrop().transform(CircleTransform()).into(_view.profileImageView)
        } ?: run {
            Picasso.get().load(R.drawable.ic_account_xl).placeholder(R.drawable.ic_account_xl).resizeDimen(R.dimen.profile_image_size, R.dimen.profile_image_size)
                .centerCrop().transform(CircleTransform()).into(_view.profileImageView)
        }
    }

}