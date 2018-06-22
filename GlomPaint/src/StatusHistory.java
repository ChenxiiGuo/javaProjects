
// This class is used to do redo and undo
// This class can memory the status of what user did.
// For general circumstance, T is used.

import java.util.ArrayDeque;
import java.util.Deque;



public class StatusHistory<T> {
    private Deque<T> redoHistory;
    private Deque<T> undoHistory;

    final int size = 6;

    // the status of this class
    private T status;

    //size is the number of status. If it is 5 then user can do undo 5 times
    StatusHistory() {

        redoHistory = new ArrayDeque<>();
        undoHistory = new ArrayDeque<>();
    }

    // redo can only be done right after undo.
    // if user do any other thing, redo history will be removed.

    public void addToHistory(T newStatus) {
        status = newStatus;
        undoHistory.offerLast(newStatus);
        if (undoHistory.size() > size) {
            undoHistory.pollFirst();
        }
        redoHistory.clear();
    }

    //return null is allowed
    public T undo() {
        if (undoHistory.size() == 1) {
            return undoHistory.peekLast();
        }
        status = undoHistory.pollLast();
        if (status != null){
            redoHistory.offerFirst(status);
        }
        status = undoHistory.peekLast();
        return status;
    }

    public T redo() {
         status = redoHistory.pollFirst();
         if (status != null) {
             undoHistory.offerLast(status);
         }
         return status;
    }

    public int size() {
        return undoHistory.size();
    }







}
