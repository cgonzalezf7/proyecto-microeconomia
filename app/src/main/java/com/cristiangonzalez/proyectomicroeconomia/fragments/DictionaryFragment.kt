package com.cristiangonzalez.proyectomicroeconomia.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.activities.WordActivity
import com.cristiangonzalez.proyectomicroeconomia.adapters.DictionaryAdapter
import com.cristiangonzalez.proyectomicroeconomia.models.Word
import com.cristiangonzalez.proyectomicroeconomia.interfaces.DictionaryClickListener
import com.cristiangonzalez.proyectomicroeconomia.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

class DictionaryFragment : Fragment(), DictionaryClickListener {

    private lateinit var _view: View

    private lateinit var viewAdapter: DictionaryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val wordList: ArrayList<Word> = ArrayList()
    private val wordFilterList: ArrayList<Word> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dictionaryDBRef: CollectionReference

    private var dictionarySubscription: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_dictionary, container, false)

        setHasOptionsMenu(true)
        setUpCurrentUser()
        setUpWordDB()
        subscribeToDictionary()
        setUpRecyclerView()

        return _view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_dictionary, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                wordFilterList.clear()
                if (newText.isNullOrEmpty()) {
                    wordFilterList.addAll(wordList)
                    viewAdapter.notifyDataSetChanged()
                } else {
                    val search = newText.toLowerCase(Locale.ROOT)
                    wordList.forEach {
                        if (it.word.toLowerCase(Locale.ROOT).contains(search)) wordFilterList.add(it)
                    }
                    viewAdapter.notifyDataSetChanged()
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        dictionarySubscription?.remove()
        super.onDestroyView()
    }

    override fun onDictionaryClickListener(data: Word) {
        val intent = Intent(activity, WordActivity::class.java)
        intent.putExtra(getString(R.string.word_email), data.email)
        intent.putExtra(getString(R.string.word_profile_image_url), data.profileImageURL)
        intent.putExtra(getString(R.string.word_word), data.word)
        intent.putExtra(getString(R.string.word_description), data.description)
        startActivity(intent)
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpWordDB() {
        dictionaryDBRef = store.collection(getString(R.string.word_collection))
    }

    private fun setUpRecyclerView() {
        viewManager = LinearLayoutManager(context)
        viewAdapter = DictionaryAdapter(wordFilterList, this)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = viewManager
        _view.recyclerView.adapter = viewAdapter
    }

    private fun subscribeToDictionary() {
        dictionarySubscription = dictionaryDBRef
            .orderBy(getString(R.string.word_word), Query.Direction.DESCENDING)
            .limit(150)
            .addSnapshotListener(object : EventListener,
                com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        activity!!.toast(getString(R.string.word_exception))
                        return
                    }
                    snapshot?.let {
                        wordList.clear()
                        val messages = it.toObjects(Word::class.java)
                        wordList.addAll(messages.asReversed())
                        wordFilterList.addAll(wordList)
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            })
    }

}
