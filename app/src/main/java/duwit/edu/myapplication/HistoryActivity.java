package duwit.edu.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView rvHistory;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    List<DocumentSnapshot> listHistory = new ArrayList<>();
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new HistoryAdapter();
        rvHistory.setAdapter(adapter);

        layLichSuTuFirebase();
    }

    private void layLichSuTuFirebase() {
        if (mAuth.getCurrentUser() == null) return;
        String emailCuaToi = mAuth.getCurrentUser().getEmail();

        db.collection("BangDiem")
                .whereEqualTo("email", emailCuaToi)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listHistory.clear();
                    listHistory.addAll(queryDocumentSnapshots.getDocuments());

                    listHistory.sort((doc1, doc2) -> {
                        com.google.firebase.Timestamp t1 = doc1.getTimestamp("thoiGian");
                        com.google.firebase.Timestamp t2 = doc2.getTimestamp("thoiGian");
                        if (t1 != null && t2 != null) {
                            return t2.compareTo(t1);
                        }
                        return 0;
                    });

                    adapter.notifyDataSetChanged();
                    if(listHistory.isEmpty()){
                        Toast.makeText(HistoryActivity.this, "Bé chưa có lịch sử làm bài nào!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HistoryActivity.this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            DocumentSnapshot doc = listHistory.get(position);
            Map<String, Object> data = doc.getData();

            if (data != null) {
                String phepToan = String.valueOf(data.get("phepToan"));
                String doKho = String.valueOf(data.get("doKho"));
                String diem = String.valueOf(data.get("diem"));

                holder.txtInfo.setText("Phép tính: " + phepToan + " (" + doKho + ")");
                holder.txtDiem.setText(diem + "đ");

                // Xử lý định dạng ngày giờ dạng Ngày/Tháng/Năm
                if (data.get("thoiGian") != null) {
                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) data.get("thoiGian");
                    Date date = timestamp.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    holder.txtTime.setText("Thời gian: " + sdf.format(date));
                }
            }
        }

        @Override
        public int getItemCount() {
            return listHistory.size();
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView txtInfo, txtTime, txtDiem;
            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                txtInfo = itemView.findViewById(R.id.txtHistoryInfo);
                txtTime = itemView.findViewById(R.id.txtHistoryTime);
                txtDiem = itemView.findViewById(R.id.txtHistoryDiem);
            }
        }
    }
}