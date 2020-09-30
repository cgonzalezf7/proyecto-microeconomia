package com.cristiangonzalez.proyectomicroeconomia.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.proyectomicroeconomia.*
import com.cristiangonzalez.proyectomicroeconomia.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.editTextEmail
import kotlinx.android.synthetic.main.activity_sign_up.editTextPassword
import kotlinx.android.synthetic.main.activity_sign_up.textInputEmail
import kotlinx.android.synthetic.main.activity_sign_up.textInputPassword

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUpProgressBar.bringToFront()
        mAuth = FirebaseAuth.getInstance()

        buttonSignUp.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword)) {
                signUpByEmail(email, password)
            } else {
                toast(R.string.login_incorrect_data)
            }
        }

        buttonGoLogIn.setOnClickListener {
            goToActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        editTextEmail.validate {
            textInputEmail.error = if (isValidEmail(it)) null else getString(R.string.login_invalid_email)
        }

        editTextPassword.validate {
            textInputPassword.error = if (isValidPassword(it)) null else getString(R.string.login_invalid_password)
        }

        editTextConfirmPassword.validate {
            textInputConfirmPassword.error = if (isValidConfirmPassword(editTextPassword.text.toString(), it)) null else getString(R.string.login_invalid_confirm_password)
        }
    }

    private fun signUpByEmail(email: String, password: String) {
        showProgressBar()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {
                    toast(R.string.login_sent_confirm_email)

                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            } else {
                hideProgressBar()
                toast(R.string.login_unexpected_error)
            }
        }
    }

    private fun showProgressBar() {
        signUpProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        signUpProgressBar.visibility = View.GONE
    }

}