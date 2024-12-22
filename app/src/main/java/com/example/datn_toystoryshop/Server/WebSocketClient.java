package com.example.datn_toystoryshop.Server;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.datn_toystoryshop.Contact_support.OnChatUpdateListener;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.SendMail;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private WebSocket webSocket;

    private NotificationManager notificationManager;
    private Context context;

    private String documentId;
    private FirebaseFirestore db;
    private String email_confirm;

    private OnChatUpdateListener chatUpdateListener;


    public WebSocketClient(Context context, NotificationManager notificationManager, String documentId) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.documentId = documentId;
        this.db = FirebaseFirestore.getInstance();
    }
    public void setOnChatUpdateListener(OnChatUpdateListener listener) {
        this.chatUpdateListener = listener;
    }

    public void start() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        Request request = new Request.Builder().url(APIService.WS_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "WebSocket11111111111 Received: " + text);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String cusId = jsonObject.getString("cusId");

                    // Kiểm tra nếu có thay đổi trong bảng order
                    if (jsonObject.has("orderStatus")) {
                        String orderId = jsonObject.getString("_id");
                        String orderStatus = jsonObject.getString("orderStatus");

                        if (cusId.equals(documentId)) {
                            sendNotification(orderId, cusId, orderStatus);

                            // Gửi email xác nhận nếu trạng thái là "Đã giao"
                            if ("Đã giao".equals(orderStatus)) {
                                loadUserDataByDocumentId(cusId, jsonObject);
                            }
                        }
                    }
                    // Kiểm tra nếu có thay đổi trong bảng chat
                    else if (jsonObject.has("chatType")) {
                        String message = jsonObject.getString("message");
                        String userId = jsonObject.optString("userId", null);
                        if (cusId.equals(documentId)&& (userId == null || !userId.equals("support1"))) {
                            // Gửi thông báo cập nhật giao diện chat
                            if (chatUpdateListener != null) {
                                chatUpdateListener.onNewChatMessage(message);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "Error: " + t.getMessage());
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed");
            }
        });
    }

    private void sendNotification(String orderId, String cusId, String orderStatus) {
        // Lấy 5 ký tự cuối cùng của orderId
        String shortOrderId = orderId.length() > 5 ? orderId.substring(orderId.length() - 5) : orderId;

        // Tạo thông báo cho người dùng khi có thay đổi trong trạng thái đơn hàng
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "home_notification_channel")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("ToyStory Shop thông báo!")
                .setContentText("Đơn hàng  " + shortOrderId  + " của bạn " + orderStatus)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
    private void sendConfirmationEmail(String cusId, JSONObject orderData) {
        try {
            // Lấy email khách hàng từ thuộc tính email_confirm
            if (email_confirm == null || email_confirm.isEmpty()) {
                Log.e(TAG, "Email không hợp lệ");
                return;
            }

            // Lấy các thông tin cần thiết từ orderData
            String orderDate = orderData.getString("orderDate");
            int revenueAll = orderData.getInt("revenue_all");
            String addressOrder = orderData.getString("address_order");
            String nameOrder = orderData.getString("name_order");
            String phoneOrder = orderData.getString("phone_order");
            String paymentMethod = orderData.getString("payment_method");

            // Xử lý danh sách sản phẩm
            JSONArray prodDetails = orderData.getJSONArray("prodDetails");
            StringBuilder productListBuilder = new StringBuilder();

            for (int i = 0; i < prodDetails.length(); i++) {
                JSONObject product = prodDetails.getJSONObject(i);
                String productName = product.getJSONObject("prodId").getString("namePro");
                int price = product.getJSONObject("prodId").getInt("price");
                int quantity = product.getInt("quantity");
                String specification = product.getString("prodSpecification");

                productListBuilder.append(String.format(
                        "<li>%s - %s (Số lượng: %d, Giá: %,d VNĐ)</li>",
                        productName, specification, quantity, price
                ));
            }

            String productList = productListBuilder.toString();

            // Tạo nội dung email
            String message = String.format(
                    "<html>" +
                            "<body>" +
                            "<h2>Kính gửi Quý khách hàng,</h2>" +
                            "<p>Cảm ơn bạn đã mua sắm tại <strong>ToyStory Shop</strong>!</p>" +
                            "<p>Đơn hàng của bạn đã được xác nhận và giao thành công. Dưới đây là thông tin chi tiết về đơn hàng:</p>" +
                            "<ul>" +
                            "<li><strong>Ngày đặt hàng:</strong> %s</li>" +
                            "<li><strong>Danh sách sản phẩm:</strong></li>" +
                            "<ul>%s</ul>" +
                            "<li><strong>Tổng cộng:</strong> %,d VNĐ</li>" +
                            "</ul>" +
                            "<p><strong>Địa chỉ giao hàng:</strong> %s</p>" +
                            "<p><strong>Họ tên:</strong> %s</p>" +
                            "<p><strong>Số điện thoại:</strong> %s</p>" +
                            "<p><strong>Hình thức thanh toán:</strong> %s</p>" +
                            "<p><strong>Thời gian dự kiến giao hàng:</strong> Đã giao thành công.</p>" +
                            "<p><em>Nếu bạn cần hỗ trợ thêm, vui lòng liên hệ bộ phận CSKH:</em></p>" +
                            "<ul>" +
                            "<li>Số điện thoại: 0123987456</li>" +
                            "<li>Email: toystory.shop.datn@gmail.com</li>" +
                            "</ul>" +
                            "<p>Cảm ơn bạn đã tin tưởng và ủng hộ ToyStory Shop!</p>" +
                            "<p>Trân trọng,</p>" +
                            "<p><strong>Đội ngũ ToyStory Shop</strong></p>" +
                            "</body>" +
                            "</html>",
                    orderDate,
                    productList,
                    revenueAll,
                    addressOrder,
                    nameOrder,
                    phoneOrder,
                    paymentMethod
            );

            // Gửi email
            String subject = "ToyStory Shop - Xác nhận đơn hàng đã giao!";
            new SendMail(email_confirm, subject, message).execute();
        } catch (JSONException e) {
            Log.e(TAG, "Lỗi xử lý dữ liệu JSON trong sendConfirmationEmail", e);
        }
    }
    private void loadUserDataByDocumentId(String documentId, JSONObject orderData) {
        DocumentReference docRef = db.collection("users").document(documentId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy tất cả dữ liệu từ tài liệu
                    email_confirm = document.getString("email");
                    sendConfirmationEmail(documentId,orderData);
                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }
}
