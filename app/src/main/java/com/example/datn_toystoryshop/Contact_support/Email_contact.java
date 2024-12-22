package com.example.datn_toystoryshop.Contact_support;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Profile.ContactSupport_screen;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Email_contact extends AppCompatActivity {
    ImageView imgBack;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private String documentId, email;
    private TextView etEmail, tvAttachmentLabel;
    private Uri imageUri;
    private FirebaseFirestore db;
    private Button btnSend, btnAttachFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_contact);

        imgBack = findViewById(R.id.imgBackEm);
        etEmail = findViewById(R.id.etEmail);
        btnAttachFile = findViewById(R.id.btnAttachFile);
        btnSend = findViewById(R.id.btnSend);
        tvAttachmentLabel = findViewById(R.id.tvAttachmentLabel);

        db = FirebaseFirestore.getInstance();
        documentId = getIntent().getStringExtra("documentId");
        loadUserDataByDocumentId(documentId);
        Spinner serviceSpinner = findViewById(R.id.spService);
        String[] services = {
                " - Chọn loại - ",
                "Hỗ trợ giao hàng",
                "Đổi trả sản phẩm",
                "Bảo hành sản phẩm",
                "Hỗ trợ thanh toán",
                "Giải đáp thắc mắc"
        };
        // ArrayAdapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(adapter);

        // Sự kiện chọn Spinner
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedService = parent.getItemAtPosition(position).toString();
                Toast.makeText(Email_contact.this, "Bạn đã chọn: " + selectedService, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }

        imgBack.setOnClickListener(v -> onBackPressed());
        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở trình chọn ảnh
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");  // Chỉ chọn ảnh
                startActivityForResult(intent, 1);  // Yêu cầu trả kết quả từ Activity này
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ màn hình
                String senderEmail = etEmail.getText().toString().trim();
                String selectedService = ((Spinner) findViewById(R.id.spService)).getSelectedItem().toString();
                String subject = ((TextView) findViewById(R.id.etSubject)).getText().toString().trim();
                String description = ((TextView) findViewById(R.id.etDescription)).getText().toString().trim();

                // Kiểm tra dữ liệu hợp lệ
                if (senderEmail.isEmpty() || subject.isEmpty() || description.isEmpty() || selectedService.equals(" - Chọn loại - ")) {
                    Toast.makeText(Email_contact.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo nội dung email
                String emailBody = "Người gửi: " + senderEmail + "\n"
                        + "Dịch vụ: " + selectedService + "\n\n"
                        + "Nội dung:\n" + description;

                // Tạo Intent gửi email
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822"); // Chỉ định ứng dụng email
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toystory.shop.datn@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
                if (imageUri != null) {
                    emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);  // Đính kèm ảnh
                }
                try {
                    // Bắt đầu Intent
                    startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // Trường hợp không có ứng dụng email
                    Toast.makeText(Email_contact.this, "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserDataByDocumentId(String documentId) {
        DocumentReference docRef = db.collection("users").document(documentId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy tất cả dữ liệu từ tài liệu
                    email = document.getString("email");
                    etEmail.setText(email);

                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();  // Lấy URI của ảnh đã chọn

                // Bạn có thể sử dụng imageUri để đính kèm ảnh vào email
                // Nếu muốn, bạn có thể lưu URI này hoặc lấy đường dẫn thực tế từ URI
                String imagePath = getPathFromURI(imageUri);
                tvAttachmentLabel.setText(imagePath);
                // Bạn có thể hiển thị hoặc xử lý ảnh ở đây (nếu muốn) trước khi gửi
                Log.d("Selected Image", "Image URI: " + imageUri.toString());

            }
        }
    }

    private String getPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

}