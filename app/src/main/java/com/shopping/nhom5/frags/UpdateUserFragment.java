package com.shopping.nhom5.frags;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.User;
import com.shopping.nhom5.utils.CloudinaryConfig;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
public class UpdateUserFragment extends Fragment {

    View v;
    TextInputEditText fNameEt, lNameEt, emailEt, passwordEt, confirmPasswordEt;
    String fName, lName, email, password, confirmPassword;
    CircularProgressIndicator progressIndicator;
    Button updateBtn, editProfileImgBtn;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    NavController navController;
    User user;
    FirebaseUser theUser;
    Uri imageUri = null;
    ImageView avatar;

    public UpdateUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        user = UpdateUserFragmentArgs.fromBundle(getArguments()).getUser();
        if(user.getFirstName()==null && user.getLastName()==null){
            user.setFirstName("");
            user.setLastName(user.getLastName());
        }
        mAuth = FirebaseAuth.getInstance();
        theUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("users/" + theUser.getUid());
        initView();
        putValueInFields();
        profileImageListener();
        updateBtnListener();
        navController = Navigation.findNavController(v);
    }

    private void initView() {
        fNameEt = v.findViewById(R.id.register_et_first_name);
        lNameEt = v.findViewById(R.id.register_et_last_name);
        emailEt = v.findViewById(R.id.signup_et_email);
        passwordEt = v.findViewById(R.id.signup_et_password);
        confirmPasswordEt = v.findViewById(R.id.register_et_confirm_password);
        updateBtn = v.findViewById(R.id.pay_button);
        progressIndicator = v.findViewById(R.id.circularProgressIndicator);
        editProfileImgBtn = v.findViewById(R.id.user_edit_profile_image_button);
        avatar = v.findViewById(R.id.imageView);
    }

    private void putValueInFields() {
        fNameEt.setText(user.getFirstName());
        lNameEt.setText(user.getLastName());
        emailEt.setText(user.getEmail());
        if (!user.getAvatarUrl().isEmpty()) {
            Picasso.get().load(user.getAvatarUrl()).into(avatar);
        }
    }

    private boolean validateInputs() {
        fName = fNameEt.getText().toString().trim();
        lName = lNameEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        confirmPassword = confirmPasswordEt.getText().toString().trim();
        TextInputLayout confirmPasswordLayout = v.findViewById(R.id.register_til_confirm_password);
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty()) {
            Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.first_name_and_last_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Pattern.compile("^(.+)@(\\\\S+)$").matcher(email).matches()) {
            Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getResources().getString(R.string.passwords_dont_match));
            return false;
        }
        if (password.length() < 8 && !password.isEmpty()) {
            confirmPasswordLayout.setError(getResources().getString(R.string.more_than_7));
            return false;
        }
        confirmPasswordLayout.setError(null);
        return true;
    }

    private void updateBtnListener() {
        updateBtn.setOnClickListener(v -> {
            if (!validateInputs()) return;
            inputEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);
            if (imageUri != null) {
                uploadImage();
            } else {
                updateUser(null);
            }
        });
    }

    private void profileImageListener() {
        editProfileImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 201);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 && data != null) {
            imageUri = data.getData();
            avatar.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        try {
            File file = new File(imageUri.getPath());
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("folder", "users");
            uploadOptions.put("public_id", theUser.getUid());

            Map uploadResult = CloudinaryConfig.getInstance().uploader().upload(file, uploadOptions);
            String url = (String) uploadResult.get("secure_url");
            updateUser(url);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            inputEnabled(true);
            progressIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUser(String avatarUrl) {
        User user = new User(fName, lName, email);
        if (avatarUrl != null)
            user.setAvatarUrl(avatarUrl);
        else
            user.setAvatarUrl(this.user.getAvatarUrl());
        mRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                inputEnabled(true);
                progressIndicator.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (avatarUrl != null)
                        Picasso.get().load(avatarUrl).into(avatar);
                    imageUri = null;
                    if (!password.isEmpty()) {
                        updatePassword();
                    } else {
                        Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_LONG).show();
                        navController.navigateUp();
                    }
                } else {
                    Toast.makeText(getContext(), "Something went wrong! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePassword() {
        theUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_LONG).show();
                    navController.navigateUp();
                } else {
                    Toast.makeText(getContext(), "Something went wrong! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void inputEnabled(boolean b) {
        fNameEt.setEnabled(b);
        lNameEt.setEnabled(b);
        passwordEt.setEnabled(b);
        confirmPasswordEt.setEnabled(b);
    }

    private void navigateToUser() {
        navController.navigate(SignupFragmentDirections.actionGlobalUserFragment());
    }
}