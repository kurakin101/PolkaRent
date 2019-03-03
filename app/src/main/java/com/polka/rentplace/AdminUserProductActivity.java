package com.polka.rentplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polka.rentplace.model.Cart;
import com.polka.rentplace.prevalent.Prevalent;
import com.polka.rentplace.viewHolder.CartViewHolder;

public class AdminUserProductActivity extends AppCompatActivity {

   private RecyclerView productsList;
   RecyclerView.LayoutManager layoutManager;
   private DatabaseReference cartListRef;

   private String userID = "";
   String p2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_product);


        userID = getIntent().getStringExtra("uid");


        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("User View").child(userID).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
//                p2 = model.getPrice();

//                Picasso.get().load(model.getImage()).into(holder.imgProductImg);

                String d = Prevalent.currentOnlineUser.getPhone();
                holder.txtProductQuantiny.setText(model.getPhone());
                if (holder.txtProductQuantiny.getText().equals(d)){
                    holder.txtProductPrice.setText("Price = " + model.getPrice());
                    holder.txtProductName.setText("Product = " + model.getPname());
                }else{
                    productsList.setVisibility(View.INVISIBLE);
                }
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;


            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();

    }
}
