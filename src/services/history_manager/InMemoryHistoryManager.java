package services.history_manager;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Node head;
    Node tail;
    Map<Integer, Node> nodes = new HashMap<>();

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            removeNode(nodes.get(task.getId()));
        }

        linkLast(task);
        nodes.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (nodes.isEmpty() || !nodes.containsKey(id)) {
            return;
        }

        Node current = nodes.get(id);
        removeNode(current);
        nodes.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node node = new Node(task);

        if (head != null) {
            tail.next = node;
            node.prev = tail;
        } else {
            head = node;
        }
        tail = node;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();

        if (head == null) {
            return result;
        }

        Node current = head;

        while (current != null) {
            result.add(current.getValue());
            current = current.next;
        }

        return result;
    }

    private static class Node {
        private final Task value;
        private Node next;
        private Node prev;

        public Node(Task value) {
            this.value = value;
        }

        public Task getValue() {
            return value;
        }

    }
}
