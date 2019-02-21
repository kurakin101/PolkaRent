package com.polka.rentplace.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.polka.rentplace.Interface.ItemClickListerner;
import com.polka.rentplace.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice, txtProductQuantiny;
    public ImageView imgProductImg;
    private ItemClickListerner itemClickListerner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantiny = itemView.findViewById(R.id.cart_product_quantiny);
        imgProductImg = itemView.findViewById(R.id.cart_product_image);

    }

    @Override
    public void onClick(View view) {
        itemClickListerner.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListerner(ItemClickListerner itemClickListerner) {
        this.itemClickListerner = itemClickListerner;
    }
}
