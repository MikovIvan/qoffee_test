package com.example.try_auth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderViewBarista extends AppCompatActivity {


    TextView orderId, name, coffee, time;
    Button accept, cancel, plus5;
    DatabaseReference orderRef;
    Toolbar toolbar;
    private String orderUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_card);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
//video 31

        orderUserId = getIntent().getExtras().get("orderId").toString();
        Toast.makeText(this, "Order Id " + orderUserId, Toast.LENGTH_SHORT).show();

        orderId = findViewById(R.id.order_id);
        name = findViewById(R.id.clientName);
        coffee = findViewById(R.id.coffee_barista);
        time = findViewById(R.id.time_barista);
        accept = findViewById(R.id.button_ok);
        cancel = findViewById(R.id.button_cancel);
        plus5 = findViewById(R.id.button_delay);
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Barista");

        retrieveOrderInfo();
    }

    //video 33
    private void retrieveOrderInfo() {
        orderRef.child(orderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Order order = dataSnapshot.getValue(Order.class);

                    orderId.setText(orderUserId);
                    name.setText(order.getName());
                    coffee.setText(order.getCoffee());
                    time.setText(order.getTime());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
