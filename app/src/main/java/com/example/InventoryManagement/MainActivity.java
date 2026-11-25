package com.example.InventoryManagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts, tvTotalValue, tvLowStock;
    private ListView lvProducts;
    private Button btnAddProduct;
    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private List<Product> productList;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStock = findViewById(R.id.tvLowStock);
        lvProducts = findViewById(R.id.lvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        productList = dbHelper.getAllProducts();
        adapter = new ProductAdapter();
        lvProducts.setAdapter(adapter);

        // Cập nhật thống kê
        int totalProducts = dbHelper.getProductCount();
        long totalValue = dbHelper.getTotalValue();
        int lowStock = dbHelper.countLowStock();

        tvTotalProducts.setText("Tổng SP: " + totalProducts);
        tvTotalValue.setText("Tổng giá trị: " + currencyFormat.format(totalValue) + " đ");
        tvLowStock.setText("Sắp hết: " + lowStock);
    }

    // Custom Adapter cho ListView
    private class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int position) {
            return productList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return productList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.item_product, parent, false);
            }

            Product product = productList.get(position);

            TextView tvName = convertView.findViewById(R.id.tvItemName);
            TextView tvPrice = convertView.findViewById(R.id.tvItemPrice);
            TextView tvCategory = convertView.findViewById(R.id.tvItemCategory);
            TextView tvQuantity = convertView.findViewById(R.id.tvItemQuantity);
            TextView tvWarning = convertView.findViewById(R.id.tvItemWarning);

            tvName.setText(product.getName());
            tvPrice.setText(currencyFormat.format(product.getPrice()) + " đ");
            tvCategory.setText(product.getCategory());
            tvQuantity.setText("SL: " + product.getQuantity());

            if (product.isLowStock()) {
                tvWarning.setVisibility(View.VISIBLE);
                tvWarning.setText("Sắp hết hàng");
                tvQuantity.setTextColor(Color.RED);
            } else {
                tvWarning.setVisibility(View.GONE);
                tvQuantity.setTextColor(Color.parseColor("#333333"));
            }

            return convertView;
        }
    }
}
