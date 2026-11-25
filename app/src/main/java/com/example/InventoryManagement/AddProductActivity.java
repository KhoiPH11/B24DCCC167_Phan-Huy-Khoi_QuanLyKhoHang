package com.example.InventoryManagement;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Spinner spnCategory;
    private Switch swActive;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private static final String[] CATEGORIES = {
            "Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spnCategory = findViewById(R.id.spnCategory);
        swActive = findViewById(R.id.swActive);
        btnSave = findViewById(R.id.btnSave);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        // Mặc định bật Switch
        swActive.setChecked(true);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;

        // Validate tên
        if (name.isEmpty()) {
            edtName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        // Validate giá
        if (priceStr.isEmpty()) {
            edtPrice.setError("Giá bán không được để trống");
            edtPrice.requestFocus();
            return;
        }
        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        // Validate số lượng
        if (quantityStr.isEmpty()) {
            edtQuantity.setError("Số lượng không được để trống");
            edtQuantity.requestFocus();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            return;
        }

        // Lưu vào database
        Product product = new Product(name, price, quantity, category, active, description);
        long id = dbHelper.insertProduct(product);

        if (id > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về MainActivity, onResume sẽ load lại
        } else {
            Toast.makeText(this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
