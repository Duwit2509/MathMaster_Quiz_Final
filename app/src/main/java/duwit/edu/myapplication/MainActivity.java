package duwit.edu.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnCong, btnTru, btnNhan, btnChia, btnSignOut;

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

        // Viết sự kiện Click chung cho cả 4 nút
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phepToan = "";
                if (v.getId() == R.id.btnCong) phepToan = "+";
                else if (v.getId() == R.id.btnTru) phepToan = "-";
                else if (v.getId() == R.id.btnNhan) phepToan = "x";
                else if (v.getId() == R.id.btnChia) phepToan = "/";

                // Chuyển màn hình và gửi kèm loại phép toán đã chọn
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("PHEP_TOAN", phepToan);
                startActivity(intent);
            }
        };

        btnCong.setOnClickListener(clickListener);
        btnTru.setOnClickListener(clickListener);
        btnNhan.setOnClickListener(clickListener);
        btnChia.setOnClickListener(clickListener);

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