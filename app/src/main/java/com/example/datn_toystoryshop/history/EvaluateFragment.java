package com.example.datn_toystoryshop.history;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.datn_toystoryshop.Adapter.FeedbackAdapter;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Feeback_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_feedback;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluateFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Product_feedback> orderList = new ArrayList<>();
    private List<Product_feedback> filteredOrderList = new ArrayList<>();
    private String documentId;
    private APIService apiService;
    private LinearLayout llnot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluate, container, false);

        recyclerView = view.findViewById(R.id.rvOrderHistory);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        llnot = view.findViewById(R.id.llnot);
        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");

        }
        apiService = RetrofitClient.getAPIService();
        // Khởi tạo RecyclerView và Adapter cho feedback
        feedbackAdapter = new FeedbackAdapter(getContext(), orderList,apiService, documentId, new FeedbackAdapter.FeedbackUpdateCallback() {
            @Override
            public void onFeedbackSubmitted() {
                getFeedbackData(documentId); // Gọi lại API để làm mới danh sách
            }
        });
        recyclerView.setAdapter(feedbackAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Gọi API để lấy feedback
        getFeedbackData(documentId);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getFeedbackData(documentId); // Gọi lại API để làm mới danh sách
        });

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


    // Hàm gọi API để lấy feedback
    private void getFeedbackData(String cusId) {
        apiService.getAllProductDetails(cusId).enqueue(new Callback<List<Product_feedback>>() {
            @Override
            public void onResponse(Call<List<Product_feedback>> call, Response<List<Product_feedback>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    feedbackAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (Product_feedback product : orderList) {
                        Log.d("Product", "Tên sản phẩm: " + product.getNamePro());
                        Log.d("Product", "Doanh thu: " + product.getRevenue());
                        Log.d("Product", "id: " + product.getProdId());
                        Log.d("Product", "Hình ảnh: " + product.getImgPro().toString());
                    }
                } else {
                    Log.e("EvaluateFragment", "Failed to load product details");
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Product_feedback>> call, Throwable t) {
                Log.e("EvaluateFragment", "Error: " + t.getMessage());
                t.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                llnot.setVisibility(View.VISIBLE);
            }
        });
    }

    }