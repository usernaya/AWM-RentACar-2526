package be.rentacar.ui.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import be.rentacar.R
import be.rentacar.auth.AuthManager

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val googleSignInClient = remember { AuthManager.getGoogleSignInClient(context) }

    val errorIdTokenNull = stringResource(R.string.login_error_id_token_null)
    val errorUnexpected = stringResource(R.string.login_error_unexpected)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "==== LAUNCHER RESULT ====")
        Log.d(TAG, "resultCode: ${result.resultCode}")
        Log.d(TAG, "data is null: ${result.data == null}")

        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                Log.d(TAG, "account email: ${account?.email}")
                Log.d(TAG, "account idToken null? ${account?.idToken == null}")

                val idToken = account?.idToken
                if (idToken != null) {
                    Log.d(TAG, "→ calling handleSignInResult")
                    viewModel.handleSignInResult(idToken)
                } else {
                    Log.e(TAG, "ID token NULL")
                    Toast.makeText(context, errorIdTokenNull, Toast.LENGTH_LONG).show()
                    viewModel.setError(errorIdTokenNull)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException: code=${e.statusCode}", e)
                Toast.makeText(
                    context,
                    "Google Sign-In failed (code ${e.statusCode})",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.setError("Google Sign-In failed: code ${e.statusCode}")
            }
        } else {
            Log.w(TAG, "Popup returned non-OK: resultCode=${result.resultCode}")
            viewModel.setError("$errorUnexpected (resultCode=${result.resultCode})")
        }
    }

    // bij openen scherm: al ingelogd? anders silent sign-in proberen
    LaunchedEffect(Unit) {
        if (AuthManager.isLoggedIn) {
            Log.d(TAG, "Already logged in — navigating to Home")
            onLoginSuccess()
        } else {
            Log.d(TAG, "→ attemptSilentSignIn")
            viewModel.attemptSilentSignIn(googleSignInClient)
        }
    }

    LaunchedEffect(uiState) {
        Log.d(TAG, "uiState changed: ${uiState::class.simpleName}")
        if (uiState is LoginUiState.Success) {
            Log.d(TAG, "→ calling onLoginSuccess()")
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onSignInClick = {
            viewModel.setLoading()
            launcher.launch(googleSignInClient.signInIntent)
        }
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_extra_large))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text(
                text = stringResource(R.string.app_tagline_nl),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_extra_large) + 16.dp))

            when (val state = uiState) {
                is LoginUiState.Loading -> {
                    Text(
                        text = stringResource(R.string.login_loading),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                is LoginUiState.Error -> {
                    ErrorCard(message = state.message)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
                    GoogleSignInButton(onClick = onSignInClick)
                }
                else -> {
                    GoogleSignInButton(onClick = onSignInClick)
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium))
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Text(
                text = stringResource(R.string.login_error_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.button_height_large))
    ) {
        Text(
            text = stringResource(R.string.login_button_google),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

