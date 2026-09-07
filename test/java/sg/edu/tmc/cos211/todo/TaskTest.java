package sg.edu.tmc.cos211.todo;

import org.junit.Test;

import sg.edu.tmc.cos211.todo.model.Task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskTest {

    @Test
    public void defaultTaskIsTodoWithMediumPriority() {
        Task task = new Task();
        assertEquals(Task.STATUS_TODO, task.getStatus());
        assertEquals(Task.PRIORITY_MEDIUM, task.getPriority());
        assertFalse(task.isDone());
        assertEquals("To Do", task.getStatusLabel());
        assertEquals("Medium", task.getPriorityLabel());
    }

    @Test
    public void sampleAssignmentRowsMapCorrectly() {
        Task reading = new Task("To read and practice Android Development", 0);
        Task gaming = new Task("To play online game", 0);
        Task javaPractice = new Task("To practice Java programming", 1);

        assertEquals("To read and practice Android Development", reading.getName());
        assertEquals(Task.STATUS_TODO, reading.getStatus());
        assertEquals(Task.STATUS_TODO, gaming.getStatus());
        assertEquals(Task.STATUS_DONE, javaPractice.getStatus());
        assertTrue(javaPractice.isDone());
    }

    @Test
    public void toggleStatusSwitchesBetweenTodoAndDone() {
        Task task = new Task("Unit test toggle", Task.STATUS_TODO);
        task.toggleStatus();
        assertTrue(task.isDone());
        assertEquals("Done", task.getStatusLabel());
        task.toggleStatus();
        assertFalse(task.isDone());
        assertEquals("To Do", task.getStatusLabel());
    }

    @Test
    public void priorityLabelsMatchAssignedValues() {
        Task task = new Task();
        task.setPriority(Task.PRIORITY_LOW);
        assertEquals("Low", task.getPriorityLabel());
        task.setPriority(Task.PRIORITY_HIGH);
        assertEquals("High", task.getPriorityLabel());
        task.setPriority(Task.PRIORITY_MEDIUM);
        assertEquals("Medium", task.getPriorityLabel());
    }

    @Test
    public void nullDescriptionAndDueDateReturnEmptyString() {
        Task task = new Task();
        task.setDescription(null);
        task.setDueDate(null);
        assertEquals("", task.getDescription());
        assertEquals("", task.getDueDate());
    }
}
