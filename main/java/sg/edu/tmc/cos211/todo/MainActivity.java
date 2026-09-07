package sg.edu.tmc.cos211.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import sg.edu.tmc.cos211.todo.adapter.TaskAdapter;
import sg.edu.tmc.cos211.todo.database.TaskDao;
import sg.edu.tmc.cos211.todo.model.Task;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskActionListener {

    public static final int REQUEST_ADD = 1001;
    public static final int REQUEST_EDIT = 1002;
    public static final int REQUEST_DETAIL = 1003;

    private TaskDao taskDao;
    private TaskAdapter adapter;
    private RecyclerView rvTasks;
    private LinearLayout layoutEmpty;
    private TextView tvTaskCount;
    private TextView tvTodoCount;
    private TextView tvDoneCount;
    private EditText etSearch;

    private int currentFilter = TaskDao.FILTER_ALL;
    private String currentQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        taskDao = new TaskDao(this);
        bindViews();
        setupRecycler();
        setupFilterChips();
        setupSearch();
        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void bindViews() {
        rvTasks = findViewById(R.id.rvTasks);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        tvTodoCount = findViewById(R.id.tvTodoCount);
        tvDoneCount = findViewById(R.id.tvDoneCount);
        etSearch = findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> openAddScreen());
    }

    private void setupRecycler() {
        adapter = new TaskAdapter(this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);
        rvTasks.setHasFixedSize(true);
    }

    private void setupFilterChips() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = checkedIds.isEmpty() ? View.NO_ID : checkedIds.get(0);
            if (checkedId == R.id.chipTodo) {
                currentFilter = TaskDao.FILTER_TODO;
            } else if (checkedId == R.id.chipDone) {
                currentFilter = TaskDao.FILTER_DONE;
            } else {
                currentFilter = TaskDao.FILTER_ALL;
            }
            loadTasks();
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s == null ? "" : s.toString();
                loadTasks();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadTasks() {
        List<Task> tasks = taskDao.search(currentQuery, currentFilter);
        adapter.submitList(tasks);
        updateEmptyState(tasks.isEmpty());
        updateStats();
    }

    private void updateEmptyState(boolean empty) {
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvTasks.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void updateStats() {
        int total = taskDao.countAll();
        int todo = taskDao.countByStatus(Task.STATUS_TODO);
        int done = taskDao.countByStatus(Task.STATUS_DONE);
        tvTaskCount.setText(getString(R.string.task_count, total));
        tvTodoCount.setText(getString(R.string.todo_count, todo));
        tvDoneCount.setText(getString(R.string.done_count, done));
    }

    private void openAddScreen() {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        startActivityForResult(intent, REQUEST_ADD);
    }

    private void openEditScreen(Task task) {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, task.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void openDetailScreen(Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.getId());
        startActivityForResult(intent, REQUEST_DETAIL);
    }

    @Override
    public void onTaskClicked(Task task) {
        openDetailScreen(task);
    }

    @Override
    public void onEditClicked(Task task) {
        openEditScreen(task);
    }

    @Override
    public void onDeleteClicked(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    taskDao.delete(task.getId());
                    Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                    loadTasks();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onStatusToggled(Task task, boolean isDone) {
        int status = isDone ? Task.STATUS_DONE : Task.STATUS_TODO;
        taskDao.updateStatus(task.getId(), status);
        loadTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadTasks();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taskDao != null) {
            taskDao.close();
        }
    }
}
