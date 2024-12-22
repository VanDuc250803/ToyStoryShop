package com.example.datn_toystoryshop.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Detail.OrderHist_Detail;
import com.example.datn_toystoryshop.Detail.Refund_Detail;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.RefundResponse;
import com.example.datn_toystoryshop.Model.Refund_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Shopping.Cart_screen;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Refund_Adapter extends RecyclerView.Adapter<Order_Refund_Adapter.OrderViewHolder> {
    private Context context;
    private List<Refund_Model> refundList;
    private APIService apiService;
    private String documentId;
    public Order_Refund_Adapter(Context context, List<Refund_Model> refundList, APIService apiService, String documentId) {
        this.context = context;
        this.refundList = refundList;
        this.apiService = apiService;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public Order_Refund_Adapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_return_goods, parent, false);
        return new Order_Refund_Adapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Refund_Adapter.OrderViewHolder holder, int position) {
        Refund_Model refund = refundList.get(position);
        String orderId = refund.getOrderId();
        String refundId = refund.get_id();
        fetchRefundDetails(refundId,orderId,holder);

        // Gọi API
        Call<Order_Model> call = apiService.getOrderById(orderId);
        call.enqueue(new Callback<Order_Model>() {
            @Override
            public void onResponse(Call<Order_Model> call, Response<Order_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order_Model order = response.body();
                    holder.textRevenueAll.setText(String.format(": %,.0fđ", (double) order.getRevenue_all()));

                    // Hiển thị danh sách sản phẩm
                    List<Order_Model.ProductDetail> productDetails = order.getProdDetails();
                    boolean isMoreThanTwo = productDetails.size() > 2;
                    List<Order_Model.ProductDetail> displayProductDetails = isMoreThanTwo ? productDetails.subList(0, 2) : productDetails;

                    Order_Refund_Product_Adapter productAdapter = new Order_Refund_Product_Adapter(context, displayProductDetails, apiService, order.get_id(),documentId,refundId);
                    holder.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));
                    holder.recyclerViewProducts.setAdapter(productAdapter);
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

                } else {
                    // Xử lý khi API trả về lỗi
                    Log.e("Order API", "Không tìm thấy order hoặc dữ liệu null.");
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Xử lý khi lỗi kết nối
                Log.e("Order API", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return refundList.size();
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
    private void fetchRefundDetails(String refundId, String orderId, OrderViewHolder holder) {
        apiService.getRefundByID(refundId).enqueue(new Callback<Refund_Model>() {
            @Override
            public void onResponse(Call<Refund_Model> call, Response<Refund_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Refund_Model refund = response.body();

                        String refundStatus = refund.getRefundStatus();
                        holder.textStatus.setText(refundStatus);
                        if ("Hủy hoàn hàng".equals(refundStatus)) {
                            // Cho phép click
                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(context, OrderHist_Detail.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("documentId", documentId);
                                context.startActivity(intent);
                            });
                            holder.btnBuyBack.setOnClickListener(v -> {
                                Intent intent = new Intent(context, OrderHist_Detail.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("documentId", documentId);
                                context.startActivity(intent);
                            });
                        } else {
                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(context, Refund_Detail.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("refundId", refundId);
                                intent.putExtra("documentId", documentId);
                                context.startActivity(intent);
                            });
                            holder.btnBuyBack.setOnClickListener(v -> {
                                Intent intent = new Intent(context, Refund_Detail.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("refundId", refundId);
                                intent.putExtra("documentId", documentId);
                                context.startActivity(intent);
                            });
                        }
                    } else {
                        Log.e("API", "Không có dữ liệu hoàn hàng.");
                    }
                }


            @Override
            public void onFailure(Call<Refund_Model> call, Throwable t) {
                Log.e("API", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }
}
