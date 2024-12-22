package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Detail.OrderHist_Detail;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Shopping.Cart_screen;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Canceled_Adapter extends RecyclerView.Adapter<Order_Canceled_Adapter.OrderViewHolder> {
    private Context context;
    private List<Order_Model> orderList;
    private APIService apiService;
    private String documentId;
    public Order_Canceled_Adapter(Context context, List<Order_Model> orderList, APIService apiService,String documentId) {
        this.context = context;
        this.orderList = orderList;
        this.apiService = apiService;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public Order_Canceled_Adapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_canceled, parent, false);
        return new Order_Canceled_Adapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Canceled_Adapter.OrderViewHolder holder, int position) {
        Order_Model order = orderList.get(position);

        holder.textStatus.setText(order.getOrderStatus());
        holder.textRevenueAll.setText(String.format(": %,.0fđ", (double) order.getRevenue_all()));

        List<Order_Model.ProductDetail> productDetails = order.getProdDetails();
        boolean isMoreThanTwo = productDetails.size() > 2;

        // Hiển thị tối đa 2 sản phẩm đầu tiên
        List<Order_Model.ProductDetail> displayProductDetails = isMoreThanTwo ? productDetails.subList(0, 2) : productDetails;

        // Setup RecyclerView con để hiển thị sản phẩm
        OrderHistoryProductAdapter productAdapter = new OrderHistoryProductAdapter(context, displayProductDetails,apiService, order.get_id());
        holder.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewProducts.setAdapter(productAdapter);

//                 Thiết lập sự kiện click để mở màn hình chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            // Chuyển đến màn hình chi tiết sản phẩm
            Intent intent = new Intent(context, OrderHist_Detail.class);
            intent.putExtra("orderId", order.get_id());
            context.startActivity(intent);
        });
        holder.btnBuyBack.setOnClickListener(v -> {
            for (Order_Model.ProductDetail productDetail : order.getProdDetails()) {
                String productId = productDetail.getProdId();
                int currentQuantity = productDetail.getQuantity();
                String selectedColor = productDetail.getProdSpecification();

                // Gọi hàm addToCart với thông tin từ sản phẩm
                addToCart(productId, currentQuantity, documentId, selectedColor);
            }
        });


        // Hiển thị/ẩn nút "Xem thêm" dựa vào số lượng sản phẩm
        if (isMoreThanTwo) {
            holder.showMoreOrder.setVisibility(View.VISIBLE);

            // Flag để theo dõi trạng thái của nút "Xem thêm"
            final boolean[] isExpanded = {false};

            holder.showMoreOrder.setOnClickListener(v -> {
                if (isExpanded[0]) {
                    // Nếu đang mở rộng, thu gọn lại về 2 sản phẩm đầu tiên
                    productAdapter.updateProductList(productDetails.subList(0, 2));
                    holder.showMoreOrder.setText("Xem thêm"); // Đổi text về "Xem thêm"
                } else {
                    // Nếu đang thu gọn, hiển thị tất cả sản phẩm
                    productAdapter.updateProductList(productDetails);
                    holder.showMoreOrder.setText("Thu gọn"); // Đổi text thành "Thu gọn"
                }
                isExpanded[0] = !isExpanded[0]; // Đảo trạng thái
            });
        } else {
            holder.showMoreOrder.setVisibility(View.GONE); // Ẩn nút "Xem thêm" nếu không có nhiều hơn 2 sản phẩm
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textStatus, textRevenueAll, showMoreOrder,btnBuyBack ;
        RecyclerView recyclerViewProducts;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textStatus = itemView.findViewById(R.id.tvOrderStatus);
            showMoreOrder  = itemView.findViewById(R.id.show_more_oder);
            textRevenueAll = itemView.findViewById(R.id.tvTotalPrice);
            btnBuyBack = itemView.findViewById(R.id.btnBuyBack);
            recyclerViewProducts = itemView.findViewById(R.id.rvProductList);
        }
    }
    private void addToCart(String productId, int currentQuantity, String documentId, String selectedColor) {

        Cart_Model cartModel = new Cart_Model(
                productId,                 // ID của sản phẩm
                currentQuantity,        // Số lượng sản phẩm
                documentId,                   // ID khách hàng (thay thế bằng ID thực tế của người dùng)
                selectedColor              // Thông số sản phẩm (ví dụ: màu sắc đã chọn)
        );

        // Gọi API để thêm sản phẩm vào giỏ hàng
        Call<Cart_Model> call = apiService.addToCart(cartModel);
        call.enqueue(new Callback<Cart_Model>() {
            @Override
            public void onResponse(Call<Cart_Model> call, Response<Cart_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyển đến màn hình Cart_screen
                    Intent intent = new Intent(context, Cart_screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("documentId", documentId);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Không thể thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart_Model> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
