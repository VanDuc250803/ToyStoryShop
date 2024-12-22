package com.example.datn_toystoryshop;

import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.datn_toystoryshop.Model.Product_Model;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilterFragment extends Fragment {

    protected List<Product_Model> originalProductList = new ArrayList<>();
    private int maxPriceLimit = 10000000; // Giá tối đa mặc định
    private int minPriceLimit = 0;       // Giá tối thiểu mặc định

    protected void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setGravity(Gravity.START);
        dialog.show();

        // Lấy các checkbox và view hiển thị số lượng
        CheckBox checkboxBrand1 = dialogView.findViewById(R.id.checkbox_brand_1);
        CheckBox checkboxBrand2 = dialogView.findViewById(R.id.checkbox_brand_2);
        CheckBox checkboxBrand3 = dialogView.findViewById(R.id.checkbox_brand_3);
        TextView brand1Count = dialogView.findViewById(R.id.tv_count_brand_1);
        TextView brand2Count = dialogView.findViewById(R.id.tv_count_brand_2);
        TextView brand3Count = dialogView.findViewById(R.id.tv_count_brand_3);

        EditText dialogMinPrice = dialogView.findViewById(R.id.et_min_price);
        EditText dialogMaxPrice = dialogView.findViewById(R.id.et_max_price);
        SeekBar dialogSeekBarMax = dialogView.findViewById(R.id.seekBar_price_max);

        // Hiển thị số lượng sản phẩm
        brand1Count.setText(String.format("(%d)", countProductsByBrand("BANPRESTO")));
        brand2Count.setText(String.format("(%d)", countProductsByBrand("POP MART")));
        brand3Count.setText(String.format("(%d)", countProductsByBrand("FUNISM")));

        // Xử lý khi áp dụng bộ lọc
        Button btnApplyFilter = dialogView.findViewById(R.id.btn_apply_filter);
        btnApplyFilter.setOnClickListener(v -> {
            boolean isBrand1Selected = checkboxBrand1.isChecked();
            boolean isBrand2Selected = checkboxBrand2.isChecked();
            boolean isBrand3Selected = checkboxBrand3.isChecked();

            // Lấy giá trị từ EditText
            String minPriceInput = dialogMinPrice.getText().toString().replace("đ", "").trim();
            String maxPriceInput = dialogMaxPrice.getText().toString().replace("đ", "").trim();

            // Gán giá trị mặc định nếu trường rỗng
            int minPrice = minPriceInput.isEmpty() ? minPriceLimit : Integer.parseInt(minPriceInput);
            int maxPrice = maxPriceInput.isEmpty() ? maxPriceLimit : Integer.parseInt(maxPriceInput);

            // Kiểm tra giá trị min và max
            if (!(isBrand1Selected || isBrand2Selected || isBrand3Selected)) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một thương hiệu!", Toast.LENGTH_SHORT).show();
            } else if (minPrice > maxPrice) {
                Toast.makeText(getContext(), "Giá trị min không được lớn hơn max!", Toast.LENGTH_SHORT).show();
            } else {
                // Áp dụng bộ lọc
                Log.d("FilterDebug", "Brands selected: " + isBrand1Selected + ", " + isBrand2Selected + ", " + isBrand3Selected);
                Log.d("FilterDebug", "Min Price: " + minPrice + ", Max Price: " + maxPrice);
                applyFilter(isBrand1Selected, isBrand2Selected, isBrand3Selected, minPrice, maxPrice);
                dialog.dismiss();
            }
        });
    }

    protected void applyFilter(boolean isBrand1Selected, boolean isBrand2Selected, boolean isBrand3Selected, int minPrice, int maxPrice) {
        List<Product_Model> filteredList = new ArrayList<>();

        if (!isBrand1Selected && !isBrand2Selected && !isBrand3Selected) {
            filteredList.addAll(originalProductList);
        } else {
            for (Product_Model product : originalProductList) {
                String brand = product.getBrand().trim();
                int price = (int) product.getPrice();

                if ((isBrand1Selected && brand.equalsIgnoreCase("BANPRESTO")) ||
                        (isBrand2Selected && brand.equalsIgnoreCase("POP MART")) ||
                        (isBrand3Selected && brand.equalsIgnoreCase("FUNISM"))) {
                    if ((minPrice == 0 || price >= minPrice) &&
                            (maxPrice == 0 || price <= maxPrice)) {
                        filteredList.add(product);
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Không có sản phẩm phù hợp với bộ lọc giá.", Toast.LENGTH_SHORT).show();
        }

        updateProductAdapter(filteredList);
        Log.d("FilterDebug", "Original Product List size: " + (originalProductList != null ? originalProductList.size() : 0));

    }

    protected int countProductsByBrand(String brandName) {
        int count = 0;

        if (originalProductList != null) {
            for (Product_Model product : originalProductList) {
                String brand = product.getBrand().trim();
                if (brand.equalsIgnoreCase(brandName)) {
                    count++;
                }
            }
        }
        return count;
    }

    protected abstract void updateProductAdapter(List<Product_Model> filteredList);
}
