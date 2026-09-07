package sg.edu.tmc.cos211.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import sg.edu.tmc.cos211.todo.database.TaskDao;
import sg.edu.tmc.cos211.todo.model.Task;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private TaskDao taskDao;
    private Task task;

    private TextView tvTaskName;
    private TextView tvStatus;
    private TextView tvDescription;
    private TextView tvPriority;
    private TextView tvDueDate;
    private TextView tvCreatedAt;
    private TextView tvUpdatedAt;
    private MaterialButton btnToggleStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        taskDao = new TaskDao(this);
        bindViews();

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1L);
        task = taskDao.getById(taskId);
        if (task == null) {
            Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindTask();

        btnToggleStatus.setOnClickListener(v -> toggleStatus());
        findViewById(R.id.btnEdit).setOnClickListener(v -> openEdit());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null) {
            Task refreshed = taskDao.getById(task.getId());
            if (refreshed == null) {
                setResult(RESULT_OK);
                finish();
                return;
            }
            task = refreshed;
            bindTask();
        }
    }

    private void bindViews() {
        tvTaskName = findViewById(R.id.tvTaskName);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);
        tvPriority = findViewById(R.id.tvPriority);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        btnToggleStatus = findViewById(R.id.btnToggleStatus);
    }

    private void bindTask() {
        tvTaskName.setText(task.getName());
        tvStatus.setText(task.getStatusLabel());
        String description = task.getDescription();
        tvDescription.setText(description.isEmpty() ? "-" : description);
        tvPriority.setText(task.getPriorityLabel());
        String due = task.getDueDate();
        tvDueDate.setText(due == null || due.isEmpty() ? getString(R.string.no_due_date) : due);
        tvCreatedAt.setText(task.getCreatedAt());
        tvUpdatedAt.setText(task.getUpdatedAt());

        if (task.isDone()) {
            tvStatus.setBackgroundResource(R.drawable.bg_status_done);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_done));
            btnToggleStatus.setText(R.string.mark_todo);
        } else {
            tvStatus.setBackgroundResource(R.drawable.bg_status_todo);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_todo));
            btnToggleStatus.setText(R.string.mark_done);
        }
    }

    private void toggleStatus() {
        task.toggleStatus();
        taskDao.updateStatus(task.getId(), task.getStatus());
        task = taskDao.getById(task.getId());
        bindTask();
        setResult(RESULT_OK);
    }

    private void openEdit() {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    taskDao.delete(task.getId());
                    Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taskDao != null) {
            taskDao.close();
        }
    }
}
