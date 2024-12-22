package com.example.datn_toystoryshop.Server;


import com.example.datn_toystoryshop.Model.Address_model;
import com.example.datn_toystoryshop.Model.ArtStoryModel;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.ChatHistoryResponse_Model;
import com.example.datn_toystoryshop.Model.ChatMessage_Model;
import com.example.datn_toystoryshop.Model.Favorite_Model;
import com.example.datn_toystoryshop.Model.FeebackApp_Model;
import com.example.datn_toystoryshop.Model.Feeback_Model;
import com.example.datn_toystoryshop.Model.Feeback_Rating_Model;
import com.example.datn_toystoryshop.Model.OrderResponse;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Model.Product_feedback;
import com.example.datn_toystoryshop.Model.Refund_Model;
import com.example.datn_toystoryshop.Model.VnPayCreate_Model;
import com.example.datn_toystoryshop.Model.Vnpay_Model;
import com.example.datn_toystoryshop.Model.Voucher;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {


    // Địa chỉ IP server
//   String SERVER_IP = "192.168.16.100";// cương
  //  String SERVER_IP = "192.168.1.15";// huy
    String SERVER_IP = "10.24.32.38";// đức


    String PAYMENT_SERVER_URL = "http://" + SERVER_IP + ":28017";
    String BASE_URL = "http://" + SERVER_IP + ":3000/";
    String WS_URL = "ws://" + SERVER_IP + ":8080";

    @GET("/api/list")
    Call<List<Product_Model>> getProducts();

    @GET("/api/new-arrivals")
    Call<List<Product_Model>> getNewArrivals();
    @GET("/api/heyone")
    Call<List<Product_Model>> getHeyOne();
    @GET("/api/finding_unicorn")
    Call<List<Product_Model>> getFindingUnicorn();
    @GET("/api/bandai_candy")
    Call<List<Product_Model>> getBandaiCandy();
    @GET("/api/squid_game")
    Call<List<Product_Model>> getSquidGame();
    @GET("/api/blokees")
    Call<List<Product_Model>> getBlokees();
    @GET("/api/52_toys")
    Call<List<Product_Model>> get52Toys();


    @GET("/api/sale")
    Call<List<Product_Model>> getSale();

    @GET("/api/limited")
    Call<List<Product_Model>> getLimited();

    @GET("/api/blind_box")
    Call<List<Product_Model>> getBlindBox();

    @GET("/api/figuring")
    Call<List<Product_Model>> getFiguring();

    @GET("/api/other")
    Call<List<Product_Model>> getOther();

    @GET("/api/favorites")
    Call<List<Favorite_Model>> getFavorites(@Query("cusId") String cusId);

    @GET("/api/carts")
    Call<List<Cart_Model>> getCarts(@Query("cusId") String cusId);

    @GET("/api/refund")
    Call<List<Refund_Model>> getRefund(@Query("cusId") String cusId);

    @GET("/api/orders/confirm")
    Call<List<Order_Model>> getOrders_Confirm(@Query("cusId") String cusId);

    @GET("/api/orders/getgoods")
    Call<List<Order_Model>> getOrders_getgoods(@Query("cusId") String cusId);

    @GET("/api/orders/delivery")
    Call<List<Order_Model>> getOrders_delivery(@Query("cusId") String cusId);

    @GET("/api/orders/successful")
    Call<List<Order_Model>> getOrders_successful(@Query("cusId") String cusId);

    @GET("/api/orders/canceled")
    Call<List<Order_Model>> getOrders_canceled(@Query("cusId") String cusId);

    @GET("/api/feebacks")
    Call<List<Feeback_Model>> getFeeback(@Query("prodId") String prodId);

    @POST("/api/add-feedback")
    Call<Feeback_Model> addFeedback(@Body Feeback_Model feebackModel);

    @GET("/api/all-product-details")
    Call<List<Product_feedback>> getAllProductDetails(@Query("cusId") String cusId);

    @GET("/api/check-feedback")
    Call<JsonObject> checkFeedback(@Query("cusId") String cusId, @Query("prodId") String prodId);

    @GET("/api/average-rating/{prodId}")
    Call<Feeback_Rating_Model> getAverageRating(@Path("prodId") String prodId);

    @POST("/api/add/add-to-favorites")
    Call<Favorite_Model> addToFavorites(@Body Favorite_Model favoriteModel);

    @POST("/api/add-Refund")
    Call<Refund_Model> addToRefund(@Body Refund_Model refundModel);

    @GET("/api/product-by/{prodId}")
    Call<Product_Model> getProductById(@Path("prodId") String prodId);

//    @GET("/api/refund-by-order/{orderId}")
//    Call<RefundResponse> getRefundByID(@Path("orderId") String orderId);

    @GET("/api/order-by/{orderId}")
    Call<Order_Model> getOrderById(@Path("orderId") String orderId);

    @GET("/api/refund-by/{id}")
    Call<Refund_Model> getRefundByID(@Path("id") String refundId);

    @PUT("/api/update/cart/{cartId}")
    Call<Cart_Model> putCartUpdate(@Path("cartId") String cartId, @Body Cart_Model cartModel);

    @PUT("/api/update/order/{orderId}")
    Call<Order_Model> putorderUpdate(@Path("orderId") String orderId, @Body Order_Model orderModel);

//    @PUT("/api/update/refund/{orderId}")
//    Call<Refund_Model> putRefundUpdate(@Path("orderId") String orderId, @Body Refund_Model refundModel);

    @PUT("/api/update/refund/{id}")
    Call<Refund_Model> putRefundUpdate(@Path("id") String refundId, @Body Refund_Model refundModel);

    @PUT("/api/update/product/{prodId}")
    Call<Product_Model> putProductUpdate(@Path("prodId") String prodId, @Body Product_Model productModel);

    @GET("/api/cart-by/{cartId}")
    Call<Cart_Model> getCartById(@Path("cartId") String cartId);

    @DELETE("/api/deleteFavorite/{id}")
    Call<Void> deleteFavorite(@Path("id") String productId);

    @DELETE("/api/deleteCart/{id}")
    Call<Void> deleteCart(@Path("id") String productId);

    @DELETE("/api/deleteCartId/{cartId}")
    Call<Void> deleteCartId(@Path("cartId") String cartId);

    @GET("api/list-popular")
    Call<List<Product_Model>> getPopular();

    @GET("/api/check-favorite/{prodId}")
    Call<Map<String, Boolean>> checkFavorite(@Path("prodId") String productId);

    @POST("/api/add/add-to-app-feeback")
    Call<FeebackApp_Model> addToFeebackApp(@Body FeebackApp_Model feebackAppModel);

    @POST("/api/add/add-to-cart")
    Call<Cart_Model> addToCart(@Body Cart_Model cartModel);

    @POST("/api/add/add-to-order")
    Call<OrderResponse> addToOrder(@Body Order_Model orderModel);

    @GET("/api/artstories")
    Call<List<ArtStoryModel>> getArtStories();

    @GET("/api/vouchers")
    Call<List<Voucher>> getVouchers();

    @GET("api/voucher/{discount_code}")
    Call<Voucher> getVoucherByCode(@Path("discount_code") String discountCode);

    @PUT("api/update/vouchers/{id}")
    Call<Voucher> updateVoucherQuantity(@Path("id") String voucherId);

    @GET("/api/addresses")
    Call<List<Address_model>> getAllAddresses(@Query("cusId") String cusId);

//    @GET("/api/addresses/{id}")
//    Call<Address_model> getAddressById(@Path("id") String addressId);

    @POST("/api/add/addresses")
    Call<Address_model> addAddress(@Body Address_model address);

    @PUT("/api/update/addresses/{id}")
    Call<Address_model> updateAddress(@Path("id") String addressId, @Body Address_model address);

    @DELETE("/api/deleteAddresses/{id}")
    Call<Void> deleteAddress(@Path("id") String addressId);

    @PUT("/api/update-default-address")
    Call<ResponseBody> updateDefault(@Query("cusId") String cusId);

    @GET("/api/cart/check-product")
    Call<JsonObject> checkProductInCart(@Query("prodId") String prodId, @Query("cusId") String cusId);

    @GET("/api/cart/get-cart-id")
    Call<JsonObject> getCartId(@Query("prodId") String prodId, @Query("cusId") String cusId);

    @POST("/api/chat/send")
    Call<ResponseBody> sendMessage(@Body ChatMessage_Model chatMessage);

    @GET("/api/chat/history")
    Call<ChatHistoryResponse_Model> getChatHistory(@Query("cusId") String user1, @Query("cusId") String user2);

    @POST("/create-checkout-vnpay")
    Call<Vnpay_Model> createCheckoutVnPay(@Body VnPayCreate_Model body);

// @DELETE("/api/deleteCartItem")
// Call<CartResponse> deleteCartItem(@Body Cart_Model request);
}
