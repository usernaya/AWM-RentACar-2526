package be.rentacar.auth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import be.rentacar.R
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthManager"

object AuthManager {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    val isLoggedIn: Boolean get() = currentUser != null

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "==== signInWithGoogle ====")
            Log.d(TAG, "idToken length: ${idToken.length}")
            Log.d(TAG, "idToken preview: ${idToken.take(20)}...")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d(TAG, "→ calling firebaseAuth.signInWithCredential")
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            Log.d(TAG, "← signInWithCredential returned")
            val user = authResult.user ?: throw IllegalStateException("Firebase user null after signIn")
            Log.d(TAG, "Sign-in successful: ${user.email} uid=${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in FAILED with ${e.javaClass.simpleName}: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut(context: Context) {
        firebaseAuth.signOut()
        getGoogleSignInClient(context).signOut()
        Log.d(TAG, "Signed out")
    }
}
