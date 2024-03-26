public class Stack<T> {
  private T head;
  private Stack<T> tail;
  private static final Stack<?> EMPTY_STACK = new Stack<>(null, null);

  private Stack(T head, Stack<T> tail) {
    this.head = head;
    if (this.tail == null) {
      this.tail = null;
      return;
    }
    this.tail = new Stack<T>(this.tail.head, this.tail.tail);
  }

  public Stack<T> push(T t) {
    return new Stack<T>(t, this);
  }

  public Stack<T> pop() {
    if (this.head == null) {
      throw new IllegalStateException("Stack is empty");
    }
    if (this.tail == null) {
      return (Stack<T>) EMPTY_STACK;
    }
    return new Stack<>(this.tail.head, this.tail.tail);
  }

  public T head() {
    if (this.head == null) {
      throw new IllegalStateException("Stack is empty");
    }
    return head;
  }

  public boolean isEmpty() {
    if (this.head == null) {
      return true;
    } else {
      return false;
    }
  }

  public static <T> Stack<T> createNew() {
    @SuppressWarnings("unchecked")
    Stack<T> emptyStack = (Stack<T>) EMPTY_STACK;
    return emptyStack;
  }
}
