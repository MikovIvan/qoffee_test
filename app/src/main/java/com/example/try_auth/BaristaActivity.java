package com.example.try_auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class BaristaActivity extends AppCompatActivity {

    String currentUserId;

    FirebaseAuth mAuth;
    RecyclerView orderList;
    Toolbar toolbar;
    Query query;
    private DatabaseReference rootRef, userRef, orderRequestRef, orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista);

        orderList = findViewById(R.id.order_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        query = FirebaseDatabase.getInstance().getReference().child("Orders")
                .orderByChild("coffeeHouse")
                .equalTo(currentUserId);

        orderRequestRef = FirebaseDatabase.getInstance().getReference().child("Order Request").child(currentUserId);

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Barista");
    }

    //video 41
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
//                        ChatRequestRef замена на orderRequestRef
                        .setQuery(query, Order.class)
//                        .setQuery(orderRequestRef.child(currentUserId), Order.class)
                        .build();

        FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position, @NonNull Order model) {


                        holder.name.setText(model.getName());
                        holder.time.setText(model.getTime());
                        holder.coffee.setText(model.getCoffee());
                        holder.orderId.setText(model.getId());


                        holder.itemView.findViewById(R.id.button_ok).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.button_delay).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
//video 31
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//
//                                String showOrderId = getRef(position).getKey();
//
//                                Intent intent = new Intent(Barista.this, OrderViewBarista.class);
//                                intent.putExtra("orderId",showOrderId);
//                                startActivity(intent);
//                            }
//                        });

                        final DatabaseReference getTypeRef = getRef(position).child("orderStatus").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();
                                    switch (type) {
                                        case "request_sent":
                                            holder.accept.setBackgroundColor(YELLOW);
                                            holder.accept.setText("Accept?");
                                            break;
                                        case "request_confirm":
                                            holder.accept.setBackgroundColor(GREEN);
                                            holder.accept.setText("Confirm");
                                            holder.cancel.setVisibility(View.INVISIBLE);
                                            holder.plus5.setVisibility(View.INVISIBLE);
                                            break;
                                        case "confirm+5":
                                            holder.accept.setBackgroundColor(GREEN);
                                            holder.accept.setText("Confirm");
                                            holder.plus5.setBackgroundColor(GREEN);
                                            holder.cancel.setVisibility(View.INVISIBLE);
                                            break;
                                        case "request_cancel":
                                            holder.cancel.setBackgroundColor(RED);
                                            holder.accept.setVisibility(View.INVISIBLE);
                                            holder.plus5.setVisibility(View.INVISIBLE);
                                            break;
                                        case "request_need_5":
                                            holder.plus5.setBackgroundColor(YELLOW);
                                            break;
                                    }

                                    holder.accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getTypeRef.setValue("request_confirm");
                                        }
                                    });
                                    holder.cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getTypeRef.setValue("request_cancel");
                                        }
                                    });
                                    holder.plus5.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getTypeRef.setValue("request_need_5");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
                        OrderViewHolder holder = new OrderViewHolder(view);
                        return holder;
                    }

                };
        orderList.setAdapter(adapter);
        adapter.startListening();


    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, name, coffee, time;
        Button accept, cancel, plus5;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            name = itemView.findViewById(R.id.clientName);
            coffee = itemView.findViewById(R.id.coffee_barista);
            time = itemView.findViewById(R.id.time_barista);
            accept = itemView.findViewById(R.id.button_ok);
            cancel = itemView.findViewById(R.id.button_cancel);
            plus5 = itemView.findViewById(R.id.button_delay);
        }
    }
}
