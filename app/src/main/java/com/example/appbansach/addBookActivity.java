package com.example.appbansach;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class addBookActivity extends AppCompatActivity {

    private EditText etMaSach, etTenSach, etTenTheLoai, etTacGia, etDonGia, etSoLuong, etTenNXB;
    private TextView btnAddBook, tvTheLoai;
    private DatabaseReference mDatabase;
    private Toolbar mlToolbar;
    private List<String> genreList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        mlToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mlToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
        getSupportActionBar().setTitle("Thêm sách");

        // Khởi tạo Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Kết nối các view
        etMaSach = findViewById(R.id.etMaSach);
        etTenSach = findViewById(R.id.etTenSach);
        etTenTheLoai = findViewById(R.id.etTenTheLoai);
        etTacGia = findViewById(R.id.etTacGia);
        etDonGia = findViewById(R.id.etDonGia);
        etSoLuong = findViewById(R.id.etSoLuong);
        etTenNXB = findViewById(R.id.etTenNXB);
        btnAddBook = findViewById(R.id.btnAddBook);
        tvTheLoai = findViewById(R.id.tvTheLoai);

        etTenTheLoai.setEnabled(false);
        etTenTheLoai.setFocusable(false);
        // Thiết lập sự kiện click cho nút thêm sách
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToFirebase();
            }
        });
        tvTheLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenreDialog(etTenTheLoai);
            }
        });
    }

    private void addBookToFirebase() {
        // Lấy dữ liệu từ các EditText
        String maSach = etMaSach.getText().toString().trim();
        String tenSach = etTenSach.getText().toString().trim();
        String tenTheLoai = etTenTheLoai.getText().toString().trim();
        String tacGia = etTacGia.getText().toString().trim();
        String donGiaStr = etDonGia.getText().toString().trim();
        String soLuongStr = etSoLuong.getText().toString().trim();
        String tenNXB = etTenNXB.getText().toString().trim();

        // Kiểm tra xem trường nào bị bỏ trống
        if (maSach.isEmpty() || tenSach.isEmpty() || tenTheLoai.isEmpty() || tacGia.isEmpty() ||
                donGiaStr.isEmpty() || soLuongStr.isEmpty() || tenNXB.isEmpty()) {
            // Hiển thị thông báo lỗi hoặc một toast
            Toast.makeText(addBookActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi donGia và soLuong từ chuỗi sang số
        double donGia;
        int soLuong;
        try {
            donGia = Double.parseDouble(donGiaStr);
            soLuong = Integer.parseInt(soLuongStr);
        } catch (NumberFormatException e) {
            Toast.makeText(addBookActivity.this, "Đơn giá và số lượng phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem MaSach đã tồn tại chưa
        mDatabase.child("books").child(maSach).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // MaSach đã tồn tại
                    showAlertDialog("Mã sách đã tồn tại", "Vui lòng nhập mã sách khác.");
                    etMaSach.requestFocus();
                } else {
                    // MaSach chưa tồn tại, thêm sách mới
                    Book book = new Book(maSach, tenSach, tenTheLoai, tacGia, donGia, soLuong, tenNXB, "");
                    mDatabase.child("books").child(maSach).setValue(book)
                            .addOnSuccessListener(aVoid -> {
                                // Ghi thành công
                                // Hiển thị thông báo thành công
                                Toast.makeText(addBookActivity.this, "Thêm sách thành công", Toast.LENGTH_SHORT).show();

                                // Xóa nội dung các trường nhập liệu
                                etMaSach.setText("");
                                etTenSach.setText("");
                                etTenTheLoai.setText("");
                                etTacGia.setText("");
                                etDonGia.setText("");
                                etSoLuong.setText("");
                                etTenNXB.setText("");
                            })
                            .addOnFailureListener(e -> {
                                // Ghi thất bại
                                // Hiển thị thông báo lỗi
                                Toast.makeText(addBookActivity.this, "Thêm sách thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(addBookActivity.this, "Kiểm tra mã sách thất bại: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showGenreDialog(EditText EditText) {
        mDatabase.child("Genres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                genreList.clear();
                for (DataSnapshot genreSnapshot : dataSnapshot.getChildren()) {
                    String genre = genreSnapshot.getValue(String.class);
                    genreList.add(genre);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(addBookActivity.this);
                builder.setTitle("Chọn thể loại");

                String[] genresArray = genreList.toArray(new String[0]);
                builder.setItems(genresArray, (dialog, which) -> {
                    String selectedGenre = genresArray[which];
                    EditText.setText(selectedGenre);
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(addBookActivity.this, "Không tải được thể loại", Toast.LENGTH_SHORT).show();
            }
        });
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
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(addBookActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
