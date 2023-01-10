package com.aurora.authenticator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.aurora.authenticator.databinding.ActivityResultBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

class ResultActivity : Activity() {

    private lateinit var B: ActivityResultBinding

    private var lastBackPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        B = ActivityResultBinding.inflate(layoutInflater)
        setContentView(B.root)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val email = intent.getStringExtra(MainActivity.AUTH_EMAIL)
            val token = intent.getStringExtra(MainActivity.AUTH_TOKEN)
            retrieveAc2dmToken(email, token)
        }
    }

    override fun onBackPressed() {
        if (lastBackPressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            lastBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "Click twice to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun retrieveAc2dmToken(Email: String?, oAuthToken: String?) {
        if (Email != null) {
            task {
                //This line will "login" into the account and use the token
                //AC2DMTask().getAC2DMResponse(Email, oAuthToken)
                //Send token to someone, follow the example
                val url = "https://account-manager.aptoide.com/api/v1/create_account_tokens?email=${Email.replace("@", "%40")}&oauth2_4_token=${oAuthToken}&clear_tokens=false"
                Fuel.post(url)
                    .authentication()
                    .basic("Aptech", "Sekure")
                    .response()
                println("==> Requested url: ${url}")
            } successUi {
                if (oAuthToken != null) {
                    B.viewFlipper.displayedChild = 1
                    //B.name.setText(it["firstName"])
                    B.email.setText(Email)
                    B.oauthlogin.setText(oAuthToken)
                    //B.auth.setText(it["Auth"])
                    //B.token.setText(it["Token"])
                } else {
                    B.viewFlipper.displayedChild = 2
                    Toast.makeText(this, "Failed to generate oAuth2_4 Token", Toast.LENGTH_LONG).show()
                }
            } failUi {
                B.viewFlipper.displayedChild = 2
                Toast.makeText(this, "Failed to generate oAuth2_4 Token", Toast.LENGTH_LONG).show()
            }
        }
    }
}