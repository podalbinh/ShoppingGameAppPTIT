package com.shopping.nhom5.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.shopping.nhom5.R;

import java.util.regex.Pattern;

public class SigninWithEmailAndPassFragment extends Fragment {

    View v;
    String email, password;
    TextInputEditText emailEt, passwordEt;
    CircularProgressIndicator progressIndicator;
    Button signinBtn;
    FirebaseAuth mAuth;
    NavController navController;

    public SigninWithEmailAndPassFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin_with_email_and_pass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        initView();
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(v);
        signupBtnListener();
    }

    private void initView() {
        emailEt = v.findViewById(R.id.signup_et_email);
        passwordEt = v.findViewById(R.id.signup_et_password);
        progressIndicator = v.findViewById(R.id.circularProgressIndicator);
        signinBtn = v.findViewById(R.id.pay_button);
    }

    private boolean validateInputs() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        TextInputLayout emailLayout = v.findViewById(R.id.signup_til_email);
        TextInputLayout passwordLayout = v.findViewById(R.id.signup_til_password);
        String error = getResources().getString(R.string.this_field_is_required);
        if (email.isEmpty()) {
            emailLayout.setError(error);
            return false;
        } else {
            emailLayout.setError(null);
        }
        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
            emailLayout.setError(getResources().getString(R.string.email_is_invalid));
            return false;
        } else {
            emailLayout.setError(null);
        }
        if (password.isEmpty()) {
            passwordLayout.setError(error);
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    private void signupBtnListener() {
        signinBtn.setOnClickListener(v -> {
            if (!validateInputs()) return;
            inputEnabled(false);
            email = emailEt.getText().toString();
            password = passwordEt.getText().toString();
            progressIndicator.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                inputEnabled(true);
                progressIndicator.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                    navigateToUserFrag();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void inputEnabled(boolean b) {
        emailEt.setEnabled(b);
        passwordEt.setEnabled(b);
        signinBtn.setEnabled(b);
    }

    private void navigateToUserFrag() {
        navController.navigate(SigninWithEmailAndPassFragmentDirections.actionGlobalUserFragment());
    }
}