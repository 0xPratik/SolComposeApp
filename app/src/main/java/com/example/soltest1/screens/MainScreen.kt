package com.example.soltest1.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import com.example.soltest1.R
import com.example.soltest1.ui.theme.Shapes
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

@Composable
fun MainScreen(mainScreenViewModel: MainScreenViewModel = hiltViewModel(), activityResultSender: ActivityResultSender) {
    val viewState = mainScreenViewModel.viewState.collectAsState().value

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Pubkey ${viewState.userAddress}")
        Text(text = "Auth Token ${viewState.authToken}")
        Button(onClick = {
            mainScreenViewModel.connect(
                identityName = "Pratik Saria",
                iconUri = Uri.parse(R.drawable.sunrise.toString()),
                identityUri = Uri.parse("https://solanamobile.com"),
                activityResultSender = activityResultSender
            )
        } ) {
            Text(text = "Connect Wallet")
        }
        Button(onClick = {
            mainScreenViewModel.trySendingSol(
                identityName = "Pratik Saria",
                iconUri = Uri.parse(R.drawable.sunrise.toString()),
                identityUri = Uri.parse("https://solanamobile.com"),
                activityResultSender = activityResultSender
            )
        }) {
            Text(text = "Send SOL to Pratik")
        }
    }
}