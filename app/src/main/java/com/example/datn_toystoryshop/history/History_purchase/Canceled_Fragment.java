package com.example.datn_toystoryshop.history.History_purchase;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.datn_toystoryshop.Adapter.Order_Canceled_Adapter;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.history.History_purchase_screen;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Canceled_Fragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOrderHistory;
    private Order_Canceled_Adapter adapter;
    private List<Order_Model> orderList = new ArrayList<>();
    private List<Order_Model> filteredOrderList = new ArrayList<>();
    private String documentId;
    private LinearLayout llnot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canceled, container, false);

        // Khởi tạo các thành phần UI
        rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        llnot = view.findViewById(R.id.llnot);
        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");
        }


        // Cấu hình RecyclerView và Adapter
        APIService apiService = RetrofitClient.getAPIService();
        adapter = new Order_Canceled_Adapter(requireContext(), filteredOrderList, apiService, documentId);
        rvOrderHistory.setAdapter(adapter);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Lấy dữ liệu từ API
        fetchOrders();

        swipeRefreshLayout.setOnRefreshListener(this::fetchOrders);

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

    private void fetchOrders() {
        if (documentId == null || documentId.isEmpty()) {
            Log.e("CanceledFragment", "documentId không được để trống");
            return;
        }

        APIService apiService = RetrofitClient.getAPIService();
        Call<List<Order_Model>> call = apiService.getOrders_canceled(documentId);
        call.enqueue(new Callback<List<Order_Model>>() {
            @Override
            public void onResponse(Call<List<Order_Model>> call, Response<List<Order_Model>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    filteredOrderList.clear();
                    filteredOrderList.addAll(orderList);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.GONE);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireContext(), "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                    rvOrderHistory.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Order_Model>> call, Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                rvOrderHistory.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                llnot.setVisibility(View.VISIBLE);
            }
        });
    }

}