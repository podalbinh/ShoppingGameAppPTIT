package com.shopping.nhom5.frags;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.User;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user == null) {
            navigateToLogin();
        } else {
            initDbRef(user.getUid());
            getUserInfo();
        }
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

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("users/" + user.getUid());
        mStorageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                overlay.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_LONG).show();
                    mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            theUser.setAvatarUrl(url);
                            Picasso.get().load(url).into(avatar);
                            mRef.setValue(theUser);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Something went wrong! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        uploadProgress.setProgress((int) percent);
                    }
                });
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
        mRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    theUser = task.getResult().getValue(User.class);
                    showUserInfo();
                    progressIndicator.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showUserInfo() {
        String fullName = theUser.getLastName() + " " + theUser.getFirstName();
        fullNameTv.setText(fullName.toUpperCase());
        String avatarUrl = theUser.getAvatarUrl();
        if (!(avatarUrl.isEmpty())) {
            Picasso.get().load(avatarUrl).into(avatar);
        }
    }
}