package com.example.appbansach;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class ListBookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private List<Book> filteredBookList;
    private TextView addBook;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        //toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Danh sách sách");

        //tạo liên kết đên books trong firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("books");

        recyclerView = findViewById(R.id.recyclerView);
        addBook = findViewById(R.id.addBook);
        SearchView searchView = findViewById(R.id.search_view);

        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();

        fetchBooksFromFirebase();

        addBook.setOnClickListener(v -> {
            Intent intent = new Intent(ListBookActivity.this, addBookActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        adapter = new BookAdapter(this, filteredBookList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(book -> {
            Intent intent = new Intent(ListBookActivity.this, BookDetailActivity.class);
            intent.putExtra("book_id", book.getMaSach());
            startActivity(intent);
        });
    }
    // Lấy danh sách sách trong firebase
    private void fetchBooksFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        bookList.add(book);
                    }
                }
                filteredBookList.clear();
                filteredBookList.addAll(bookList);
                adapter.notifyDataSetChanged();
                if (bookList.isEmpty()) {
                    Toast.makeText(ListBookActivity.this, "Danh sách sách rỗng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListBookActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Tìm kiếm sách theo tên
    private void filterBooks(String query) {
        filteredBookList.clear();
        if (!TextUtils.isEmpty(query)) {
            for (Book book : bookList) {
                if (book.getTenSach().toLowerCase().contains(query.toLowerCase())) {
                    filteredBookList.add(book);
                }
            }
        } else {
            filteredBookList.addAll(bookList);
        }
        adapter.notifyDataSetChanged();
    }
    //quay lai
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
