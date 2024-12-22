package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Detail.Order_Confirm_Detail;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Profile.ContactSupport_screen;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;

import java.util.List;

public class Order_Confirm_Adapter extends RecyclerView.Adapter<Order_Confirm_Adapter.OrderViewHolder> {
    private Context context;
    private List<Order_Model> orderList;
    private APIService apiService;
    private String documentId;

    public Order_Confirm_Adapter(Context context, List<Order_Model> orderList, APIService apiService, String documentId) {
        this.context = context;
        this.orderList = orderList;
        this.apiService = apiService;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public Order_Confirm_Adapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new Order_Confirm_Adapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Confirm_Adapter.OrderViewHolder holder, int position) {
        Order_Model order = orderList.get(position);

        holder.textStatus.setText(order.getOrderStatus());
        holder.textRevenueAll.setText(String.format(": %,.0f Đ", (double) order.getRevenue_all()));

        List<Order_Model.ProductDetail> productDetails = order.getProdDetails();
        boolean isMoreThanTwo = productDetails.size() > 2;

        // Hiển thị tối đa 2 sản phẩm đầu tiên
        List<Order_Model.ProductDetail> displayProductDetails = isMoreThanTwo ? productDetails.subList(0, 2) : productDetails;

        Order_Confirm_Product_Adapter productAdapter = new Order_Confirm_Product_Adapter(context, displayProductDetails,apiService, order.get_id(), documentId);
        holder.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewProducts.setAdapter(productAdapter);

//                 Thiết lập sự kiện click để mở màn hình chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            // Chuyển đến màn hình chi tiết sản phẩm
            Intent intent = new Intent(context, Order_Confirm_Detail.class);
            intent.putExtra("orderId", order.get_id());
            context.startActivity(intent);
        });
        // Sự kiện liên hệ shop
        holder.textContactShop.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContactSupport_screen.class);
            context.startActivity(intent);
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
        TextView textStatus, textRevenueAll, textContactShop, showMoreOrder;
        RecyclerView recyclerViewProducts;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textStatus = itemView.findViewById(R.id.tvOrderStatus);
            textRevenueAll = itemView.findViewById(R.id.tvTotalPrice);
            showMoreOrder  = itemView.findViewById(R.id.show_more_oder);
            textContactShop = itemView.findViewById(R.id.btnContactShop);
            recyclerViewProducts = itemView.findViewById(R.id.rvProductList);
        }
    }
}
