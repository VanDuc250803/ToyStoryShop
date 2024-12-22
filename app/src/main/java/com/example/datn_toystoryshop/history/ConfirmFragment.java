package com.example.datn_toystoryshop.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.datn_toystoryshop.Adapter.Order_Confirm_Adapter;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinnerMonth, spinnerYear;
    private RecyclerView recyclerView;
    private Order_Confirm_Adapter adapter;
    private List<Order_Model> orderList = new ArrayList<>();
    private List<Order_Model> filteredOrderList = new ArrayList<>();
    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private LinearLayout llnot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear = view.findViewById(R.id.spinnerYear);
        recyclerView = view.findViewById(R.id.rvOrderHistory);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        llnot = view.findViewById(R.id.llnot);
        sharedPreferences = requireContext().getSharedPreferences("Settings", requireContext().MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");
        }
        // Khởi tạo RecyclerView và Adapter
        setUpSpinners();
        APIService apiService = RetrofitClient.getAPIService();
        adapter = new Order_Confirm_Adapter(getContext(), filteredOrderList, apiService, documentId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Gọi API để lấy danh sách đơn hàng
        fetchOrders();
        // Xử lý sự kiện khi người dùng chọn tháng hoặc năm
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterOrders(); // Lọc lại danh sách khi chọn tháng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterOrders(); // Lọc lại danh sách khi chọn năm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchOrders(); // Gọi lại API để làm mới danh sách
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
    private void setUpSpinners() {
        // Thiết lập Adapter cho Spinner tháng
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Thiết lập Adapter cho Spinner năm
        ArrayList<String> years = new ArrayList<>();
        for (int i = 2024; i <= 2030; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void fetchOrders() {
        String cusId = documentId;
        Log.e("FavoriteScreen", "cusId không được để trống " + cusId);
        if (cusId == null || cusId.isEmpty()) {
            Log.e("FavoriteScreen", "cusId không được để trống");
            return;
        }
        APIService apiService = RetrofitClient.getAPIService();
        Call<List<Order_Model>> call = apiService.getOrders_Confirm(cusId);
        call.enqueue(new Callback<List<Order_Model>>() {
            @Override
            public void onResponse(Call<List<Order_Model>> call, Response<List<Order_Model>> response) {
                if (getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        orderList.clear();
                        orderList.addAll(response.body());
                        filteredOrderList.clear();
                        filteredOrderList.addAll(orderList);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        llnot.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        llnot.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order_Model>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void filterOrders() {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();

        // Kiểm tra nếu người dùng chưa chọn tháng (chọn "...")
        if (selectedMonth.equals("...")) {
            filteredOrderList.clear();
            filteredOrderList.addAll(orderList);
        } else {
            String monthNumber = convertMonthNameToNumber(selectedMonth);
            filteredOrderList.clear();

            for (Order_Model order : orderList) {
                String orderDate = order.getOrderDate();
                String orderMonth = orderDate.substring(5, 7);
                String orderYear = orderDate.substring(0, 4);

                if (orderMonth.equals(monthNumber) && orderYear.equals(selectedYear)) {
                    filteredOrderList.add(order);
                }
            }
        }

        if (getContext() != null) {
            adapter.notifyDataSetChanged();
        }
    }



    // Phương thức chuyển đổi tên tháng sang số
    private String convertMonthNameToNumber(String monthName) {
        switch (monthName) {
            case "Tháng 1":
                return "01";
            case "Tháng 2":
                return "02";
            case "Tháng 3":
                return "03";
            case "Tháng 4":
                return "04";
            case "Tháng 5":
                return "05";
            case "Tháng 6":
                return "06";
            case "Tháng 7":
                return "07";
            case "Tháng 8":
                return "08";
            case "Tháng 9":
                return "09";
            case "Tháng 10":
                return "10";
            case "Tháng 11":
                return "11";
            case "Tháng 12":
                return "12";
            default:
                return "01"; // Mặc định trả về "01" nếu không hợp lệ
        }
    }

}