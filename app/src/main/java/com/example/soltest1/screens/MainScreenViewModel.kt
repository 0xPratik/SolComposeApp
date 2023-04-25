package com.example.soltest1.screens
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solana.Solana
import com.solana.api.getRecentBlockhash
import com.solana.api.sendRawTransaction
import com.solana.api.sendTransaction
import com.solana.core.PublicKey
import com.solana.core.SIGNATURE_LENGTH
import com.solana.core.SerializeConfig
import com.solana.core.Transaction
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.RpcCluster
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.JsonRpcDriver
import com.solana.networking.RPCEndpoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.solana.programs.SystemProgram
import kotlin.math.log

data class WalletViewState(

    val userAddress: String = "",
    val authToken: String = "",
)

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val walletAdapter: MobileWalletAdapter,
    ) : ViewModel() {


    private val _state = MutableStateFlow(WalletViewState())

    val viewState = _state.asStateFlow()

    fun connect(
        identityUri: Uri,
        iconUri: Uri,
        identityName: String,
        activityResultSender: ActivityResultSender
    ) {
        viewModelScope.launch {
            val result = walletAdapter.transact(activityResultSender) {

                authorize(
                    identityUri = identityUri,
                    iconUri = iconUri,
                    identityName = identityName,
                    rpcCluster = RpcCluster.Devnet
                )
            }

            when (result) {
                is TransactionResult.Success -> {
                    Log.d("Connected",PublicKey(result.payload.publicKey).toBase58())

                    _state.value = WalletViewState(
                        userAddress = PublicKey(result.payload.publicKey).toBase58(),
                        authToken = result.payload.authToken)
                }
                is TransactionResult.NoWalletFound -> {
                    Log.d("Err","No Wallet Found")
                }
                is TransactionResult.Failure -> {
                    /** not gonna do anything here now **/
                }
            }
        }
    }

    fun trySendingSol(
        identityUri: Uri,
        iconUri: Uri,
        identityName: String,
        activityResultSender: ActivityResultSender
    ) {
        viewModelScope.launch {
            val token = viewState.value.authToken
            val res = walletAdapter.transact(activityResultSender) {

                val reauth = reauthorize(identityUri, iconUri, identityName, token)
                _state.value = WalletViewState(
                    PublicKey(reauth.publicKey).toBase58(),
                    reauth.authToken
                )
                val my_pubkey = PublicKey("9iSD3wkC1aq3FcwgjJfEua9FkkZJWv7Cuxs6sKjc3VnR")
                val ix = SystemProgram.transfer(
                    fromPublicKey = PublicKey(reauth.publicKey),
                    toPublickKey = my_pubkey,
                    lamports = 100000000
                )
                val tx = Transaction()
                val solana = Solana(HttpNetworkingRouter(RPCEndpoint.devnetSolana))
                tx.recentBlockhash = solana.api.getRecentBlockhash().getOrThrow()
                tx.feePayer = PublicKey(reauth.publicKey)
                tx.add(ix)
                val txBytes = tx.serialize(
                    SerializeConfig(
                    requireAllSignatures = false,
                    verifySignatures = false
                )
                )
                val signingResult = signTransactions(arrayOf(txBytes))
                Log.d("Sign Result",signingResult.signedPayloads.toString());
                val txid = solana.api.sendRawTransaction(signingResult.signedPayloads[0]).getOrThrow()
                Log.d("TXID",txid.toString())
                return@transact signingResult.signedPayloads[0].sliceArray(1 until 1 + SIGNATURE_LENGTH)

            }

            when(res){
                is TransactionResult.Success -> {
                    val solana = Solana(HttpNetworkingRouter(RPCEndpoint.devnetSolana))
                }
                is TransactionResult.NoWalletFound -> {
                    Log.d("Err","No Wallet Found")
                }
                is TransactionResult.Failure -> {
                    Log.d("FAILED","FAILED")
                }
            }
        }
    }


//    fun disconnect() {
//        viewModelScope.launch {
//            walletConnectionUseCase.clearConnection()
//        }
//    }

}