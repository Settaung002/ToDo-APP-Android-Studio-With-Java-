package sg.edu.tmc.cos211.todo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

import sg.edu.tmc.cos211.todo.database.TaskDao;
import sg.edu.tmc.cos211.todo.model.Task;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private TaskDao taskDao;
    private Task existingTask;
    private boolean isEditMode;

    private TextInputLayout tilTaskName;
    private TextInputEditText etTaskName;
    private TextInputEditText etDescription;
    private RadioGroup rgPriority;
    private MaterialButton btnDueDate;
    private SwitchMaterial swStatus;

    private String selectedDueDate = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        taskDao = new TaskDao(this);
        bindViews();

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1L);
        isEditMode = taskId > 0;
        if (isEditMode) {
            existingTask = taskDao.getById(taskId);
            if (existingTask == null) {
                Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            populateForm(existingTask);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(isEditMode ? R.string.edit_task : R.string.add_task);
        toolbar.setNavigationOnClickListener(v -> finish());

        btnDueDate.setOnClickListener(v -> showDatePicker());
        swStatus.setOnCheckedChangeListener((buttonView, isChecked) ->
                swStatus.setText(isChecked ? R.string.status_done : R.string.status_todo));

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveTask());
    }

    private void bindViews() {
        tilTaskName = findViewById(R.id.tilTaskName);
        etTaskName = findViewById(R.id.etTaskName);
        etDescription = findViewById(R.id.etDescription);
        rgPriority = findViewById(R.id.rgPriority);
        btnDueDate = findViewById(R.id.btnDueDate);
        swStatus = findViewById(R.id.swStatus);
    }

    private void populateForm(Task task) {
        etTaskName.setText(task.getName());
        etDescription.setText(task.getDescription());
        selectedDueDate = task.getDueDate();
        if (selectedDueDate != null && !selectedDueDate.isEmpty()) {
            btnDueDate.setText(selectedDueDate);
        }

        switch (task.getPriority()) {
            case Task.PRIORITY_LOW:
                rgPriority.check(R.id.rbLow);
                break;
            case Task.PRIORITY_HIGH:
                rgPriority.check(R.id.rbHigh);
                break;
            default:
                rgPriority.check(R.id.rbMedium);
                break;
        }

        swStatus.setChecked(task.isDone());
        swStatus.setText(task.isDone() ? R.string.status_done : R.string.status_todo);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDueDate = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    btnDueDate.setText(selectedDueDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.no_due_date),
                (d, which) -> {
                    selectedDueDate = "";
                    btnDueDate.setText(R.string.select_date);
                });
        dialog.show();
    }

    private void saveTask() {
        String name = etTaskName.getText() == null ? "" : etTaskName.getText().toString().trim();
        if (name.isEmpty()) {
            tilTaskName.setError(getString(R.string.error_empty_name));
            etTaskName.requestFocus();
            return;
        }
        tilTaskName.setError(null);

        String description = etDescription.getText() == null
                ? ""
                : etDescription.getText().toString().trim();

        Task task = isEditMode ? existingTask : new Task();
        task.setName(name);
        task.setDescription(description);
        task.setPriority(readPriority());
        task.setDueDate(selectedDueDate);
        task.setStatus(swStatus.isChecked() ? Task.STATUS_DONE : Task.STATUS_TODO);

        if (isEditMode) {
            taskDao.update(task);
            Toast.makeText(this, R.string.task_updated, Toast.LENGTH_SHORT).show();
        } else {
            taskDao.insert(task);
            Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private int readPriority() {
        int checkedId = rgPriority.getCheckedRadioButtonId();
        if (checkedId == R.id.rbLow) {
            return Task.PRIORITY_LOW;
        }
        if (checkedId == R.id.rbHigh) {
            return Task.PRIORITY_HIGH;
        }
        return Task.PRIORITY_MEDIUM;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taskDao != null) {
            taskDao.close();
        }
    }
}
