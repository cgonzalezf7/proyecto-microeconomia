package com.cristiangonzalez.proyectomicroeconomia.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.proyectomicroeconomia.*
import com.cristiangonzalez.proyectomicroeconomia.utils.goToActivity
import com.cristiangonzalez.proyectomicroeconomia.utils.isValidEmail
import com.cristiangonzalez.proyectomicroeconomia.utils.toast
import com.cristiangonzalez.proyectomicroeconomia.utils.validate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.buttonGoLogIn
import kotlinx.android.synthetic.main.activity_forgot_password.editTextEmail

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotPasswordProgressBar.bringToFront()
        mAuth = FirebaseAuth.getInstance()

        editTextEmail.validate {
            textInputEmail.error = if (isValidEmail(it)) null else getString(R.string.login_invalid_email)
        }

        buttonGoLogIn.setOnClickListener {
            goToActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        buttonForgot.setOnClickListener {
            showProgressBar()
            val email = editTextEmail.text.toString()

            if (isValidEmail(email)) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                    toast(R.string.login_sent_reset_password)

                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            } else {
                hideProgressBar()
                toast(R.string.login_invalid_email)
            }
        }
    }

    private fun showProgressBar() {
        forgotPasswordProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        forgotPasswordProgressBar.visibility = View.GONE
    }

}