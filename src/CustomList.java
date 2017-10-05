import java.util.List;

public interface CustomList<T> extends List<T>{

    void addIgnoredElement(T o);
    boolean deleteElementFromIgnoreList(T in);
}
