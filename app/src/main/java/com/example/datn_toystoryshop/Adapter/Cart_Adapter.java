package com.example.datn_toystoryshop.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Detail.Product_detail;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.ProductCallback;
import com.example.datn_toystoryshop.Shopping.Cart_screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart_Adapter extends RecyclerView.Adapter<Cart_Adapter.CartViewHolder> {

    private Context context;
    private List<Cart_Model> cartList;
    private Cart_Model cart;
    private String selectedItem;
    private String documentId;
    private int quantity;
    private APIService apiService;
    private CheckBox checkBoxSelectAll;
    private boolean isDefaultSelected = true; // Biến trạng thái để theo dõi giá trị mặc định
    private OnCartDataChangeListener dataChangeListener;

    public Cart_Adapter(Context context, List<Cart_Model> cartList, APIService apiService, String documentId) {
        this.context = context;
        this.cartList = cartList;
        this.apiService = apiService;
        this.documentId = documentId;
    }
    // Phương thức cập nhật dữ liệu của một mục cụ thể
    public void updateCartData(Cart_Model cart, int cartPosition) {
        this.cartList.set(cartPosition, cart); // Cập nhật dữ liệu tại vị trí cụ thể
        notifyItemChanged(cartPosition);      // Làm mới chỉ mục được thay đổi
    }
    public interface OnCartDataChangeListener {
        void onCartDataChanged();
    }
    public void setOnCartDataChangeListener(OnCartDataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    public List<Cart_Model> getSelectedItems() {
        List<Cart_Model> selectedItems = new ArrayList<>();
        for (Cart_Model cart : cartList) {
            if (cart.isSelected()) {
                selectedItems.add(cart);
            }
        }
        return selectedItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Cart_Adapter.CartViewHolder holder, int position) {
        Log.e("OrderHistoryAdapter", "j66666666666666666Cart_Adapter" + documentId);
        Cart_Model cart = cartList.get(position);
        // Mặc định ẩn hiddenTextView
        holder.hiddenTextView.setVisibility(View.GONE);
        String cartId = cart.get_id();
        String prodId = cart.getProdId();
        quantity = cart.getQuantity();

        Log.d("CartAdapter", "Attempting to delete product with ID: aaa");
        // Xử lý sự kiện nhấn vào nút xóa
        holder.hiddenTextView.setOnClickListener(v -> {
            // Khi nhấn vào nút xóa, xóa sản phẩm khỏi giỏ hàng
            Log.d("CartAdapter", "Attempting to delete product with ID: " + cart.getProdId());
            deletecart(cart.getProdId(), holder);
            // Dừng hành động vuốt sau khi xóa
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.tvQuantity.setText(String.valueOf(quantity)); // Đặt giá trị ban đầu cho số lượng là 1
        // Sự kiện tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            currentQuantity++;
            holder.tvQuantity.setText(String.valueOf(currentQuantity));
            updateCartItem(apiService, cart.get_id(), selectedItem, currentQuantity);
            quantity = currentQuantity;
            Log.d("CartAdapter", "yyyyyyyyyyyyyyyyyyyyyyyyyyy1 " + currentQuantity);
            Log.d("CartAdapter", "yyyyyyyyyyyyyyyyyyyyyyyyyyy1 " + quantity);

            //  gặp lỗi tvQuantity không cập nhập lên ngay mà phải click 2 lần thì mới cập nhập, mặc dù khi click 1 lần dữ liệu đã lên đc mongo

        });
// Đặt sự kiện cho CheckBox để chọn/bỏ chọn từng sản phẩm
        holder.checkBoxSelectItem.setOnCheckedChangeListener(null); // Xóa listener cũ để tránh gọi lại
        holder.checkBoxSelectItem.setChecked(cart.isSelected()); // Đặt trạng thái cho CheckBox dựa trên giá trị hiện tại

// Đặt sự kiện khi CheckBox được chọn hoặc bỏ chọn
        holder.checkBoxSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            countSelectedItems();
            cart.setSelected(isChecked); // Cập nhật trạng thái của sản phẩm
            Log.d("CartAdapter", "Product ID: " + cart.getProdId() + " selected: " + isChecked);
            updateTotalPayment(false); // Tính toán lại tổng tiền khi trạng thái thay đổi
            holder.checkBoxSelectItem.setChecked(cart.isSelected());
        });

        // Sự kiện giảm số lượng với giá trị tối thiểu là 1
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                holder.tvQuantity.setText(String.valueOf(currentQuantity));
                updateCartItem(apiService, cart.get_id(), selectedItem, currentQuantity);
                quantity = currentQuantity;
                Log.d("CartAdapter", "yyyyyyyyyyyyyyyyyyyyyyyyyyy " + currentQuantity);
            } else {
                Toast.makeText(context, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
            }
        });
        // Thiết lập Spinner với dữ liệu từ arrays
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.color_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.colorSpinner.setAdapter(adapter);
// Lấy giá trị `prodSpecification` từ Cart_Model
        String prodSpecification = cart.getProdSpecification();

// Tìm vị trí của `prodSpecification` trong adapter
        int defaultPosition = adapter.getPosition(prodSpecification);

        // Cờ để kiểm soát trạng thái mặc định
        final boolean[] isDefaultSelected = {true};

// Nếu tìm thấy, thiết lập vị trí mặc định cho Spinner
        if (defaultPosition >= 0) {
            holder.colorSpinner.setSelection(defaultPosition);
        } else {
            // Nếu không tìm thấy, đặt mặc định là item đầu tiên (hoặc bạn có thể xử lý khác nếu muốn)
            holder.colorSpinner.setSelection(0);
        }

        // Thiết lập onItemSelectedListener cho colorSpinner
        holder.colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newItem = parent.getItemAtPosition(position).toString();
                // Ngăn chặn vòng lặp gọi lại
                // Ngăn chặn vòng lặp gọi lại do giá trị mặc định
                if (isDefaultSelected[0]) {
                    isDefaultSelected[0] = false; // Đánh dấu đã vượt qua giá trị mặc định
                    Log.d("CartAdapter", "Lần chọn mặc định, bỏ qua callback");
                    return; // Không làm gì
                }

                // Kiểm tra giá trị có thay đổi không
                if (newItem.equals(selectedItem)) {
                    Log.d("CartAdapter", "Giá trị không thay đổi, không cần cập nhật");
                    return; // Không làm gì nếu giá trị không thay đổi
                }
                selectedItem = newItem;
                updateCartItem(apiService, cart.get_id(), selectedItem, quantity);
                Log.d("CartAdapter", "yyyyyyyyyyyyyyyyyyyyyyyyyyy111111111111111111 " + selectedItem);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không có gì được chọn
            }
        });
        //lấy giá để tính tổng
        loadProductPrices();

        // Gọi phương thức lấy dữ liệu từ MongoDB với prodId
        loadProductById(apiService, prodId, new ProductCallback() {
            @Override
            public void onSuccess(Product_Model product) {

                holder.productName.setText(product.getNamePro());
                holder.productPrice.setText(String.format("%,.0fđ", product.getPrice()));
                List<String> images = product.getImgPro();
                if (images != null && !images.isEmpty()) {
                    Glide.with(context).load(images.get(0)).into(holder.productImage);
                }
                // Thiết lập sự kiện click để mở màn hình chi tiết
                holder.itemContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Product_detail.class);

                        // Truyền các thuộc tính sản phẩm qua Intent
                        intent.putExtra("documentId", documentId);
                        intent.putExtra("productId", product.get_id());
                        intent.putExtra("owerId", product.getOwerId());
                        intent.putExtra("statusPro", product.isStatusPro());
                        intent.putExtra("productPrice", product.getPrice());
                        intent.putExtra("desPro", product.getDesPro());
                        intent.putExtra("creatDatePro", product.getCreatDatePro());
                        intent.putExtra("quantity", product.getQuantity());
                        intent.putExtra("listPro", product.getListPro());
                        intent.putStringArrayListExtra("productImg", new ArrayList<>(product.getImgPro()));
                        intent.putExtra("productName", product.getNamePro());
                        intent.putExtra("cateId", product.getCateId());
                        intent.putExtra("brand", product.getBrand());
                        // Truyền _id của cart vào Intent
                        intent.putExtra("cartId", cartId);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("cartAdapter", "Failed to load product details: " + t.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSelectItem;
        ImageView productImage;
        TextView productName, freeReturn, productPrice, originalPrice, btnDecrease, tvQuantity, btnIncrease, hiddenTextView;
        Spinner colorSpinner;
        LinearLayout itemContent;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxSelectItem = itemView.findViewById(R.id.checkBoxSelectItem);
            hiddenTextView = itemView.findViewById(R.id.hiddenTextView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            colorSpinner = itemView.findViewById(R.id.colorSpinner);
            freeReturn = itemView.findViewById(R.id.freeReturn);
            productPrice = itemView.findViewById(R.id.productPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            itemContent = itemView.findViewById(R.id.itemContent);
        }
    }

    public ItemTouchHelper.SimpleCallback getItemTouchHelper() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                CartViewHolder cartViewHolder = (CartViewHolder) viewHolder;

                // Khi vuốt sang trái, hiển thị dialog xác nhận
                String productId = cartList.get(viewHolder.getAdapterPosition()).getProdId();
                Log.d("CartAdapter", "Swipe action triggered. Product ID: " + productId);

                // Hiển thị dialog xác nhận
                new AlertDialog.Builder(context)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Khi nhấn "Yes", xóa sản phẩm
                            deletecart(productId, cartViewHolder);
                            cartViewHolder.hiddenTextView.setVisibility(View.GONE); // Ẩn nút xóa
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Khi nhấn "No", hoàn tác hành động vuốt
                            notifyItemChanged(viewHolder.getAdapterPosition());
                            cartViewHolder.hiddenTextView.setVisibility(View.GONE); // Ẩn nút xóa
                        })
                        .create()
                        .show();
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                CartViewHolder cartViewHolder = (CartViewHolder) viewHolder;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) { // Vuốt sang trái
                        float maxSwipe = -cartViewHolder.hiddenTextView.getWidth(); // Chiều rộng của nút xóa
                        float translationX = Math.max(dX, maxSwipe);
                        cartViewHolder.itemContent.setTranslationX(translationX);

                    } else { // Reset trạng thái
                        cartViewHolder.itemContent.setTranslationX(0);
                        cartViewHolder.hiddenTextView.setVisibility(View.GONE);
                    }
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }


            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.5f; // Tỷ lệ vuốt cần thiết để hoàn tất hành động
            }
        };
    }


    public void updateCartItem(APIService apiService, String cartId, String selectedItem, int currentQuantity) {

        // Chuẩn bị dữ liệu cập nhật
        Cart_Model cartModel = new Cart_Model();
        cartModel.set_id(cartId);
        cartModel.setProdSpecification(selectedItem);
        cartModel.setQuantity(currentQuantity); // Cập nhật số lượng

        // Gọi API
        Call<Cart_Model> call = apiService.putCartUpdate(cartId, cartModel);
        call.enqueue(new Callback<Cart_Model>() {
            @Override
            public void onResponse(Call<Cart_Model> call, Response<Cart_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    refreshCart();
                    if (dataChangeListener != null) {
                        dataChangeListener.onCartDataChanged();
                    }
                } else {
                    Toast.makeText(context, "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart_Model> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadProductById(APIService apiService, String prodId, ProductCallback callback) {
        Call<Product_Model> call = apiService.getProductById(prodId);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product_Model product = response.body();
                    Log.d("cartAdapter", "Product retrieved: " + product.getNamePro() + ", Price: " + product.getPrice());
                    callback.onSuccess(product);
                } else {
                    Log.e("cartAdapter", "Response unsuccessful or product body is null");
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Log.e("cartAdapter", "API call failed:222222 " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    private Map<String, Double> productPriceMap = new HashMap<>();

    public void loadProductPrices() {
        for (Cart_Model cart : cartList) {
            String prodId = cart.getProdId();
            loadProductById(apiService, prodId, new ProductCallback() {
                @Override
                public void onSuccess(Product_Model product) {
                    productPriceMap.put(prodId, product.getPrice());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("CartAdapter", "Failed to load product price: " + t.getMessage());
                }
            });
        }
    }

    // Hàm tính tổng tiền thanh toán
    public void updateTotalPayment(boolean isSelectAll) {
        double totalPayment = 0;

        // Duyệt qua từng sản phẩm trong giỏ hàng
        for (Cart_Model cart : cartList) {
            if (isSelectAll) {
                cart.setSelected(true); // Chọn tất cả sản phẩm
                Log.d("CartAdapter", "Selecting all products");
            }

            // Kiểm tra sản phẩm có được chọn và có giá không
            if (cart.isSelected() && productPriceMap.containsKey(cart.getProdId())) {
                double productPrice = productPriceMap.get(cart.getProdId());
                double subtotal;
                // Kiểm tra nếu sản phẩm có prodSpecification bằng "Nguyên set 12 hộp"
                if ("Nguyên set 12 hộp".equals(cart.getProdSpecification())) {
                    subtotal = productPrice * cart.getQuantity() * 12; // Nhân thêm 12
                    Log.d("CartAdapter", "CartAdapterCartAdapterSpecial calculation for product ID: " + cart.getProdId() +
                            " Specification: " + cart.getProdSpecification() +
                            " Price: " + productPrice + " Quantity: " + cart.getQuantity() +
                            " Subtotal: " + subtotal);
                } else {
                    subtotal = productPrice * cart.getQuantity(); // Tính bình thường
                    Log.d("CartAdapter", "CartAdapterCartAdapterRegular calculation for product ID: " + cart.getProdId() +
                            " Price: " + productPrice + " Quantity: " + cart.getQuantity() +
                            " Subtotal: " + subtotal);
                }

                totalPayment += subtotal; // Cộng vào tổng tiền
            } else if (cart.isSelected()) {
                // Ghi log nếu không tìm thấy giá sản phẩm
                Log.d("CartAdapter", "Price not found for product ID: " + cart.getProdId());
            }
        }

        // Cập nhật danh sách hiển thị lại
        notifyDataSetChanged();

        // Cập nhật tổng tiền trên Cart_screen
        Log.d("CartAdapter", "Total Payment: " + totalPayment);
        ((Cart_screen) context).updateTotalPayment(totalPayment);
    }

    public void deselectAllItems() {

        for (Cart_Model cart : cartList) {
            cart.setSelected(false); // Đặt tất cả sản phẩm là không chọn
        }
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }


    private void deletecart(String productId, Cart_Adapter.CartViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            Log.e("CartAdapter", "API call failed:222222Invalid position, cannot delete item.");
            return;
        }

        Log.d("CartAdapter", "API call failed:222222Deleting item at position: " + position + ", product ID: " + productId);

        Call<Void> call = apiService.deleteCart(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cartList.remove(position);  // Xóa sản phẩm khỏi danh sách
                    notifyItemRemoved(position); // Cập nhật giao diện
                    Log.d("CartAdapter", "API call failed:222222Item deleted successfully.");
                    Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    if (dataChangeListener != null) {
                        dataChangeListener.onCartDataChanged();
                    }
                    refreshCart();
                } else {
                    Log.e("CartAdapter", "API call failed:222222Failed to delete product: " + response.message());
                    Toast.makeText(context, "Xóa sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CartAdapter", "API call failed:22222211111111111 " + t.getMessage());
                Toast.makeText(context, "Lỗi mạng11111: Không thể xóa sản phẩm!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public int countSelectedItems() {
        int selectedCount = 0;

        // Duyệt qua từng sản phẩm trong giỏ hàng
        for (Cart_Model cart : cartList) {
            if (cart.isSelected()) { // Kiểm tra nếu sản phẩm được chọn
                selectedCount++;
            }
        }
        Log.e("FavoriteAdapter", "aaaaaaaaacccccc " + selectedCount);
        // Hiển thị Toast với số lượng sản phẩm đã chọn
        return selectedCount;
    }

    public void refreshCart() {
        String cusId = documentId;

        if (cusId == null || cusId.isEmpty()) {
            Log.e("FavoriteScreen", "cusId không được để trống");
            return;
        }
        apiService.getCarts(cusId).enqueue(new Callback<List<Cart_Model>>() {
            @Override
            public void onResponse(Call<List<Cart_Model>> call, Response<List<Cart_Model>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartList.clear();
                    cartList.addAll(response.body());
                    notifyDataSetChanged();  // Cập nhật lại giao diện
                }
            }

            @Override
            public void onFailure(Call<List<Cart_Model>> call, Throwable t) {
                // Xử lý lỗi khi gọi API thất bại
            }
        });
    }

}