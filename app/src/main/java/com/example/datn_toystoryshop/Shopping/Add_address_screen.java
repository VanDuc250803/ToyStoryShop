package com.example.datn_toystoryshop.Shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.Model.Address_model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Add_address_screen extends AppCompatActivity {
    private EditText etName, etPhoneNumber, etAddress, etAddressDetail;
    private CheckBox cbDefault;
    private TextView btnSave;
    private ImageView imgBack,imgADD;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private String documentId;
    private static final int LOCATION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_screen);

        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        etAddressDetail = findViewById(R.id.etAddressDetail);
        cbDefault = findViewById(R.id.cbDefault);
        btnSave = findViewById(R.id.btnSave);
        imgADD = findViewById(R.id.imgADD);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        imgBack = findViewById(R.id.btnBack);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imgADD.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Yêu cầu quyền truy cập vị trí
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_REQUEST_CODE);
            } else {
                // Lấy vị trí hiện tại
                getCurrentLocation();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_address_screen.this, AddressList_Screen.class);
                startActivity(intent);
                finish();
            }
        });
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String addressDetail = etAddressDetail.getText().toString().trim();
            boolean isDefault = cbDefault.isChecked();

            // Kiểm tra các trường nhập liệu
            if (!validateInput(name, phone, address, addressDetail)) {
                return;
            }
            // Gọi API để thêm địa chỉ
            addAddressToServer(documentId, name, phone, address, addressDetail, isDefault);

        });
    }
    private boolean validateInput(String name, String phone, String address, String addressDetail) {
        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            etName.requestFocus();
            return false;
        } else if (!name.matches("^[\\u00C0-\\u1EF9a-zA-Z\\s]{2,50}$")) {
            etName.setError("Tên chỉ được chứa ký tự chữ và có độ dài 2-50 ký tự");
            etName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhoneNumber.setError("Số điện thoại không được để trống");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!phone.matches("^0[0-9]{9}$")) {
            etPhoneNumber.setError("Số điện thoại không hợp lệ (bắt đầu bằng 0 và có 10 chữ số)");
            etPhoneNumber.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            etAddress.setError("Địa chỉ không được để trống");
            etAddress.requestFocus();
            return false;
        } else if (address.length() < 5 || address.length() > 100) {
            etAddress.setError("Địa chỉ phải có độ dài từ 5-100 ký tự");
            etAddress.requestFocus();
            return false;
        }

        if (addressDetail.isEmpty()) {
            etAddressDetail.setError("Chi tiết địa chỉ không được để trống");
            etAddressDetail.requestFocus();
            return false;
        } else if (addressDetail.length() < 5 || addressDetail.length() > 100) {
            etAddressDetail.setError("Chi tiết địa chỉ phải có độ dài từ 5-100 ký tự");
            etAddressDetail.requestFocus();
            return false;
        }

        return true;
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Log vị trí
                            getAddressFromLocation(latitude, longitude);
                            Log.d("UserLocation", "Latitude: " + latitude + ", Longitude: " + longitude);
                        } else {
                            Log.e("UserLocation", "Không thể lấy vị trí");

                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);

                // Lấy địa chỉ đầy đủ
                String fullAddress = address.getAddressLine(0);

                // Log địa chỉ hoặc hiển thị lên giao diện
                Log.d("UserAddress", "Địa chỉ: " + fullAddress);
                etAddress.setText(fullAddress); // Hiển thị trong EditText
            } else {
                Log.e("UserAddress", "Không tìm thấy địa chỉ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("UserAddress", "Lỗi khi lấy địa chỉ: " + e.getMessage());
        }
    }


    private void addAddressToServer(String cusId,String name, String phone, String address, String addressDetail, boolean isDefault) {
        // Nếu là địa chỉ mặc định, gọi API để cập nhật các địa chỉ cũ thành không mặc định
        if (isDefault) {
            updateAllAddressesToNonDefault(cusId);
        }

        // Khởi tạo Retrofit client
        APIService apiService = RetrofitClient.getAPIService();

        // Tạo đối tượng địa chỉ để gửi đi
        Address_model newAddressModel = new Address_model(null, cusId, name, phone, address, addressDetail, isDefault);

        // Gọi API POST để thêm địa chỉ
        Call<Address_model> call = apiService.addAddress(newAddressModel);

        call.enqueue(new Callback<Address_model>() {
            @Override
            public void onResponse(Call<Address_model> call, Response<Address_model> response) {
                if (response.isSuccessful()) {
                    // Nếu thành công, trả kết quả về màn hình trước
                    setResult(RESULT_OK);  // Trả kết quả thành công
                    finish();  // Quay lại màn hình AddressList_Screen
                } else {
                    // Nếu thất bại, thông báo lỗi
                    Toast.makeText(Add_address_screen.this, "Có lỗi xảy ra khi thêm địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Address_model> call, Throwable t) {
                // Xử lý lỗi kết nối
                Toast.makeText(Add_address_screen.this, "Lỗi kết nối2222: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API để cập nhật tất cả các địa chỉ có isDefault = true thành false
    private void updateAllAddressesToNonDefault(String cusId) {
        APIService apiService = RetrofitClient.getAPIService();

        Call<ResponseBody> call = apiService.updateDefault(cusId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Địa chỉ mặc định đã được cập nhật thành công
                    Log.d("UpdateDefault", "Tất cả các địa chỉ đã được cập nhật thành không mặc định.");
                } else {
                    // Nếu thất bại, thông báo lỗi
                    Toast.makeText(Add_address_screen.this, "Có lỗi xảy ra khi cập nhật địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý lỗi kết nối
                Log.e("APIError", "Lỗi kết nối: " + t.getMessage(), t);
                Toast.makeText(Add_address_screen.this, "Lỗi kết nối3333: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}