package com.example.appbansach;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;import java.text.NumberFormat;
import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Khởi tạo Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("books");

        // Thiết lập Toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
        getSupportActionBar().setTitle("Chi tiết sách");
        // Lấy dữ liệu từ Intent và truy vấn chi tiết sách từ Firebase
        Intent intent = getIntent();
        if (intent != null) {
            String bookId = intent.getStringExtra("book_id");
            fetchBookDetails(bookId);
        }
    }

    private void fetchBookDetails(String bookId) {
        mDatabase.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                if (book != null) {
                    // Hiển thị thông tin sách lên giao diện
                    displayBookDetails(book);
                } else {
                    Toast.makeText(BookDetailActivity.this, "Không tìm thấy thông tin sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookDetailActivity.this, "Lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBookDetails(Book book) {

        double donGia = book.getDonGia();

        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        String formattedDonGia = vnFormat.format(donGia);

        TextView txtMaSach = findViewById(R.id.txtMaSach);
        TextView txtTenSach = findViewById(R.id.txtTenSach);
        TextView txtTacGia = findViewById(R.id.txtTacGia);
        TextView txtTheLoai = findViewById(R.id.txtTheLoai);
        TextView txtDonGia = findViewById(R.id.txtDonGia);
        TextView txtSoLuong = findViewById(R.id.txtSoLuong);
        TextView txtTenNXB = findViewById(R.id.txtTenNXB);


        txtMaSach.setText(book.getMaSach());
        txtTenSach.setText(book.getTenSach());
        txtTacGia.setText(book.getTacGia());
        txtTheLoai.setText(book.getTenTheLoai());
        txtDonGia.setText(formattedDonGia);
        txtSoLuong.setText(String.valueOf(book.getSoLuong()));
        txtTenNXB.setText(book.getTenNXB());
    }

    // Xử lý sự kiện khi người dùng nhấn nút quay lại trên ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Khi người dùng nhấn nút back trên ActionBar, quay lại màn hình trước đó
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
