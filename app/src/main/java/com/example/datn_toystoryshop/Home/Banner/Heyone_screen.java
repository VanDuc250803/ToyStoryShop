package com.example.datn_toystoryshop.Home.Banner;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Adapter.Product_No_Star_Adapter;
import com.example.datn_toystoryshop.Home.BlindBox_screen;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Heyone_screen extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Product_No_Star_Adapter adapter;
    private ImageView backIcon;
    private List<Product_Model> productList; // Danh sách hiện tại đang hiển thị trên RecyclerView
    private List<Product_Model> originalProductList; // Danh sách gốc lưu toàn bộ sản phẩm từ API
    private int minPriceLimit = 0;// Giá tối đa là 1.000.000
    private EditText searchBar;



    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private APIService apiService;
    private Button btnSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heyone_screen);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        searchBar = findViewById(R.id.search_bar);
        btnSort = findViewById(R.id.btnSort);
//        if (nightMode) {
//            imgBack.setImageResource(R.drawable.back_icon);
//        } else {
//            imgBack.setImageResource(R.drawable.back_icon_1);
//        }
        recyclerView = findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        Log.e("OrderHistoryAdapter", "j8888888888888888BlindBox_screen" + documentId);
        originalProductList = new ArrayList<>();
        apiService = RetrofitClient.getAPIService();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        apiService.getHeyOne().enqueue(new Callback<List<Product_Model>>() {
            @Override
            public void onResponse(Call<List<Product_Model>> call, Response<List<Product_Model>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Danh sách sản phẩm: " + response.body().toString());
                    originalProductList = new ArrayList<>(response.body()); // Cập nhật danh sách gốc
                    updateBrandCounts();  // Cập nhật số lượng các thương hiệu
                    adapter = new Product_No_Star_Adapter(Heyone_screen.this, originalProductList, documentId);
                    recyclerView.setAdapter(adapter);
                    LoadAPI();
                } else {
                    Log.e("API Response", "Không có dữ liệu");
                    Toast.makeText(Heyone_screen.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product_Model>> call, Throwable t) {
                Toast.makeText(Heyone_screen.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });

        backIcon = findViewById(R.id.ivBack);
        backIcon.setOnClickListener(v -> onBackPressed());
        Button btnFilter = findViewById(R.id.btn_filter); // Nút bộ lọc
        btnFilter.setOnClickListener(v -> showFilterDialog());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            LoadAPI();

        });

        btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Heyone_screen.this);
        String[] sortOptions = {
                "Sắp xếp theo A-Z",
                "Sắp xếp theo Z-A",
                "Giá từ thấp đến cao",
                "Giá từ cao đến thấp"
        };

        builder.setTitle("Chọn cách sắp xếp")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sortProducts("name_asc");
                            break;
                        case 1:
                            sortProducts("name_desc");
                            break;
                        case 2:
                            sortProducts("price_asc");
                            break;
                        case 3:
                            sortProducts("price_desc");
                            break;
                        case 4:
                            sortProducts("date_asc");
                            break;
                        case 5:
                            sortProducts("date_desc");
                            break;
                        default:
                            break;
                    }
                });
        builder.show();
    }
    private String removeDiacritics(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
    private void sortProducts(String sortBy) {
        if (originalProductList == null || originalProductList.isEmpty()) {
            Toast.makeText(Heyone_screen.this, "Không có sản phẩm để sắp xếp.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (sortBy) {
            case "name_asc": // Sắp xếp A-Z
                originalProductList.sort((lhs, rhs) -> {
                    String lhsName = removeDiacritics(lhs.getNamePro() != null ? lhs.getNamePro() : "");
                    String rhsName = removeDiacritics(rhs.getNamePro() != null ? rhs.getNamePro() : "");
                    return lhsName.compareTo(rhsName);
                });
                break;

            case "name_desc": // Sắp xếp Z-A
                originalProductList.sort((lhs, rhs) -> {
                    String lhsName = removeDiacritics(lhs.getNamePro() != null ? lhs.getNamePro() : "");
                    String rhsName = removeDiacritics(rhs.getNamePro() != null ? rhs.getNamePro() : "");
                    return rhsName.compareTo(lhsName);
                });
                break;

            case "price_asc": // Sắp xếp giá từ thấp đến cao
                originalProductList.sort((lhs, rhs) -> Double.compare(lhs.getPrice(), rhs.getPrice()));
                break;

            case "price_desc": // Sắp xếp giá từ cao đến thấp
                originalProductList.sort((lhs, rhs) -> Double.compare(rhs.getPrice(), lhs.getPrice()));
                break;

            case "date_asc": // Sắp xếp ngày từ cũ đến mới
                originalProductList.sort((lhs, rhs) -> {
                    String lhsDateStr = lhs.getCreatDatePro();
                    String rhsDateStr = rhs.getCreatDatePro();

                    // Nếu một trong hai ngày null, để nguyên vị trí
                    if (lhsDateStr == null || rhsDateStr == null) {
                        return 0;
                    }

                    // Parse ngày
                    Date lhsDate = parseDate(lhsDateStr);
                    Date rhsDate = parseDate(rhsDateStr);

                    // Nếu không parse được ngày, để nguyên vị trí
                    if (lhsDate == null || rhsDate == null) {
                        return 0;
                    }

                    return lhsDate.compareTo(rhsDate);
                });
                break;

            case "date_desc": // Sắp xếp ngày từ mới đến cũ
                originalProductList.sort((lhs, rhs) -> {
                    String lhsDateStr = lhs.getCreatDatePro();
                    String rhsDateStr = rhs.getCreatDatePro();

                    // Nếu một trong hai ngày null, để nguyên vị trí
                    if (lhsDateStr == null || rhsDateStr == null) {
                        return 0;
                    }

                    // Parse ngày
                    Date lhsDate = parseDate(lhsDateStr);
                    Date rhsDate = parseDate(rhsDateStr);

                    // Nếu không parse được ngày, để nguyên vị trí
                    if (lhsDate == null || rhsDate == null) {
                        return 0;
                    }

                    return rhsDate.compareTo(lhsDate);
                });
                break;


            default:
                break;
        }

        adapter.updateData(originalProductList); // Cập nhật lại RecyclerView
    }
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null; // Ngày null hoặc rỗng
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Trả về null nếu không parse được
        }
    }

    private  void LoadAPI(){
        // Gọi lại API để làm mới danh sách sản phẩm
        apiService.getHeyOne().enqueue(new Callback<List<Product_Model>>() {
            @Override
            public void onResponse(Call<List<Product_Model>> call, Response<List<Product_Model>> response) {
                swipeRefreshLayout.setRefreshing(false); // Dừng hiệu ứng refresh
                if (response.isSuccessful() && response.body() != null) {
                    originalProductList = response.body(); // Lưu danh sách sản phẩm mới
                    adapter.updateData(originalProductList); // Cập nhật dữ liệu trong adapter
                } else {
                    Toast.makeText(Heyone_screen.this, "Không có dữ liệu mới.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product_Model>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false); // Dừng hiệu ứng refresh
                Toast.makeText(Heyone_screen.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showFilterDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Heyone_screen.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setGravity(Gravity.START);
        dialog.show();

        CheckBox checkboxBrand1 = dialogView.findViewById(R.id.checkbox_brand_1);
        CheckBox checkboxBrand2 = dialogView.findViewById(R.id.checkbox_brand_2);
        CheckBox checkboxBrand3 = dialogView.findViewById(R.id.checkbox_brand_3);
        TextView tvCountBrand1 = dialogView.findViewById(R.id.tv_count_brand_1);
        TextView tvCountBrand2 = dialogView.findViewById(R.id.tv_count_brand_2);
        TextView tvCountBrand3 = dialogView.findViewById(R.id.tv_count_brand_3);
        CheckBox checkboxBrand4 = dialogView.findViewById(R.id.checkbox_brand_4);
        CheckBox checkboxBrand5 = dialogView.findViewById(R.id.checkbox_brand_5);
        CheckBox checkboxBrand6 = dialogView.findViewById(R.id.checkbox_brand_6);
        TextView tvCountBrand4 = dialogView.findViewById(R.id.tv_count_brand_4);
        TextView tvCountBrand5 = dialogView.findViewById(R.id.tv_count_brand_5);
        TextView tvCountBrand6= dialogView.findViewById(R.id.tv_count_brand_6);

        // Hiển thị số lượng sản phẩm theo từng thương hiệu
        tvCountBrand1.setText(String.valueOf(countProductsByBrand("BANPRESTO")));
        tvCountBrand2.setText(String.valueOf(countProductsByBrand("POP MART")));
        tvCountBrand3.setText(String.valueOf(countProductsByBrand("FUNISM")));
        tvCountBrand4.setText(String.valueOf(countProductsByBrand("YOLOPARK")));
        tvCountBrand5.setText(String.valueOf(countProductsByBrand("DZNR")));
        tvCountBrand6.setText(String.valueOf(countProductsByBrand("FUNKO")));


        EditText dialogMaxPrice = dialogView.findViewById(R.id.et_max_price);
        EditText dialogMinPrice = dialogView.findViewById(R.id.et_min_price);
        SeekBar dialogSeekBarMax = dialogView.findViewById(R.id.seekBar_price_max);

        // Thiết lập giá trị mặc định cho Max Price
        dialogMinPrice.setText(minPriceLimit + "đ");
        dialogMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Loại bỏ ký tự "đ" nếu đã có trong giá trị nhập
                String input = s.toString().replace("đ", "").trim();

                // Kiểm tra xem người dùng có nhập giá trị không
                if (!input.isEmpty()) {
                    try {
                        // Chuyển đổi giá trị nhập thành số nguyên
                        int minPrice = Integer.parseInt(input);

                        // Đặt lại văn bản với ký tự "đ" ở cuối
                        dialogMinPrice.removeTextChangedListener(this); // Ngăn chặn vòng lặp vô tận
                        dialogMinPrice.setText(minPrice + "đ");
                        dialogMinPrice.setSelection(dialogMinPrice.getText().length() - 1); // Đặt con trỏ trước ký tự "đ"
                        dialogMinPrice.addTextChangedListener(this);

                        // Kiểm tra điều kiện min > max và xử lý nếu cần
                        int maxPrice = dialogSeekBarMax.getProgress();
                        if (minPrice > maxPrice) {
                            Toast.makeText(Heyone_screen.this, "Giá trị min không được lớn hơn max", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        // Xử lý ngoại lệ nếu có lỗi khi chuyển đổi giá trị
                        dialogMinPrice.setText("0đ");
                    }
                }
            }
        });

        // TextWatcher để thêm ký tự "đ" cho maxPrice
        dialogMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().replace("đ", "").trim();

                if (!input.isEmpty()) {
                    try {
                        int maxPrice = Integer.parseInt(input);
                        dialogMaxPrice.removeTextChangedListener(this);
                        dialogMaxPrice.setText(maxPrice + "đ");
                        dialogMaxPrice.setSelection(dialogMaxPrice.getText().length() - 1);
                        dialogMaxPrice.addTextChangedListener(this);

                        dialogSeekBarMax.setProgress(maxPrice);
                        int minPrice = Integer.parseInt(dialogMinPrice.getText().toString().replace("đ", "").trim());
                        if (minPrice > maxPrice) {
                            Toast.makeText(Heyone_screen.this, "Giá trị min không được lớn hơn max", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        dialogMaxPrice.setText("1000000đ");
                    }
                }
            }
        });


        dialogSeekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                dialogMaxPrice.setText(progress + "đ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        TextView btnApplyFilter = dialogView.findViewById(R.id.btn_apply_filter);
        btnApplyFilter.setOnClickListener(v -> {
            boolean isBrand1Selected = checkboxBrand1.isChecked();
            boolean isBrand2Selected = checkboxBrand2.isChecked();
            boolean isBrand3Selected = checkboxBrand3.isChecked();
            boolean isBrand4Selected = checkboxBrand4.isChecked();
            boolean isBrand5Selected = checkboxBrand5.isChecked();
            boolean isBrand6Selected = checkboxBrand6.isChecked();

            // Lấy giá trị từ EditText hoặc SeekBar cho minPrice và maxPrice
            int minPrice = Integer.parseInt(dialogMinPrice.getText().toString().replace("đ", "").trim()); // Loại bỏ "đ" và khoảng trắng
            int maxPrice = dialogSeekBarMax.getProgress(); // Giá trị từ SeekBar max

            // Kiểm tra xem có ít nhất một thương hiệu được chọn
            if (!(isBrand1Selected || isBrand2Selected || isBrand3Selected || isBrand4Selected || isBrand5Selected || isBrand6Selected)) {
                // Nếu không chọn thương hiệu nhưng đã chọn giá, tìm kiếm theo khoảng giá
                if (minPrice != 0 || maxPrice != 0) {
                    // Tìm kiếm trong tất cả sản phẩm theo khoảng giá
                    applyFilterForAllProducts(minPrice, maxPrice);
                    dialog.dismiss();
                } else {
                    Toast.makeText(Heyone_screen.this, "Vui lòng chọn ít nhất một thương hiệu hoặc một khoảng giá!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu có ít nhất một thương hiệu được chọn, áp dụng bộ lọc theo thương hiệu và khoảng giá
                applyFilter(isBrand1Selected, isBrand2Selected, isBrand3Selected, isBrand4Selected, isBrand5Selected, isBrand6Selected, minPrice, maxPrice);
                dialog.dismiss();
            }
        });


    }
    private void applyFilterForAllProducts(int minPrice, int maxPrice) {
        if (originalProductList == null) {
            Toast.makeText(this, "Danh sách sản phẩm chưa được tải.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Product_Model> filteredList = new ArrayList<>();
        for (Product_Model product : originalProductList) {
            int price = (int) product.getPrice();

            // Kiểm tra khoảng giá
            if ((minPrice == 0 || price >= minPrice) && (maxPrice == 0 || price <= maxPrice)) {
                filteredList.add(product);
            }
        }

        // Kiểm tra nếu không có sản phẩm nào phù hợp với bộ lọc giá
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm phù hợp với bộ lọc giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật Adapter với danh sách sản phẩm đã lọc
        adapter.updateData(filteredList);
    }

    private void applyFilter(boolean isBrand1Selected, boolean isBrand2Selected, boolean isBrand3Selected,boolean isBrand4Selected, boolean isBrand5Selected, boolean isBrand6Selected, int minPrice, int maxPrice) {
        if (originalProductList == null) {
            Toast.makeText(this, "Danh sách sản phẩm chưa được tải.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasBrand1 = false, hasBrand2 = false, hasBrand3 = false ,hasBrand4 = false, hasBrand5 = false, hasBrand6 = false;

        // Kiểm tra xem có sản phẩm thuộc thương hiệu được chọn hay không
        for (Product_Model product : originalProductList) {
            String brand = product.getBrand().trim();

            if (brand.equals("BANPRESTO")) hasBrand1 = true;
            if (brand.equals("POP MART")) hasBrand2 = true;
            if (brand.equals("FUNISM")) hasBrand3 = true;
            if (brand.equals("YOLOPARK")) hasBrand4 = true;
            if (brand.equals("DZNR")) hasBrand5 = true;
            if (brand.equals("FUNKO")) hasBrand6 = true;
        }

        // Hiển thị thông báo nếu không có sản phẩm nào thuộc thương hiệu đã chọn và dừng tiến trình lọc
        if (isBrand1Selected && !hasBrand1) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu BANPRESTO.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBrand2Selected && !hasBrand2) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu POP MART.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBrand3Selected && !hasBrand3) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu FUNISM.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBrand4Selected && !hasBrand4) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu YOLOPARK.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBrand5Selected && !hasBrand5) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu DZNR.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBrand6Selected && !hasBrand6) {
            Toast.makeText(this, "Không có sản phẩm thuộc thương hiệu FUNKO.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu có sản phẩm phù hợp, tiếp tục lọc
        List<Product_Model> filteredList = new ArrayList<>();
        for (Product_Model product : originalProductList) {
            String brand = product.getBrand().trim();
            int price = (int) product.getPrice();

            if ((isBrand1Selected && brand.equals("BANPRESTO")) ||
                    (isBrand2Selected && brand.equals("POP MART")) ||
                    (isBrand3Selected && brand.equals("FUNISM"))||
                    (isBrand4Selected && brand.equals("YOLOPARK")) ||
                    (isBrand5Selected && brand.equals("DZNR")) ||
                    (isBrand6Selected && brand.equals("FUNKO"))) {
                if ((minPrice == 0 || price >= minPrice) && (maxPrice == 0 || price <= maxPrice)) {
                    filteredList.add(product);
                }
            }
        }

        // Kiểm tra nếu không có sản phẩm nào phù hợp với bộ lọc giá
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm phù hợp với bộ lọc giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật Adapter với danh sách sản phẩm đã lọc
        adapter.updateData(filteredList);
    }

    private int countProductsByBrand(String brandName) {
        int count = 0;

        if (originalProductList != null) {
            for (Product_Model product : originalProductList) {
                String brand = product.getBrand().trim(); // Loại bỏ khoảng trắng thừa
                Log.d("Brand Count", "Checking product: " + brand);  // Debugging để xem brand hiện tại

                if (brand.equalsIgnoreCase(brandName)) {
                    count++;
                }
            }
        } else {
            Log.e("Brand Count", "originalProductList is null");
        }

        Log.d("Brand Count", "Total count for " + brandName + ": " + count); // Debugging tổng số
        return count;
    }
    // Lưu trữ số lượng các thương hiệu trong danh sách gốc
    private Map<String, Integer> brandCounts = new HashMap<>();

    private void updateBrandCounts() {
        brandCounts.clear();
        for (Product_Model product : originalProductList) {
            String brand = product.getBrand().trim();
            brandCounts.put(brand, brandCounts.getOrDefault(brand, 0) + 1);
        }
    }
}