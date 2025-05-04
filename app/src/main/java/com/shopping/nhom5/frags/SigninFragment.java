package com.shopping.nhom5.frags;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.cloudinary.api.exceptions.ApiException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.User;

public class SigninFragment extends Fragment {

    View view;
    NavController navController;
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "UserFragment";
    Button btnLoginWithGoogle;
    private DatabaseReference mDatabase;

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        btnLoginWithGoogle = this.view.findViewById(R.id.user_btn_google);
        navController = Navigation.findNavController(view);
        configureGoogleSignIn();
        setLoginWithGoogleListener();
        navigateToLoginFragTroughTxt();
        navigateToRegister();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                onSignInSuccess(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void navigateToLoginFragTroughTxt() {
        TextView loginTxt = view.findViewById(R.id.edit_text_login_text);

        String fullText = getString(R.string.already_have_an_account);
        String clickablePart = getString(R.string.login_clickable_text);

        SpannableString myString = new SpannableString(fullText);

        int startIndex = fullText.indexOf(clickablePart);
        int endIndex = startIndex + clickablePart.length();

        if (startIndex == -1) {
            // Đề phòng trường hợp không tìm thấy (nội dung dịch sai, v.v.)
            Log.e("SigninFragment", "Không tìm thấy đoạn text có thể click!");
            loginTxt.setText(fullText);
            return;
        }

        // Click event
        myString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                NavDirections action = SigninFragmentDirections.actionLoginFragmentToLoginWithEmailAndPassFragment();
                navController.navigate(action);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // bỏ gạch chân nếu muốn
                ds.setColor(Color.GREEN);    // màu chữ
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Style bổ sung
        myString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginTxt.setText(myString);
        loginTxt.setMovementMethod(LinkMovementMethod.getInstance());
        loginTxt.setHighlightColor(Color.TRANSPARENT);
    }


    private void navigateToRegister() {
        Button continueBtn = view.findViewById(R.id.pay_button);
        TextInputEditText emailEt = view.findViewById(R.id.login_et_email);
        continueBtn.setOnClickListener(v -> {
            NavDirections action = SigninFragmentDirections.actionLoginFragmentToRegisterFragment(emailEt.getText().toString());
            navController.navigate(action);
        });
    }


    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void setLoginWithGoogleListener() {
        btnLoginWithGoogle.setOnClickListener(v -> {
            Log.d(TAG, "Login with Google button clicked");
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void checkEmailAndSignIn(GoogleSignInAccount account, String userId, String email) {
        mDatabase.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Đã có user
                navigateToUserFrag();
            } else {
                // Chưa có → tạo mới
                createUserInDatabase(email, userId, account);
            }
        });
    }


    private void createUserInDatabase(String email,String userId, GoogleSignInAccount account) {
        User newUser = new User(email,account.getDisplayName());
        newUser.setId(userId);
        mDatabase.child(userId).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User created successfully in the database.");
                logInUser(account);
            } else {
                Log.w(TAG, "Failed to create user in the database.", task.getException());
            }
        });
    }

    private void logInUser(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        navigateToUserFrag();
                        Log.d(TAG, "signInWithCredential:success");
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }
    private void navigateToUserFrag() {
        navController.navigate(SigninFragmentDirections.actionGlobalUserFragment());
    }

    // Call this method after a successful Google sign-in
    private void onSignInSuccess(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();
                    checkEmailAndSignIn(account, userId, email);
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

}