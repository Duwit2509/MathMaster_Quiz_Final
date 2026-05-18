package duwit.edu.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    TextView txtCauHoi, txtDiem;
    Button btnA, btnB, btnC, btnD;
    int dapAnDung;
    int diem = 0;
    String loaiPhepToan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtCauHoi = findViewById(R.id.txtCauHoi);
        txtDiem = findViewById(R.id.txtDiem);
        btnA = findViewById(R.id.btnA); btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC); btnD = findViewById(R.id.btnD);

        loaiPhepToan = getIntent().getStringExtra("PHEP_TOAN");

        taoCauHoiMoi();

        // Xử lý khi nhấn vào các nút đáp án
        View.OnClickListener answerClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (Integer.parseInt(b.getText().toString()) == dapAnDung) {
                    diem += 10;
                    Toast.makeText(QuizActivity.this, "Chính xác! +10 điểm", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizActivity.this, "Sai rồi! Đáp án là: " + dapAnDung, Toast.LENGTH_SHORT).show();
                }
                txtDiem.setText("Điểm: " + diem);
                taoCauHoiMoi();
            }
        };

        btnA.setOnClickListener(answerClick); btnB.setOnClickListener(answerClick);
        btnC.setOnClickListener(answerClick); btnD.setOnClickListener(answerClick);
    }

    void taoCauHoiMoi() {
        Random r = new Random();
        int so1 = r.nextInt(20) + 1; // Số từ 1-20
        int so2 = r.nextInt(20) + 1;

        if (loaiPhepToan.equals("+")) dapAnDung = so1 + so2;
        else if (loaiPhepToan.equals("-")) {
            if (so1 < so2) { int tmp = so1; so1 = so2; so2 = tmp; } // Đảm bảo không ra số âm
            dapAnDung = so1 - so2;
        } else if (loaiPhepToan.equals("x")) dapAnDung = so1 * so2;
        else {
            dapAnDung = so1;
            so1 = dapAnDung * so2;
            loaiPhepToan = "/";
        }

        txtCauHoi.setText(so1 + " " + loaiPhepToan + " " + so2 + " = ?");

        // Tạo 3 đáp án nhiễu ngẫu nhiên
        int vitriDung = r.nextInt(4);
        Button[] buttons = {btnA, btnB, btnC, btnD};

        // Tạo một mảng để lưu các giá trị đáp án đã được sử dụng
        int[] danhSachDapAn = new int[4];

        for (int i = 0; i < 4; i++) {
            if (i == vitriDung) {
                buttons[i].setText(String.valueOf(dapAnDung));
                danhSachDapAn[i] = dapAnDung; // Lưu lại đáp án đúng
            } else {
                int nhieu;
                boolean biTrung;

                do {
                    biTrung = false;
                    // Tạo số nhiễu ngẫu nhiên (có thể cộng hoặc trừ để đáp án phong phú hơn)
                    if (r.nextBoolean()) {
                        nhieu = dapAnDung + r.nextInt(10) + 1;
                    } else {
                        nhieu = dapAnDung - (r.nextInt(10) + 1);
                        if (nhieu < 0) nhieu = dapAnDung + r.nextInt(10) + 1; // Đảm bảo không âm
                    }

                    // Kiểm tra xem số 'nhieu' vừa tạo có trùng với các ô đã quét qua trước đó không
                    for (int j = 0; j < i; j++) {
                        if (danhSachDapAn[j] == nhieu) {
                            biTrung = true;
                            break;
                        }
                    }
                    // Nếu trùng với chính vị trí đáp án đúng
                    if (nhieu == dapAnDung) {
                        biTrung = true;
                    }

                } while (biTrung);

                // Đã tìm được số độc nhất, gán vào nút và lưu vào danh sách
                buttons[i].setText(String.valueOf(nhieu));
                danhSachDapAn[i] = nhieu;
            }
        }
    }
}