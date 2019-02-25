package com.example.try_auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class OrderStatusClient extends AppCompatActivity {

    TextView orderIdClient, nameClient, coffeeClient, timeClient, coffeeHouseClient;
    Button acceptClient, cancelClient;
    String orderId;
    Toolbar toolbar;
    String senderUserId, currentState, recieverUserId;
    FirebaseAuth mAuth;
    DatabaseReference orderRequestRef, userRef;
    Order order;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_client);

        orderId = getIntent().getExtras().get("orderId").toString();

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderIdClient = findViewById(R.id.order_id_client);
        nameClient = findViewById(R.id.clientName_client);
        coffeeClient = findViewById(R.id.coffee_client);
        timeClient = findViewById(R.id.time_client);
        coffeeHouseClient = findViewById(R.id.coffee_house_client);
        acceptClient = findViewById(R.id.button_ok_client);
        cancelClient = findViewById(R.id.button_cancel_client);

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("OrderStatus");

        retrieveOrderInfo();


        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        orderRequestRef = FirebaseDatabase.getInstance().getReference().child("Order Request");
//        recieverUserId = getIntent().getExtras().get("recieverUserId").toString();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void retrieveOrderInfo() {
        orderRef.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    order = dataSnapshot.getValue(Order.class);
                    orderIdClient.setText(orderId);
                    nameClient.setText(order.getName());
                    coffeeClient.setText(order.getCoffee());
                    timeClient.setText(order.getTime());
                    coffeeHouseClient.setText(order.getCoffeeHouseName());

                    ManageOrderRequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ManageOrderRequest() {
//        orderRef.child("orderStatus").getRef()
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue().toString().equals("request_sent")) {
//                            acceptClient.setText("Waiting for confirmation ");
//                            acceptClient.setBackgroundColor(YELLOW);
//                        }
//                        if(dataSnapshot.getValue().toString().equals("request_confirm")){
//                            acceptClient.setText("Order confirm");
//                            acceptClient.setBackgroundColor(GREEN);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        orderRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orderRef.child("orderStatus").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (order.getOrderStatus().equals("request_sent")) {
                                    acceptClient.setText("Waiting for confirmation ");
                                    acceptClient.setBackgroundColor(YELLOW);
                                }
                                if (order.getOrderStatus().equals("request_confirm")) {
                                    acceptClient.setVisibility(View.VISIBLE);
                                    acceptClient.setText("Order confirm");
                                    acceptClient.setBackgroundColor(GREEN);
                                    cancelClient.setVisibility(View.INVISIBLE);
                                }
                                if (order.getOrderStatus().equals("request_cancel")) {
                                    acceptClient.setVisibility(View.INVISIBLE);
                                    cancelClient.setBackgroundColor(RED);
                                }
                                if (order.getOrderStatus().equals("request_need_5")) {
                                    acceptClient.setText("Need + 5 min?");
                                    acceptClient.setBackgroundColor(GRAY);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        acceptClient.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (order.getOrderStatus().equals("request_need_5")) {
                                    order.setOrderStatus("confirm+5");
                                    orderRef.child(orderId).setValue(order);
                                    cancelClient.setVisibility(View.INVISIBLE);
                                    acceptClient.setText("Order confirm");
                                    acceptClient.setBackgroundColor(GREEN);
                                }
                            }
                        });

                        cancelClient.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderRef.child(orderId).removeValue();
                                Intent intent = new Intent(OrderStatusClient.this, OrderActivity.class);
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
