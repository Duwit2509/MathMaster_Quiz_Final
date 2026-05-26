package duwit.edu.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    com.google.firebase.firestore.FirebaseFirestore db;
    com.google.firebase.auth.FirebaseAuth mAuth;
    TextView txtCauHoi, txtDiem, txtSoCau;
    Button btnA, btnB, btnC, btnD;
    int dapAnDung;
    int diem = 0;
    int soCauDaLam = 0;
    String loaiPhepToan;
    String doKho;
    private android.os.CountDownTimer countDownTimer;
    private long thoiGianConLai = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtCauHoi = findViewById(R.id.txtCauHoi);
        txtDiem = findViewById(R.id.txtDiem);
        txtSoCau = findViewById(R.id.txtSoCau);
        btnA = findViewById(R.id.btnA); btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC); btnD = findViewById(R.id.btnD);

        loaiPhepToan = getIntent().getStringExtra("PHEP_TOAN");
        doKho = getIntent().getStringExtra("DO_KHO");

        android.widget.LinearLayout layoutQuizMain = findViewById(R.id.layoutQuizMain);
        View viewBackgroundGoc = findViewById(R.id.viewBackgroundGoc);
        TextView txtTitlePhepToan = findViewById(R.id.txtTitlePhepToan);

        String tenHienThi = "Phép Cộng";
        int[] mauGradient = new int[]{
                android.graphics.Color.parseColor("#5DE6D6"),
                android.graphics.Color.parseColor("#1B97CC")
        };

        if (loaiPhepToan != null) {
            if (loaiPhepToan.equals("-")) {
                tenHienThi = "Phép Trừ";
                mauGradient = new int[]{android.graphics.Color.parseColor("#CFDE65"), android.graphics.Color.parseColor("#C7C30B")};
            } else if (loaiPhepToan.equals("x")) {
                tenHienThi = "Phép Nhân";
                mauGradient = new int[]{android.graphics.Color.parseColor("#AD65D0"), android.graphics.Color.parseColor("#9E1AD7")};
            } else if (loaiPhepToan.equals("/") || loaiPhepToan.equals("÷")) {
                tenHienThi = "Phép Chia";
                mauGradient = new int[]{android.graphics.Color.parseColor("#2FA094"), android.graphics.Color.parseColor("#04A491")};
            }
        }

        // Tự vẽ nền Gradient bằng Java để tránh xung đột XML
        if (viewBackgroundGoc != null) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.TL_BR, mauGradient);
            viewBackgroundGoc.setBackground(gd);
        }

        if (txtTitlePhepToan != null) {
            txtTitlePhepToan.setText("Quiz " + tenHienThi);
        }

        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        taoCauHoiMoi();

        // Xử lý khi nhấn vào các nút đáp án
        View.OnClickListener answerClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                Button b = (Button) v;
                if (Integer.parseInt(b.getText().toString()) == dapAnDung) {
                    diem += 10;
                    Toast.makeText(QuizActivity.this, "Chính xác! +10 điểm", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizActivity.this, "Sai rồi! Đáp án là: " + dapAnDung, Toast.LENGTH_SHORT).show();
                }
                txtDiem.setText("Điểm: " + diem);
                soCauDaLam++;
                // Kiểm tra nếu đã làm đủ 10 câu thì hiện bảng tổng kết, ngược lại thì chơi tiếp
                if (soCauDaLam >= 10) {
                    hienBangTongKet();
                } else {
                    taoCauHoiMoi();
                }
            }
        };

        btnA.setOnClickListener(answerClick); btnB.setOnClickListener(answerClick);
        btnC.setOnClickListener(answerClick); btnD.setOnClickListener(answerClick);

    }

    void taoCauHoiMoi() {
        batDauDemNguoc();
        txtSoCau.setText("Câu: " + (soCauDaLam + 1) + "/10");
        Random r = new Random();
        int maxNumber = 20;

        //Tinh chỉnh giới hạn sinh sô
        if (doKho != null) {
            if (doKho.equals("Dễ")) {
                maxNumber = 10;
            } else if (doKho.equals("Khó")) {
                maxNumber = 100;
            }
        }

        int so1 = r.nextInt(maxNumber) + 1;
        int so2 = r.nextInt(maxNumber) + 1;

        if (loaiPhepToan.equals("+")) dapAnDung = so1 + so2;
        else if (loaiPhepToan.equals("-")) {
            if (so1 < so2) { int tmp = so1; so1 = so2; so2 = tmp; }
            dapAnDung = so1 - so2;
        } else if (loaiPhepToan.equals("x")) dapAnDung = so1 * so2;
        else {
            dapAnDung = so1;
            so1 = dapAnDung * so2;
            loaiPhepToan = "÷";
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
    void hienBangTongKet() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        luuDiemLenFirebase(diem);
        // Tạo một hộp thoại thông báo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("HOÀN THÀNH THỬ THÁCH");
        builder.setMessage("Chúc mừng bé đã hoàn thành 10 câu hỏi!\nĐộ khó: " + doKho + "\n\nTổng số điểm đạt được: " + diem + " điểm");
        builder.setCancelable(false);

        // Nút Chơi lại
        builder.setPositiveButton("Chơi lại", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                diem = 0;
                soCauDaLam = 0;
                txtDiem.setText("Điểm: 0");
                taoCauHoiMoi();
                dialog.dismiss();
            }
        });

        // Nút Về Menu
        builder.setNegativeButton("Về Menu", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                finish();
            }
        });

        // Hiển thị bảng lên màn hình
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void luuDiemLenFirebase(int diemSo) {
        // 1. Lấy thông tin email của người dùng đang đăng nhập hiện tại
        String email = "Ẩn danh";
        if (mAuth.getCurrentUser() != null) {
            email = mAuth.getCurrentUser().getEmail();
        }

        // 2. Gom tất cả dữ liệu thành một gói
        java.util.Map<String, Object> lichSuChoi = new java.util.HashMap<>();
        lichSuChoi.put("email", email);
        lichSuChoi.put("diem", diemSo);
        lichSuChoi.put("phepToan", loaiPhepToan);
        lichSuChoi.put("doKho", doKho);
        lichSuChoi.put("thoiGian", com.google.firebase.Timestamp.now());

        // 3. Đẩy lên collection tên là "BangDiem" trên Firestore
        db.collection("BangDiem")
                .add(lichSuChoi)
                .addOnSuccessListener(documentReference -> {
                    android.util.Log.d("Firebase_Test", "Đã lưu điểm kèm độ khó thành công!");
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("Firebase_Test", "Lỗi lưu điểm: " + e.getMessage());
                });
    }

    private void batDauDemNguoc() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        TextView txtTimer = findViewById(R.id.txtTimer);

        // Khởi tạo bộ đếm
        countDownTimer = new android.os.CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                thoiGianConLai = millisUntilFinished;
                // Hiển thị số giây còn lại lên màn hình
                if (txtTimer != null) {
                    txtTimer.setText("Thời gian: " + (millisUntilFinished / 1000) + "s");
                }
            }

            @Override
            public void onFinish() {
                if (txtTimer != null) {
                    txtTimer.setText("Hết giờ!");
                }

                android.widget.Toast.makeText(QuizActivity.this, "Hết giờ! Bạn bị tính là sai.", android.widget.Toast.LENGTH_SHORT).show();

                soCauDaLam++;
                if (soCauDaLam >= 10) {
                    hienBangTongKet();
                } else {
                    taoCauHoiMoi();
                }
            }
        }.start();
    }
    //Bảo vệ ứng dụng, lỡ người dùng bấm nút Back quay về giữa chừng thì tắt luôn đồng hồ đếm ngầm
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}