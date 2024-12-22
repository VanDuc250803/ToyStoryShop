package com.example.datn_toystoryshop.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Model.Voucher;
import com.example.datn_toystoryshop.R;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private List<Voucher> voucherList;
    private boolean showAllItems = false;
    private OnVoucherSelectedListener onVoucherSelectedListener;
    private boolean isProductRecyclerView; // Thêm biến để phân biệt giữa ship và product

    public VoucherAdapter(List<Voucher> voucherList, OnVoucherSelectedListener onVoucherSelectedListener, boolean isProductRecyclerView) {
        this.voucherList = voucherList;
        this.onVoucherSelectedListener = onVoucherSelectedListener;
        this.isProductRecyclerView = isProductRecyclerView;  // Xác định đây là RecyclerView của sản phẩm hay vận chuyển
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        int index = showAllItems ? position : Math.min(position, 1);  // Hiển thị tối đa 2 sản phẩm

        Voucher voucher = voucherList.get(index);

        holder.voucherPrice.setText("đ" + voucher.getPrice_reduced() / 1000 + "k");
        holder.minimumPrice.setText("Đơn tối thiểu 0đ");

        if (voucher.getQuantity_voucher() == 0) {
            holder.radioButton.setEnabled(false);
            holder.llfull.setAlpha(0.5f);
            holder.out_of_stock_overlay.setVisibility(View.VISIBLE);
        }
        holder.voucherDescription.setText("Giảm tối đa");

        if (voucher.getType_voucher().equals("Giảm giá sản phẩm")) {
            holder.icon.setImageResource(R.drawable.ic_logo);
        } else if (voucher.getType_voucher().equals("Giảm giá vận chuyển")) {
            holder.icon.setImageResource(R.drawable.ic_logo);
        }

        holder.radioButton.setChecked(voucher.isSelected());

        // Cập nhật sự kiện khi RadioButton được chọn
        holder.radioButton.setOnClickListener(v -> {
            // Nếu là RecyclerView sản phẩm, bỏ chọn tất cả các voucher sản phẩm
            if (isProductRecyclerView) {
                for (Voucher vItem : voucherList) {
                    vItem.setSelected(false);
                }
            } else {
                // Nếu là RecyclerView vận chuyển, bỏ chọn tất cả các voucher vận chuyển
                for (Voucher vItem : voucherList) {
                    vItem.setSelected(false);
                }
            }
            voucher.setSelected(true);

            notifyDataSetChanged();

            // Gọi phương thức onVoucherSelectedListener để cập nhật số lượng voucher đã chọn
            if (onVoucherSelectedListener != null) {
                onVoucherSelectedListener.onVoucherSelected(getSelectedVoucherCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return showAllItems ? voucherList.size() : Math.min(voucherList.size(), 2);
    }

    public void toggleShowAll() {
        showAllItems = !showAllItems;
        notifyDataSetChanged();
    }

    private int getSelectedVoucherCount() {
        int count = 0;
        for (Voucher voucher : voucherList) {
            if (voucher.isSelected()) {
                count++;
            }
        }
        return count;
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView voucherPrice, voucherDescription, minimumPrice;
        ImageView icon;
        LinearLayout llfull;
        RadioButton radioButton;
        FrameLayout out_of_stock_overlay;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherPrice = itemView.findViewById(R.id.voucher_price);
            voucherDescription = itemView.findViewById(R.id.voucher_description);
            minimumPrice = itemView.findViewById(R.id.minimunPrice);
            icon = itemView.findViewById(R.id.icon);
            radioButton = itemView.findViewById(R.id.radio_button);
            llfull = itemView.findViewById(R.id.llfull);
            out_of_stock_overlay = itemView.findViewById(R.id.out_of_stock_overlay);

        }
    }

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(int selectedCount);
    }
}

