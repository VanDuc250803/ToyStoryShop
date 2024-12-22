package com.example.datn_toystoryshop.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Profile.Evaluate_screen;
import com.example.datn_toystoryshop.Profile.Introduce_Friends_screen;
import com.example.datn_toystoryshop.Profile.Privacy_Security_screen;
import com.example.datn_toystoryshop.Profile.Setting_screen;
import com.example.datn_toystoryshop.Profile.Terms_Conditions_screen;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Register_login.SignIn_screen;
import com.example.datn_toystoryshop.Profile.ContactSupport_screen;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Profile_Fragment extends Fragment {

    private TextView tvSettings, tvRate, tvIntroduceFriend, tvTerms, tvLogout, tvname, tvtvinformation, tvPrivacySecurity, tvContactSupport;
    private ImageView ivAvatar;
    private FirebaseFirestore db;
    private static final String PREFS_NAME = "MyPrefs"; // Khai báo hằng số cho tên SharedPreferences
    private static final String NOTIFICATION_BLOCKED_KEY = "isNotificationBlocked"; // Khai báo hằng số cho trạng thái thông báo
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    private String documentId, name, phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvname = view.findViewById(R.id.tv_user_name);
        tvtvinformation = view.findViewById(R.id.tv_information);
        tvSettings = view.findViewById(R.id.tv_settings);
        tvContactSupport = view.findViewById(R.id.tv_contact_support);
        tvRate = view.findViewById(R.id.tv_rate);
        tvIntroduceFriend = view.findViewById(R.id.tv_introducefriend);
        tvTerms = view.findViewById(R.id.tv_terms);
        tvPrivacySecurity = view.findViewById(R.id.tv_privacy_security);
        tvLogout = view.findViewById(R.id.tv_logout);

        sharedPreferences = requireContext().getSharedPreferences("Settings", requireContext().MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Khởi tạo các view từ layout
        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");
        }
        loadUserDataByDocumentId(documentId);

        // Gắn sự kiện cho các TextView
        setOnClickListener(tvSettings, Setting_screen.class, documentId);
        setOnClickListener(tvRate, Evaluate_screen.class, documentId);
        setOnClickListener(tvContactSupport, ContactSupport_screen.class, documentId);
        setOnClickListener(tvIntroduceFriend, Introduce_Friends_screen.class, null);
        setOnClickListener(tvTerms, Terms_Conditions_screen.class, null);
        setOnClickListener(tvPrivacySecurity, Privacy_Security_screen.class, null);


        // Xử lý sự kiện cho mục "Đăng xuất"
        tvLogout.setOnClickListener(v -> {
            // Đăng xuất người dùng
            Toast.makeText(getActivity(), getString(R.string.sign_out_success_pro), Toast.LENGTH_SHORT).show();

            // Đặt lại trạng thái thông báo
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(NOTIFICATION_BLOCKED_KEY, false); // Đặt lại trạng thái thông báo
            editor.apply();

            // Chuyển tới màn hình đăng nhập
            Intent intent = new Intent(getActivity(), SignIn_screen.class);
            startActivity(intent);
            getActivity().finish(); // Đóng tất cả các Activity hiện tại

        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Chuyển về Home_screen
                Intent intent = new Intent(requireActivity(), Home_screen.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;

    }
    // Phương thức xử lý chung
    private void setOnClickListener(TextView textView, Class<?> targetClass, @Nullable String documentId) {
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), targetClass);
            if (documentId != null) {
                intent.putExtra("documentId", documentId);
            }
            startActivity(intent);
        });
    }
    private void loadUserDataByDocumentId(String documentId) {
        DocumentReference docRef = db.collection("users").document(documentId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy tất cả dữ liệu từ tài liệu
                    name = document.getString("name");
                    phoneNumber = document.getString("phoneNumber");

                    // Hiển thị dữ liệu trên log
                    Log.d("UserData", "Name: " + name);
                    Log.d("UserData", "PhoneNumber: " + phoneNumber);

                    // Cập nhật giao diện nếu cần
                    tvname.setText(name);
                    tvtvinformation.setText(phoneNumber);
                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }

}