package com.example.datn_toystoryshop.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_toystoryshop.Adapter.OrderHistoryAdapter;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.history.ConfirmFragment;
//import com.example.datn_toystoryshop.history.DeliveryFragment;
//import com.example.datn_toystoryshop.history.EvaluateFragment;
//import com.example.datn_toystoryshop.history.GetGoodsFragment;
import com.example.datn_toystoryshop.history.DeliveryFragment;
import com.example.datn_toystoryshop.history.EvaluateFragment;
import com.example.datn_toystoryshop.history.GetGoodsFragment;
import com.example.datn_toystoryshop.history.History_purchase_screen;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class History_Fragment extends Fragment {

    private TextView currentSelectedTextView;
    private LinearLayout history_purchase, confirm, get_goods, delivery, evaluate;
    private TextView confirmText, getGoodsText, deliveryText, evaluateText;
    private static final float DEFAULT_TEXT_SIZE = 10f; // Kích thước mặc định (dp)
    private static final float HIGHLIGHTED_TEXT_SIZE = 12f; // Kích thước khi được chọn (dp)
    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    public History_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history_purchase = view.findViewById(R.id.history_purchase);
        confirm = view.findViewById(R.id.confirm);
        get_goods = view.findViewById(R.id.get_goods);
        delivery = view.findViewById(R.id.delivery);
        evaluate = view.findViewById(R.id.evaluate);
        confirmText = view.findViewById(R.id.confirmText);
        getGoodsText = view.findViewById(R.id.getGoodsText);
        deliveryText = view.findViewById(R.id.deliveryText);
        evaluateText = view.findViewById(R.id.evaluateText);

        sharedPreferences = requireContext().getSharedPreferences("Settings", requireContext().MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Bundle bundle = getArguments();
            documentId = bundle.getString("documentId");
        currentSelectedTextView = confirmText; // Mặc định chọn Confirm
        highlightTextView(confirmText); // Tô màu ban đầu
        replaceFragment(new ConfirmFragment());
        highlightTextView(confirmText);

        history_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), History_purchase_screen.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
                requireActivity().finish();
            }
        });

//        // Xử lý sự kiện nhấn
        confirm.setOnClickListener(v -> {
            replaceFragment(new ConfirmFragment());
            highlightTextView(confirmText);
        });

        get_goods.setOnClickListener(v -> {
            replaceFragment(new GetGoodsFragment());
            highlightTextView(getGoodsText);
        });

        delivery.setOnClickListener(v -> {
            replaceFragment(new DeliveryFragment());
            highlightTextView(deliveryText);
        });

        evaluate.setOnClickListener(v -> {
            replaceFragment(new EvaluateFragment());
            highlightTextView(evaluateText);
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
    @Override
    public void onResume() {
        super.onResume();
        // Làm mới giao diện hoặc logic cần thiết khi quay lại Fragment này
        highlightTextView(confirmText); // Đánh dấu tab mặc định
    }

    private void replaceFragment(Fragment fragment) {
        if (documentId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("documentId", documentId);
            fragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Nếu muốn cho phép quay lại Fragment trước
        fragmentTransaction.commit();
    }
    private void highlightTextView(TextView textView) {
        if (currentSelectedTextView != null) {
            // Reset màu và kích thước của TextView trước đó
            currentSelectedTextView.setTextColor(getResources().getColor(android.R.color.black));
            currentSelectedTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_SIZE);
        }

        // Đổi màu và kích thước TextView được chọn
        textView.setTextColor(getResources().getColor(R.color.highlight_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, HIGHLIGHTED_TEXT_SIZE);
        currentSelectedTextView = textView;
    }
}
