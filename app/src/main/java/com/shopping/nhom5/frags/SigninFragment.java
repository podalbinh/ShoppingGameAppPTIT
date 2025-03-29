package com.shopping.nhom5.frags;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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

import com.google.android.material.textfield.TextInputEditText;
import com.shopping.nhom5.R;

public class SigninFragment extends Fragment {

    View view;
    NavController navController;

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
        navController = Navigation.findNavController(view);
        navigateToLoginFragTroughTxt();
        navigateToRegister();
    }


    private void navigateToLoginFragTroughTxt() {
        TextView loginTxt = view.findViewById(R.id.edit_text_login_text);
        SpannableString myString = new SpannableString(loginTxt.getText().toString());

        int startIndex = 25;
        int lastIndex = 30;
        myString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                NavDirections action = SigninFragmentDirections.actionLoginFragmentToLoginWithEmailAndPassFragment();
                navController.navigate(action);
            }
        }, startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        myString.setSpan(new UnderlineSpan(), startIndex, lastIndex, 0);
        myString.setSpan(new StyleSpan(Typeface.BOLD),startIndex,lastIndex,0);

        loginTxt.setText(myString);
        loginTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void navigateToRegister() {
        Button continueBtn = view.findViewById(R.id.pay_button);
        TextInputEditText emailEt = view.findViewById(R.id.login_et_email);
        continueBtn.setOnClickListener(v -> {
            NavDirections action = SigninFragmentDirections.actionLoginFragmentToRegisterFragment(emailEt.getText().toString());
            navController.navigate(action);
        });
    }

}