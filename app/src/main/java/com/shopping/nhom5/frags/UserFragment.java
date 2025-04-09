package com.shopping.nhom5.frags;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.User;
import com.shopping.nhom5.utils.CloudinaryConfig;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class UserFragment extends Fragment {

    View v;
    NavController navController;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mRef;
    Button logoutBtn, editProfileImgBtn, updateUserBtn, myOrdersBtn;
    TextView fullNameTv;
    CircularProgressIndicator progressIndicator;
    ScrollView scrollView;
    User theUser;
    Uri imageUri;
    RelativeLayout overlay;
    LinearProgressIndicator uploadProgress;
    ImageView avatar;

    public UserFragment() {
        // Required empty public constructor
    }

    public User getTheUser() {
        return theUser;
    }

    public void setTheUser(User theUser) {
        this.theUser = theUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();

        if (user == null) {
            Log.w("UserFragment", "User is null, redirecting to login.");
            navigateToLogin();
            return;
        }

        Log.d("UserFragment", "User UID: " + user.getUid());
        initDbRef(user.getUid());
        getUserInfo();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
        initView();
        logoutListener();
        updateUserBtnListener();
        profileImageListener();
        myOrdersBtnClickListener();
    }

    private void initView() {
        logoutBtn = v.findViewById(R.id.user_logout_button);
        updateUserBtn = v.findViewById(R.id.update_user_button);
        fullNameTv = v.findViewById(R.id.user_tv_full_name);
        progressIndicator = v.findViewById(R.id.user_progress_circle);
        scrollView = v.findViewById(R.id.scrollView2);
        editProfileImgBtn = v.findViewById(R.id.user_edit_profile_image_button);
        avatar = v.findViewById(R.id.shapeableImageView);
        uploadProgress = v.findViewById(R.id.image_uploading_progress);
        overlay = v.findViewById(R.id.image_uploading_overlay);
        myOrdersBtn = v.findViewById(R.id.user_my_orders);
    }

    private void initDbRef(String userUID) {
        mRef = FirebaseDatabase.getInstance().getReference("users").child(userUID);
    }

    private void logoutListener() {
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });
    }

    private void updateUserBtnListener() {
        updateUserBtn.setOnClickListener(v -> {
            navigateToUpdateUser();
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
            uploadImage();
        }
    }

    private void uploadImage() {
        overlay.setVisibility(View.VISIBLE);
        uploadProgress.setMax(100);

        new Thread(() -> {
            try {
                if (imageUri == null) {
                    throw new Exception("Image URI is null");
                }

                // Use content resolver to get input stream from URI
                java.io.InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    throw new Exception("Failed to open input stream from URI");
                }

                Map<String, Object> uploadOptions = new HashMap<>();
                uploadOptions.put("folder", "users");
                uploadOptions.put("public_id", user.getUid());

                Log.d("UploadImage", "Uploading file with URI: " + imageUri.toString());
                Log.d("UploadImage", "Cloudinary config: " + CloudinaryConfig.getInstance().config.toString());

                // Use the input stream for upload
                Map uploadResult = CloudinaryConfig.getInstance().uploader().upload(inputStream, uploadOptions);
                String url = (String) uploadResult.get("secure_url");

                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    theUser.setAvatarUrl(url);
                    Picasso.get().load(url).into(avatar);
                    mRef.setValue(theUser);
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_LONG).show();
                    overlay.setVisibility(View.INVISIBLE);
                });
            } catch (Exception e) {
                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    overlay.setVisibility(View.INVISIBLE);
                    Log.e("UploadImage", "Upload failed", e);
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void myOrdersBtnClickListener() {
        myOrdersBtn.setOnClickListener(v -> {
            navigateToOrders();
        });
    }

    private void navigateToLogin() {
        navController.navigate(UserFragmentDirections.actionUserFragmentToLoginFragment());
    }

    private void navigateToUpdateUser() {
        navController.navigate(UserFragmentDirections.actionUserFragmentToUpdateUserFragment(theUser));
    }

    private void navigateToOrders() {
        navController.navigate(UserFragmentDirections.actionUserFragmentToOrdersFragment());
    }

    private void getUserInfo() {
        mRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    theUser = task.getResult().getValue(User.class);
                    if (theUser != null) {
                        showUserInfo();
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Người dùng chưa được tạo trên hệ thống.", Toast.LENGTH_SHORT).show();
                }
                progressIndicator.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Lỗi khi tải thông tin.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showUserInfo() {
        String fullName;
        if(theUser.getFirstName()!=null && theUser.getLastName()!=null)
            fullName = theUser.getLastName() + " " + theUser.getFirstName();
        else fullName= theUser.getLastName();
        fullNameTv.setText(fullName.toUpperCase());
        String avatarUrl = theUser.getAvatarUrl();
        if (!(avatarUrl.isEmpty())) {
            Picasso.get().load(avatarUrl).into(avatar);
        }
    }
}