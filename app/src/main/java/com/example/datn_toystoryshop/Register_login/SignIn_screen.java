package com.example.datn_toystoryshop.Register_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_toystoryshop.Home.Banner.SquidGame_screen;
import com.example.datn_toystoryshop.Home.Banner.Toys_52_screen;
import com.example.datn_toystoryshop.Home.BlindBox_screen;
import com.example.datn_toystoryshop.Home.LimitedFigure_screen;
import com.example.datn_toystoryshop.Home.Sale_screen;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignIn_screen extends AppCompatActivity {
    private TextInputEditText edInput, edPassword;
    private Button btnGoogleLogin1;
    private ImageView btnGoogleLogin2;
    private TextView txtSignup, txtForgotPass, btnLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout btnGoogleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        edInput = findViewById(R.id.edemailLg); // Nhập email hoặc số điện thoại
        edPassword = findViewById(R.id.edpasswordLg);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnGoogleLogin1 = findViewById(R.id.btnGoogleLogin1);
        btnGoogleLogin2 = findViewById(R.id.btnGoogleLogin2);
        txtSignup = findViewById(R.id.txtSignup);
        txtForgotPass = findViewById(R.id.txtForgotPass);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cấu hình Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Truyền dữ liệu từ ForgotOTP_screen
        Intent intent = getIntent();
        if (intent != null) {
            String email = intent.getStringExtra("email");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String password = intent.getStringExtra("password");
            String newPassword = intent.getStringExtra("newPassword");
            if (phoneNumber != null && phoneNumber.startsWith("+84")) {
                phoneNumber = phoneNumber.replaceFirst("\\+84", "0");
            }
            // Gán dữ liệu vào các TextInputEditText
            if (email != null) {
                edInput.setText(email);
            }
            if (phoneNumber != null) {
                edInput.setText(phoneNumber);
            }
            if (password != null) {
                edPassword.setText(password);
            }
            if (newPassword != null) {
                edPassword.setText(newPassword);
            }
        }
        edInput.setText("0382200084");
        edPassword.setText("A111111");
        // Đăng nhập bằng Google
        btnGoogleLogin.setOnClickListener(view -> signInWithGoogle());
        btnGoogleLogin1.setOnClickListener(view -> signInWithGoogle());
        btnGoogleLogin2.setOnClickListener(view -> signInWithGoogle());

        // Đăng nhập bằng email/số điện thoại
        btnLogin.setOnClickListener(view -> {
            String input = edInput.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (input.isEmpty()) {
                Toast.makeText(SignIn_screen.this, getString(R.string.Toast_infor), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isPasswordValid(password)) {
                Toast.makeText(SignIn_screen.this, getString(R.string.Toast_pass), Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPhoneNumber(input)) {
                loginWithPhone(input, password);
            } else if (isValidEmail(input)) {
                loginWithEmail(input, password);
            } else {
                Toast.makeText(SignIn_screen.this, getString(R.string.Toast_format), Toast.LENGTH_SHORT).show();
            }
        });

        // Chuyển đến màn hình đăng ký
        txtSignup.setOnClickListener(view -> {
            Intent in = new Intent(SignIn_screen.this, SignUp_screen.class);
            startActivity(in);
        });

        // Chuyển đến màn hình quên mật khẩu
        txtForgotPass.setOnClickListener(view -> {
            Intent in = new Intent(SignIn_screen.this, Forgot_pass.class);
            startActivity(in);
        });
    }

    // Xử lý đăng nhập Google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Xử lý kết quả đăng nhập Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Xử lý kết quả đăng nhập Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, getString(R.string.Toast_google), Toast.LENGTH_SHORT).show();
        }
    }

    // Đăng nhập Google với Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String gmail = acct.getEmail();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", gmail);
                        userData.put("name", user.getDisplayName());
                        userData.put("phoneNumber", user.getPhoneNumber());
                        userData.put("password", "");

                        db.collection("users")
                                .add(userData)
                                .addOnSuccessListener(documentReference -> {
                                    String documentId = documentReference.getId();
                                    Toast.makeText(SignIn_screen.this, getString(R.string.Toast_success), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignIn_screen.this, Home_screen.class);
                                    intent.putExtra("documentId", documentId);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                      Toast.makeText(SignIn_screen.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(SignIn_screen.this, getString(R.string.Toast_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Đăng nhập bằng số điện thoại
    private void loginWithPhone(String phoneNumber, String password) {
        if (!phoneNumber.startsWith("+84")) {
            phoneNumber = "+84" + phoneNumber.substring(1);
        }
        String finalPhoneNumber = phoneNumber;
        db.collection("users")
                .whereEqualTo("phoneNumber", finalPhoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            DocumentSnapshot document = documents.get(0);
                            String storedPassword = document.getString("password");
                            if (storedPassword != null && BCrypt.checkpw(password, storedPassword)) {                                Toast.makeText(SignIn_screen.this, getString(R.string.Toast_success), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignIn_screen.this, Home_screen.class);
                                String documentId = document.getId();
                                intent.putExtra("documentId", documentId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignIn_screen.this, getString(R.string.Toast_wrong_password), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignIn_screen.this, getString(R.string.Toast_wrong_sdt), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignIn_screen.this, getString(R.string.Toast_wrong_sdt), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Đăng nhập bằng email
    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Toast.makeText(SignIn_screen.this, getString(R.string.Toast_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn_screen.this, Home_screen.class);
                            intent.putExtra("documentId", uid);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(SignIn_screen.this, getString(R.string.Toast_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Kiểm tra số điện thoại
    private boolean isPhoneNumber(String input) {
        return Patterns.PHONE.matcher(input).matches();
    }

    // Kiểm tra độ dài mật khẩu
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
}
