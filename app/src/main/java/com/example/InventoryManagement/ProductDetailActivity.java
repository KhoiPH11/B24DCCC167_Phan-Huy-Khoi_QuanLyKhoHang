package com.example.InventoryManagement;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName, tvCategory, tvPrice, tvQuantity, tvStockValue, tvDescription, tvWarning;
    private Button btnImport, btnSell, btnBack, btnDelete;
    private DatabaseHelper dbHelper;
    private Product product;
    private int productId;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvQuantity = findViewById(R.id.tvDetailQuantity);
        tvStockValue = findViewById(R.id.tvDetailStockValue);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvWarning = findViewById(R.id.tvDetailWarning);
        btnImport = findViewById(R.id.btnImport);
        btnSell = findViewById(R.id.btnSell);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        loadProduct();

        btnImport.setOnClickListener(v -> importOne());
        btnSell.setOnClickListener(v -> sellOne());
        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadProduct() {
        product = dbHelper.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(product.getName());
        tvCategory.setText("Loại: " + product.getCategory());
        tvPrice.setText(currencyFormat.format(product.getPrice()) + " đ");
        tvQuantity.setText("Số lượng: " + product.getQuantity());
        tvStockValue.setText("Giá trị tồn: " + currencyFormat.format(product.getStockValue()) + " đ");
        tvDescription.setText(product.getDescription() != null && !product.getDescription().isEmpty()
                ? product.getDescription() : "(Không có mô tả)");

        if (product.isLowStock()) {
            tvWarning.setVisibility(android.view.View.VISIBLE);
            tvWarning.setText("⚠ Sắp hết hàng");
            tvQuantity.setTextColor(Color.RED);
        } else {
            tvWarning.setVisibility(android.view.View.GONE);
            tvQuantity.setTextColor(Color.parseColor("#333333"));
        }
    }

    private void importOne() {
        int newQuantity = product.getQuantity() + 1;
        dbHelper.updateQuantity(productId, newQuantity);
        Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show();
        loadProduct(); // Cập nhật lại giao diện
    }

    private void sellOne() {
        if (product.getQuantity() <= 0) {
            Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        int newQuantity = product.getQuantity() - 1;
        dbHelper.updateQuantity(productId, newQuantity);
        Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show();
        loadProduct();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm \"" + product.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteProduct(productId);
                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
