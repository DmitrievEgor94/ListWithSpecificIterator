import java.util.*;

public class FilterList<E> extends ArrayList<E> {

    private List<E> ignoredElements = new ArrayList<>();


    public FilterList(List<E> elements, E... ignoredElements) {
        super();

        for (E element : elements) {
            this.add(element);
        }

        for (E ignoredElement : ignoredElements) {
            this.ignoredElements.add(ignoredElement);
        }
    }


    public List<E> getIgnoredElements() {
        return ignoredElements;
    }

    public boolean add(E e) {
        if (ignoredElements.contains(e)) return false;
        else return super.add(e);

    }

    public Iterator<E> iterator() {
        return new CustomItr();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new CustomListCustomItr(0);
    }

    private class CustomItr implements Iterator<E> {

        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        public E next() {
            checkForComodification();
            setCursorOnNextUnignorePosition();
            int i = cursor;
            if (i >= FilterList.this.size())
                throw new NoSuchElementException();
            Object[] elementData = FilterList.this.toArray();
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        private void setCursorOnNextUnignorePosition() {
            Object[] elementData = FilterList.this.toArray();
            List<E> ignoredElements = (ArrayList<E>) FilterList.this.getIgnoredElements();

            while (cursor < FilterList.this.size() && ignoredElements.contains(elementData[cursor]))
                cursor++;
        }

        public boolean hasNext() {
            int savedCursor = cursor;
            setCursorOnNextUnignorePosition();
            int checkCursor = cursor;
            cursor = savedCursor;
            return checkCursor != FilterList.this.size();
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();
            try {
                FilterList.this.remove(lastRet);
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class CustomListCustomItr extends CustomItr implements ListIterator<E> {

        CustomListCustomItr(int index) {
            super();
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            int savedCursor = cursor;
            setCursorOnPreviousUnignorePosition();
            int checkCursor = cursor;
            cursor = savedCursor;
            return (checkCursor) != 0;
        }

        private void setCursorOnPreviousUnignorePosition() {
            Object[] elementData = FilterList.this.toArray();
            List<E> ignoredElements = (ArrayList<E>) FilterList.this.getIgnoredElements();

            int size = FilterList.this.size();

            if (cursor == FilterList.this.size()) cursor--;
            while (cursor >= 0 && ignoredElements.contains(elementData[cursor]))
                cursor--;
        }


        @Override
        public E previous() {
            checkForComodification();
            int savedCursor = cursor;
            cursor--;
            setCursorOnPreviousUnignorePosition();
            int i = cursor;
            cursor = savedCursor;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = FilterList.this.toArray();
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        @Override
        public int nextIndex() {
            super.setCursorOnNextUnignorePosition();
            return cursor;
        }

        @Override
        public int previousIndex() {
            setCursorOnPreviousUnignorePosition();
            return cursor - 1;
        }

        @Override
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                FilterList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                FilterList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public static void main(String args[]) {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        List<Integer> filterList = new FilterList<>(list, 2, 1);

        filterList.add(1);
        filterList.add(4);

        System.out.println("Our list: " + filterList);

        filterList.add(1);
        filterList.add(345);

        Iterator<Integer> iterator = filterList.iterator();


        iterator.next();
        iterator.next();
        iterator.remove();
        System.out.println("Our list without ignored elements and one deleted element: " + filterList);

        ListIterator<Integer> listIterator = filterList.listIterator();
        listIterator.next();
        listIterator.next();
        listIterator.previous();
        System.out.println("ListIterator test work: " + listIterator.previous());


        System.out.println("Our list after deleting one element from ignore list: " + filterList);
    }
}
