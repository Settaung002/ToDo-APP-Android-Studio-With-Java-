package sg.edu.tmc.cos211.todo.model;

import java.io.Serializable;

/**
 * Domain model for a ToDo task stored in SQLite.
 * Status: 0 = to do, 1 = done.
 * Priority: 0 = low, 1 = medium, 2 = high.
 */
public class Task implements Serializable {

    public static final int STATUS_TODO = 0;
    public static final int STATUS_DONE = 1;

    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    private long id;
    private String name;
    private String description;
    private int status;
    private int priority;
    private String dueDate;
    private String createdAt;
    private String updatedAt;

    public Task() {
        this.status = STATUS_TODO;
        this.priority = PRIORITY_MEDIUM;
        this.description = "";
        this.dueDate = "";
    }

    public Task(String name, int status) {
        this();
        this.name = name;
        this.status = status;
    }

    public Task(long id, String name, String description, int status, int priority,
                String dueDate, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDone() {
        return status == STATUS_DONE;
    }

    public void toggleStatus() {
        status = isDone() ? STATUS_TODO : STATUS_DONE;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate == null ? "" : dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatusLabel() {
        return isDone() ? "Done" : "To Do";
    }

    public String getPriorityLabel() {
        switch (priority) {
            case PRIORITY_LOW:
                return "Low";
            case PRIORITY_HIGH:
                return "High";
            default:
                return "Medium";
        }
    }
}
