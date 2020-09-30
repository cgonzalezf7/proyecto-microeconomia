package com.cristiangonzalez.proyectomicroeconomia.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.cristiangonzalez.proyectomicroeconomia.R
import kotlinx.android.synthetic.main.dialog_credits.view.*

class CreditsDialog : DialogFragment() {

    private lateinit var _view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _view = inflater.inflate(R.layout.dialog_credits, container, false)

        _view.dialogToolbar.title = getString(R.string.credits_title_credits)
        _view.dialogToolbar.setNavigationIcon(R.drawable.ic_close)
        _view.dialogToolbar.setNavigationOnClickListener {
            dismiss()
        }

        return _view
    }

}
