package duwit.edu.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnCong, btnTru, btnNhan, btnChia, btnSignOut, btnGoToHistory;
    Spinner spnDoKho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ id từ XML
        btnCong = findViewById(R.id.btnCong);
        btnTru = findViewById(R.id.btnTru);
        btnNhan = findViewById(R.id.btnNhan);
        btnChia = findViewById(R.id.btnChia);
        btnSignOut = findViewById(R.id.btnSignOut);
        spnDoKho = findViewById(R.id.spnDoKho);
        btnGoToHistory = findViewById(R.id.btnGoToHistory);

        // Tạo danh sách độ khó cho Spinner
        String[] danhSachDoKho = {"Dễ", "Trung bình", "Khó"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, danhSachDoKho);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spnDoKho.setAdapter(adapter);

        // Viết sự kiện Click chung cho cả 4 nút
        View.OnClickListener gameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phepToan = "+";
                if (v.getId() == R.id.btnCong) phepToan = "+";
                else if (v.getId() == R.id.btnTru) phepToan = "-";
                else if (v.getId() == R.id.btnNhan) phepToan = "x";
                else if (v.getId() == R.id.btnChia) phepToan = "÷";

                String doKhoDaChon = spnDoKho.getSelectedItem().toString();

                // Chuyển màn hình và gửi kèm loại phép toán đã chọn
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("PHEP_TOAN", phepToan);
                intent.putExtra("DO_KHO", doKhoDaChon);
                startActivity(intent);
            }
        };

        btnCong.setOnClickListener(gameClickListener);
        btnTru.setOnClickListener(gameClickListener);
        btnNhan.setOnClickListener(gameClickListener);
        btnChia.setOnClickListener(gameClickListener);

        btnGoToHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        //Viết sự kiện Click cho nút đăng xuất
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lệnh đăng xuất tài khoản khỏi hệ thống Firebase hoàn toàn
                FirebaseAuth.getInstance().signOut();

                // Chuyển người dùng quay ngược lại màn hình Login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}