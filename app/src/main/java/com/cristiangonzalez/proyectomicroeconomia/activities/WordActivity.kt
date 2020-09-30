package com.cristiangonzalez.proyectomicroeconomia.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.utils.CircleTransform
import com.cristiangonzalez.proyectomicroeconomia.utils.goToActivity
import com.cristiangonzalez.proyectomicroeconomia.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_word.*
import java.util.ArrayList

class WordActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dictionaryDBRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)

        setUpActionBar()
        setUpCurrentUser()
        setUpDictionaryDB()
        setUpUI(getExtras())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_word, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteWord -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.word_delete_dialog_title))
                    .setMessage(resources.getString(R.string.word_delete_dialog_message))
                    .setNegativeButton(resources.getString(R.string.word_delete_dialog_negative)) { _, _ -> }
                    .setPositiveButton(resources.getString(R.string.word_delete_dialog_positive)) { _, _ ->
                        dictionaryDBRef.get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    val word = document.get(getString(R.string.word_word)) as String
                                    val currentWord = textViewWord.text.toString()

                                    if (word == currentWord) {
                                        if (currentUser.email == textViewAuthorEmail.text.toString()) {
                                            val id = document.id
                                            dictionaryDBRef.document(id).delete()
                                                .addOnSuccessListener{
                                                    toast(R.string.word_delete_success)
                                                    goToActivity<MainActivity>()
                                                }
                                                .addOnFailureListener{ toast(R.string.word_delete_failure)}
                                        } else {
                                            toast(R.string.word_cant_delete_word)
                                        }
                                    }
                                }
                            }.addOnFailureListener{
                                toast(R.string.profile_exception)
                            }
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpActionBar() {
        setSupportActionBar(wordToolbar)

        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = getString(R.string.word_word_capital)
        }
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpDictionaryDB() {
        dictionaryDBRef = store.collection(getString(R.string.word_collection))
    }

    private fun getExtras(): ArrayList<String> {
        val extras = intent.extras!!
        val array: ArrayList<String>
        val word = extras.getString(getString(R.string.word_word))!!
        val description = extras.getString(getString(R.string.word_description))!!
        val email = extras.getString(getString(R.string.word_email))!!
        val image = extras.getString(getString(R.string.word_profile_image_url))!!

        array = arrayListOf(word, description, email, image)
        return array
    }

    private fun setUpUI(data: ArrayList<String>) {
        textViewWord.text = data[0]
        textViewDescription.text = data[1]
        textViewAuthorEmail.text = data[2]
        if (data[3].isEmpty()) {
            Picasso.get().load(R.drawable.ic_account).placeholder(R.drawable.ic_account).resizeDimen(R.dimen.word_profile_image_size, R.dimen.word_profile_image_size)
                .centerCrop().transform(CircleTransform()).into(imageViewProfile)
        } else {
            Picasso.get().load(data[3]).placeholder(R.drawable.ic_account).resizeDimen(R.dimen.word_profile_image_size, R.dimen.word_profile_image_size)
                .centerCrop().transform(CircleTransform()).into(imageViewProfile)
        }

    }

}