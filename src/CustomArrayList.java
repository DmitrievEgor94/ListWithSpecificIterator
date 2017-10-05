import java.util.*;

public class CustomArrayList<E> extends ArrayList<E> implements CustomList<E> {

    private ArrayList<E> ignoredElements = new ArrayList<>();

    public void addIgnoredElement(E in){
        ignoredElements.add(in);
    }

    public boolean deleteElementFromIgnoreList(E in){
        return this.getIgnoredElements().remove(in);
    }

    public ArrayList<E> getIgnoredElements() {
        return ignoredElements;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr2();
    }

    @Override
    public ListIterator<E> listIterator() {
        return  new ListItr2(0);
    }

    private class Itr2 implements Iterator<E>{

       int cursor;
       int lastRet = -1;
       int expectedModCount = modCount;

       public E next() {
           checkForComodification();
           setCursorOnNextUnignorePosition();
           int i = cursor;
           if (i >= CustomArrayList.this.size())
               throw new NoSuchElementException();
           Object[] elementData = CustomArrayList.this.toArray();
           if (i >= elementData.length)
               throw new ConcurrentModificationException();
           cursor=i+1;
           return  (E)elementData[lastRet = i];
       }

       private void setCursorOnNextUnignorePosition(){
           Object[] elementData = CustomArrayList.this.toArray();
           List<E> ignoredElements= (ArrayList<E>) CustomArrayList.this.getIgnoredElements();
           int size = CustomArrayList.this.size();

           while(cursor < CustomArrayList.this.size()&&ignoredElements.contains(elementData[cursor]))
               cursor++;
       }

       public boolean hasNext() {
           int savedCursor = cursor;
           setCursorOnNextUnignorePosition();
           int checkCursor = cursor;
           cursor=savedCursor;
           return cursor != CustomArrayList.this.size();
       }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
                checkForComodification();
            try {
                CustomArrayList.this.remove(lastRet);
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

   private class ListItr2 extends Itr2 implements ListIterator<E>{

       ListItr2(int index) {
           super();
           cursor = index;
       }

       @Override
       public boolean hasPrevious() {
           int savedCursor=cursor;
           setCursorOnPreviousUnignorePosition();
           int checkCursor=cursor;
           cursor=savedCursor;
           return (checkCursor)!= 0;
       }

       private void setCursorOnPreviousUnignorePosition(){
           Object[] elementData = CustomArrayList.this.toArray();
           List<E> ignoredElements= (ArrayList<E>) CustomArrayList.this.getIgnoredElements();
           int size = CustomArrayList.this.size();

           if (cursor==CustomArrayList.this.size()) cursor--;
           while(cursor > 0&&ignoredElements.contains(elementData[cursor]))
               cursor--;
       }


       @Override
       public E previous() {
           checkForComodification();
           int savedCursor=cursor;
           int i = cursor-1;
           setCursorOnPreviousUnignorePosition();
           cursor=savedCursor;
           if (i < 0)
               throw new NoSuchElementException();
           Object[] elementData = CustomArrayList.this.toArray();
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
          return cursor-1;
       }

       @Override
       public void set(E e) {
           if (lastRet < 0)
               throw new IllegalStateException();
           checkForComodification();

           try {
               CustomArrayList.this.set(lastRet, e);
           } catch (IndexOutOfBoundsException ex) {
               throw new ConcurrentModificationException();
           }
       }

       @Override
       public void add(E e) {
           checkForComodification();

           try {
               int i = cursor;
               CustomArrayList.this.add(i, e);
               cursor = i + 1;
               lastRet = -1;
               expectedModCount = modCount;
           } catch (IndexOutOfBoundsException ex) {
               throw new ConcurrentModificationException();
           }
       }
   }

    public static void main(String args[]){
        CustomList<Integer> customArrayList = new CustomArrayList();

        customArrayList.add(1);
        customArrayList.add(4);
        customArrayList.add(5);
        customArrayList.add(9);
        customArrayList.add(23);

        System.out.println("Our list: "+customArrayList);

        Iterator<Integer> iterator=customArrayList.iterator();

        customArrayList.addIgnoredElement(1);
        iterator.next();
        iterator.next();
        iterator.remove();
        customArrayList.addIgnoredElement(9);
        System.out.println("Our list without ignored elements and one deleted element: "+customArrayList);

        ListIterator<Integer> listIterator=customArrayList.listIterator();
        listIterator.next();
        listIterator.next();
        System.out.println("ListIterator test work: "+listIterator.previous());


        customArrayList.deleteElementFromIgnoreList(9);
        System.out.println("Our list after deleting one element from ignore list: "+customArrayList);
    }
}
