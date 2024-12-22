package com.example.datn_toystoryshop.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datn_toystoryshop.Adapter.Product_New_Star_Adapter;
import com.example.datn_toystoryshop.Adapter.Product_Star_Adapter;
import com.example.datn_toystoryshop.Adapter.Home_search_Adapter;
import com.example.datn_toystoryshop.Home.Banner.BandaiCandy_screen;
import com.example.datn_toystoryshop.Home.Banner.Blokees_screen;
import com.example.datn_toystoryshop.Home.Banner.FindingUnicorn_screen;
import com.example.datn_toystoryshop.Home.Banner.Heyone_screen;
import com.example.datn_toystoryshop.Home.Sale_screen;
import com.example.datn_toystoryshop.Home.Banner.SquidGame_screen;
import com.example.datn_toystoryshop.Home.Banner.Toys_52_screen;
import com.example.datn_toystoryshop.Home.All_new_screen;
import com.example.datn_toystoryshop.Home.BlindBox_screen;
import com.example.datn_toystoryshop.Home.Figuring_screen;
import com.example.datn_toystoryshop.Home.LimitedFigure_screen;
import com.example.datn_toystoryshop.Home.NewArrivals_screen;
import com.example.datn_toystoryshop.Home.OtherProducts_screen;
import com.example.datn_toystoryshop.Home.Popular_screen;
import com.example.datn_toystoryshop.Home.Store_follow_screen;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Adapter.Home_Image_Adapter;
import com.example.datn_toystoryshop.Server.APIService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home_Fragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText search_bar;
    private TextView recyclertextviewsuggestions;
    private ViewPager2 viewPager;
    private FrameLayout new_arrivals, blind_box, figuring, other_products, sale, limited_figure;
    private RecyclerView recyclerViewNew, recyclerViewPopu, recyclerViewSuggestions;
    private Button btn_follow_store_1, btn_follow_store_2, btn_follow_store_3, btn_follow_store_4, btn_see_all_new, btn_see_all_popular, btn_view_all_stores;
    private Product_New_Star_Adapter productNewStarAdapter;
    private Product_Star_Adapter product_star_adapter;
    private Home_search_Adapter suggestionAdapter;
    private List<Product_Model> listProductModel, popularProductList;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private APIService apiService;
    private int currentPage = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
// RecyclerViews
        recyclerViewNew = view.findViewById(R.id.recyclerViewNewProducts);
        recyclerViewPopu = view.findViewById(R.id.recyclerViewpopularProducts);
        recyclertextviewsuggestions = view.findViewById(R.id.recycler_textview_suggestions);
        recyclerViewSuggestions = view.findViewById(R.id.recycler_view_suggestions);
// Buttons
        btn_follow_store_1 = view.findViewById(R.id.btn_follow_store_1);
        btn_follow_store_2 = view.findViewById(R.id.btn_follow_store_2);
        btn_follow_store_3 = view.findViewById(R.id.btn_follow_store_3);
        btn_follow_store_4 = view.findViewById(R.id.btn_follow_store_4);
        btn_see_all_new = view.findViewById(R.id.btn_see_all_new);
        btn_see_all_popular = view.findViewById(R.id.btn_see_all_popular);
        btn_view_all_stores = view.findViewById(R.id.btn_view_all_stores);
// ViewPager
        viewPager = view.findViewById(R.id.view_pager);
// Categories
        new_arrivals = view.findViewById(R.id.new_arrivals);
        blind_box = view.findViewById(R.id.blind_box);
        figuring = view.findViewById(R.id.figuring);
        other_products = view.findViewById(R.id.other_products);
        sale = view.findViewById(R.id.sale);
        limited_figure = view.findViewById(R.id.limited_figure);
// Search Bar
        search_bar = view.findViewById(R.id.search_bar);



        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");
        }

        recyclerViewNew.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopu.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSuggestions.setVisibility(View.GONE);
        recyclertextviewsuggestions.setVisibility(View.GONE);

        sharedPreferences = requireContext().getSharedPreferences("Settings", requireContext().MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Lắng nghe sự kiện chạm cho toàn bộ view
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra nếu view chạm không phải là search_bar hoặc recyclerViewSuggestions
                if (!isPointInsideView(event.getX(), event.getY(), search_bar) &&
                        !isPointInsideView(event.getX(), event.getY(), recyclerViewSuggestions)) {
                    recyclerViewSuggestions.setVisibility(View.GONE);
                    recyclertextviewsuggestions.setVisibility(View.GONE);
                }
            }
            return false;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

         apiService = retrofit.create(APIService.class);

        loadProducts(apiService);
        loadPopularProducts(apiService);

        setupSearchBar(apiService);

        List<Integer> images = Arrays.asList(
                R.drawable.viewpager1,
                R.drawable.viewpager2,
                R.drawable.viewpager3,
                R.drawable.viewpager4,
                R.drawable.viewpager5
        );

        Home_Image_Adapter adapter = new Home_Image_Adapter(images, position -> {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(getActivity(), BandaiCandy_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                case 1:
                    intent = new Intent(getActivity(), FindingUnicorn_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                case 2:
                    intent = new Intent(getActivity(), Heyone_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                case 3:
                    intent = new Intent(getActivity(), SquidGame_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                case 4:
                    intent = new Intent(getActivity(), Toys_52_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                case 5:
                    intent = new Intent(getActivity(), Blokees_screen.class);
                    intent.putExtra("documentId", documentId);
                    break;
                // Thêm các case cho các hình ảnh khác
                default:
                    intent = new Intent(getActivity(), Home_screen.class); // Nếu có hình mặc định
                    break;
            }
            startActivity(intent);
        });
        viewPager.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == images.size()) currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

        setupClickListeners();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData(apiService);
            swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng loading của SwipeRefreshLayout
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
    private void navigateToScreen(Class<?> targetScreen, String key, Serializable value) {
        Intent intent = new Intent(getActivity(), targetScreen);
        intent.putExtra("documentId", documentId);
        if (key != null && value != null) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    private void setupClickListeners() {
        // Chuyển màn hình đến các Activity khác
        new_arrivals.setOnClickListener(v -> navigateToScreen(NewArrivals_screen.class, null, null));
        blind_box.setOnClickListener(v -> navigateToScreen(BlindBox_screen.class, null, null));
        figuring.setOnClickListener(v -> navigateToScreen(Figuring_screen.class, null, null));
        other_products.setOnClickListener(v -> navigateToScreen(OtherProducts_screen.class, null, null));
        sale.setOnClickListener(v -> navigateToScreen(Sale_screen.class, null, null));
        limited_figure.setOnClickListener(v -> navigateToScreen(LimitedFigure_screen.class, null, null));

        // Xem tất cả sản phẩm mới và phổ biến
        btn_see_all_new.setOnClickListener(v -> navigateToScreen(All_new_screen.class, "productList", (Serializable) listProductModel));
        btn_see_all_popular.setOnClickListener(v -> navigateToScreen(Popular_screen.class, "productListPopu", (Serializable) popularProductList));

        // Xem tất cả cửa hàng
        btn_view_all_stores.setOnClickListener(v -> navigateToScreen(Store_follow_screen.class, null, null));

        // Theo dõi các cửa hàng
        btn_follow_store_1.setOnClickListener(v -> openLink("https://youtube.com/@ToyStationVietnam"));
        btn_follow_store_2.setOnClickListener(v -> openLink("https://www.youtube.com/@dochoirobot9952"));
        btn_follow_store_3.setOnClickListener(v -> openLink("https://www.youtube.com/@mykingdom"));
        btn_follow_store_4.setOnClickListener(v -> openLink("https://www.youtube.com/@HOBIVERSEVIETNAM"));

        // Tải lại dữ liệu khi refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData(apiService);
            swipeRefreshLayout.setRefreshing(false); // Tắt hiệu ứng loading
        });
    }

    private void loadData(APIService apiService) {
        // Gọi lại API để tải danh sách sản phẩm mới
        loadProducts(apiService);

        // Gọi lại API để tải danh sách sản phẩm phổ biến
        loadPopularProducts(apiService);

        // Nếu có thêm dữ liệu khác cần load, gọi ở đây
        setupSearchBar(apiService);

        // Đảm bảo cập nhật giao diện hoặc dữ liệu khác nếu cần
    }
    private void loadProducts(APIService apiService) {
        Call<List<Product_Model>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product_Model>>() {
            @Override
            public void onResponse(Call<List<Product_Model>> call, Response<List<Product_Model>> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    listProductModel = response.body();
                    productNewStarAdapter = new Product_New_Star_Adapter(getContext(), listProductModel, true, documentId);
                    recyclerViewNew.setAdapter(productNewStarAdapter);
                } else {
                    Log.e("ProductFragment", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<Product_Model>> call, Throwable t) {
                Log.e("ProductFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void loadPopularProducts(APIService apiService) {
        Call<List<Product_Model>> call = apiService.getPopular();
        call.enqueue(new Callback<List<Product_Model>>() {
            @Override
            public void onResponse(Call<List<Product_Model>> call, Response<List<Product_Model>> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    popularProductList = response.body();
                    if (popularProductList.isEmpty()) {
                        Log.d("HomeFragment", "No popular products available");
                    } else {
                        product_star_adapter = new Product_Star_Adapter(requireContext(), popularProductList, documentId);
                        recyclerViewPopu.setAdapter(product_star_adapter);
                    }
                } else {
                    Log.e("HomeFragment", "Response unsuccessful or body is null for popular products");
                }
            }

            @Override
            public void onFailure(Call<List<Product_Model>> call, Throwable t) {
                Log.e("HomeFragment", "API call failed for popular products: " + t.getMessage());
            }
        });
    }


    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        return (x >= viewX && x <= viewX + view.getWidth() &&
                y >= viewY && y <= viewY + view.getHeight());
    }

    private void setupSearchBar(APIService apiService) {
        Call<List<Product_Model>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product_Model>>() {
            @Override
            public void onResponse(Call<List<Product_Model>> call, Response<List<Product_Model>> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    listProductModel = response.body();
                    suggestionAdapter = new Home_search_Adapter(requireContext(), listProductModel, documentId);

                    recyclerViewSuggestions.setAdapter(suggestionAdapter);

                    search_bar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String query = s.toString();
                            if (query.isEmpty()) {
                                recyclerViewSuggestions.setVisibility(View.GONE);
                                recyclertextviewsuggestions.setVisibility(View.GONE);
                            } else {
                                recyclerViewSuggestions.setVisibility(View.VISIBLE);
                                recyclertextviewsuggestions.setVisibility(View.VISIBLE);
                                suggestionAdapter.filterList(query);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } else {
                    Log.e("HomeFragment", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<Product_Model>> call, Throwable t) {
                Log.e("HomeFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
