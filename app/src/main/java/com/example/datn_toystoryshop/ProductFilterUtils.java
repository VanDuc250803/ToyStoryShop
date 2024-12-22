package com.example.datn_toystoryshop;

import com.example.datn_toystoryshop.Model.Product_Model;

import java.util.ArrayList;
import java.util.List;

public class ProductFilterUtils {
    public static List<Product_Model> filterProducts(List<Product_Model> originalList,
                                                     boolean isBrand1Selected, boolean isBrand2Selected, boolean isBrand3Selected,
                                                     int minPrice, int maxPrice) {
        List<Product_Model> filteredList = new ArrayList<>();

        for (Product_Model product : originalList) {
            String brand = product.getBrand().trim();
            int price = (int) product.getPrice();

            // Kiểm tra nếu sản phẩm phù hợp với thương hiệu và khoảng giá
            if ((isBrand1Selected && brand.equals("BANPRESTO")) ||
                    (isBrand2Selected && brand.equals("POP MART")) ||
                    (isBrand3Selected && brand.equals("FUNISM"))) {

                if ((minPrice == 0 || price >= minPrice) && (maxPrice == 0 || price <= maxPrice)) {
                    filteredList.add(product);
                }
            }
        }

        return filteredList;
    }

    public static int countProductsByBrand(List<Product_Model> productList, String brandName) {
        int count = 0;

        for (Product_Model product : productList) {
            String brand = product.getBrand().trim();
            if (brand.equalsIgnoreCase(brandName)) {
                count++;
            }
        }

        return count;
    }
}
