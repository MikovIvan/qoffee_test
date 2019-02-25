package com.example.try_auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderActivity extends AppCompatActivity {

    String currentUserId, currentUserName;
    String orderId;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Map<String, String> listOfCafe1 = new HashMap<>();
    EditText time, coffee;
    Button sendOrder;
    Toolbar toolbar;
    Spinner spinnerCoffeeHouse, spinnerCoffeeChoose;
    String senderUserId, recieverUserId, currentState;
    DatabaseReference orderRequestRef, userRef;
    Order order;
    User user;
    Coffee coffeeSelected;
    List<Coffee> coffeeList = new ArrayList<>();
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        spinnerCoffeeChoose = findViewById(R.id.spinner_list_of_drinks);

        initCoffeeList();
        chooseCoffee();


        orderRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentUserId = mAuth.getCurrentUser().getUid();

        time = findViewById(R.id.timeET);

        sendOrder = findViewById(R.id.send_order);


        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order");


        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOrder();
                sendOrderRequest();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        orderRequestRef = FirebaseDatabase.getInstance().getReference().child("Order Request");

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRef.orderByChild("type").equalTo("barista").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spinnerCoffeeHouse = findViewById(R.id.spinner_list_of_cafe);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listOfCafe1.put(ds.child("name").getValue().toString(), ds.child("id").getValue().toString());
                }
                ArrayList<String> listOfCafe = new ArrayList<>(listOfCafe1.keySet());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderActivity.this, android.R.layout.simple_spinner_item, listOfCafe);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCoffeeHouse.setAdapter(adapter);
                spinnerCoffeeHouse.setSelection(0);

                recieverUserId = listOfCafe1.get(spinnerCoffeeHouse.getSelectedItem().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initCoffeeList() {
        coffeeList.add(new Coffee("1", "Капучино", "160"));
        coffeeList.add(new Coffee("2", "Латте", "160"));
        coffeeList.add(new Coffee("3", "Эспрессо", "100"));
        coffeeList.add(new Coffee("4", "Американо", "140"));
    }

    private void chooseCoffee() {
        SpinAdapter adapter = new SpinAdapter(this, R.layout.spin_layout, coffeeList);
        spinnerCoffeeChoose.setAdapter(adapter);
        spinnerCoffeeChoose.setSelection(0);

        spinnerCoffeeChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coffeeSelected = (Coffee) spinnerCoffeeChoose.getSelectedItem();
                String orderButton = "заказать " + coffeeSelected.getPrice();
                sendOrder.setText(orderButton);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createNewOrder() {
        orderId = UUID.randomUUID().toString();
        retrieveUserName();
    }

    private void sendOrder() {
        order.setOrderStatus("request_sent");
        orderRef.child("Orders").child(orderId).setValue(order);
        Intent intent = new Intent(OrderActivity.this, OrderStatusClient.class);
        intent.putExtra("orderId", orderId);
//        intent.putExtra("recieverUserId", recieverUserId);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLoginActivity();
        }
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_logout_option:
                mAuth.signOut();
                sendUserToLoginActivity();
        }

        return true;
    }

    private void retrieveUserName() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    currentUserName = user.getName();
                    coffeeSelected = (Coffee) spinnerCoffeeChoose.getSelectedItem();
                    order = new Order(orderId, currentUserName, coffeeSelected.getName(), time.getText().toString(), recieverUserId, spinnerCoffeeHouse.getSelectedItem().toString(), "order_created");
                    orderRef.child("Orders").child(orderId).setValue(order);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendOrderRequest() {
        orderRequestRef.child(senderUserId).child(recieverUserId).child(orderId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            orderRequestRef.child(recieverUserId).child(senderUserId).child(orderId).child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendOrder();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
