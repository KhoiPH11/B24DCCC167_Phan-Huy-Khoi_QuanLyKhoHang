package com.example.InventoryManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Bảng products
    private static final String TABLE_PRODUCTS = "products";
    private static final String COL_PRODUCT_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_CATEGORY = "category";
    private static final String COL_ACTIVE = "active";
    private static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsers);

        // Tạo bảng products
        String createProducts = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PRICE + " INTEGER NOT NULL, " +
                COL_QUANTITY + " INTEGER NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_ACTIVE + " INTEGER NOT NULL, " +
                COL_DESCRIPTION + " TEXT)";
        db.execSQL(createProducts);

        // Chèn tài khoản mẫu
        insertUser(db, "admin", "123456");

        // Chèn 3 sản phẩm mẫu
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột không dây Logitech"));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ RGB"));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay A5 bìa cứng"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // ========== USERS ==========

    private void insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
    }

    public void insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        insertUser(db, username, password);
        db.close();
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ========== PRODUCTS ==========

    private long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        values.put(COL_PRICE, p.getPrice());
        values.put(COL_QUANTITY, p.getQuantity());
        values.put(COL_CATEGORY, p.getCategory());
        values.put(COL_ACTIVE, p.getActive());
        values.put(COL_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = insertProduct(db, p);
        db.close();
        return id;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, COL_PRODUCT_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Product p = cursorToProduct(cursor);
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_PRODUCT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            product = cursorToProduct(cursor);
        }
        cursor.close();
        db.close();
        return product;
    }

    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        int rows = db.update(TABLE_PRODUCTS, values, COL_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COL_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_PRODUCTS, null);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_QUANTITY + " < 5", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
        int active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active, description);
    }
}
