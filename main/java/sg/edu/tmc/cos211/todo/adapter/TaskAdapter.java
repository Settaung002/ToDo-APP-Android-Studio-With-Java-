package sg.edu.tmc.cos211.todo.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.edu.tmc.cos211.todo.R;
import sg.edu.tmc.cos211.todo.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onTaskClicked(Task task);

        void onEditClicked(Task task);

        void onDeleteClicked(Task task);

        void onStatusToggled(Task task, boolean isDone);
    }

    private final List<Task> tasks = new ArrayList<>();
    private final TaskActionListener listener;

    public TaskAdapter(TaskActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbStatus;
        private final View viewPriority;
        private final TextView tvTaskName;
        private final TextView tvTaskDescription;
        private final TextView tvStatus;
        private final TextView tvPriority;
        private final TextView tvDueDate;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbStatus = itemView.findViewById(R.id.cbStatus);
            viewPriority = itemView.findViewById(R.id.viewPriority);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(final Task task) {
            tvTaskName.setText(task.getName());

            String description = task.getDescription();
            if (description.isEmpty()) {
                tvTaskDescription.setVisibility(View.GONE);
            } else {
                tvTaskDescription.setVisibility(View.VISIBLE);
                tvTaskDescription.setText(description);
            }

            tvStatus.setText(task.getStatusLabel());
            tvPriority.setText(task.getPriorityLabel());

            String dueDate = task.getDueDate();
            if (dueDate == null || dueDate.isEmpty()) {
                tvDueDate.setVisibility(View.GONE);
            } else {
                tvDueDate.setVisibility(View.VISIBLE);
                tvDueDate.setText(dueDate);
            }

            applyStatusStyle(task.isDone());
            applyPriorityColor(task.getPriority());

            cbStatus.setOnCheckedChangeListener(null);
            cbStatus.setChecked(task.isDone());
            cbStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onStatusToggled(task, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClicked(task);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClicked(task);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClicked(task);
                }
            });
        }

        private void applyStatusStyle(boolean done) {
            if (done) {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskName.setAlpha(0.6f);
                tvStatus.setBackgroundResource(R.drawable.bg_status_done);
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_done));
            } else {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskName.setAlpha(1f);
                tvStatus.setBackgroundResource(R.drawable.bg_status_todo);
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_todo));
            }
        }

        private void applyPriorityColor(int priority) {
            int colorRes;
            switch (priority) {
                case Task.PRIORITY_LOW:
                    colorRes = R.color.priority_low;
                    break;
                case Task.PRIORITY_HIGH:
                    colorRes = R.color.priority_high;
                    break;
                default:
                    colorRes = R.color.priority_medium;
                    break;
            }
            viewPriority.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colorRes));
        }
    }
}
