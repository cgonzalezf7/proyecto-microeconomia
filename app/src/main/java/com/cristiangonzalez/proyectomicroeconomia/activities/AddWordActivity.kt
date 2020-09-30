package com.cristiangonzalez.proyectomicroeconomia.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.models.Word
import com.cristiangonzalez.proyectomicroeconomia.utils.goToActivity
import com.cristiangonzalez.proyectomicroeconomia.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_word.*
import java.util.*

class AddWordActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dictionaryDBRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        setUpActionBar()
        setUpCurrentUser()
        setUpDictionaryDB()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_add_word, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveWord -> {
                setUpSaveWordButton()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (editTextWord.isFocused || editTexDescription.isFocused) {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.word_dialog_title))
                .setMessage(resources.getString(R.string.word_dialog_message))
                .setNegativeButton(resources.getString(R.string.word_dialog_negative)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.word_dialog_positive)) { _, _ ->
                    super.onBackPressed()
                }
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(addWordToolbar)

        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpDictionaryDB() {
        dictionaryDBRef = store.collection(getString(R.string.word_collection))
    }

    private fun saveWord(word: Word) {
        val newWord = HashMap<String, Any>()
        newWord[getString(R.string.word_email)] = word.email
        newWord[getString(R.string.word_profile_image_url)] = word.profileImageURL
        newWord[getString(R.string.word_word)] = word.word
        newWord[getString(R.string.word_description)] = word.description

        dictionaryDBRef.add(newWord)
            .addOnCompleteListener {
                toast(R.string.word_add_word)
                goToActivity<MainActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            .addOnFailureListener {
                toast(R.string.word_error_add_word)
            }
    }

    private fun setUpSaveWordButton() {
        val word = editTextWord.text.toString()
        val description = editTexDescription.text.toString()

        if (word.isNotEmpty() && description.isNotEmpty()) {
            dictionaryDBRef.get()
                .addOnSuccessListener { result ->
                    var duplicateWord = false
                    for (document in result) {
                        val currentWord = document.get(getString(R.string.word_word)) as String
                        if (word == currentWord) duplicateWord = true
                    }
                    if (duplicateWord) {
                        toast(R.string.word_duplicate_word)
                    } else {
                        val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: run { "" }
                        val email = currentUser.email!!
                        val newWord = Word(email, photo, word, description)
                        saveWord(newWord)
                    }
                }.addOnFailureListener{
                    toast(R.string.profile_exception)
                }
        } else {
            toast(R.string.word_empty_data)
        }
    }

}