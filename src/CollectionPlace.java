import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CollectionPlace {

    private CopyOnWriteArrayList<Alice> collection;
    private AtomicBoolean reverse = new AtomicBoolean(true);
    private Date date;

    public CollectionPlace(CopyOnWriteArrayList<Alice> collection){
        this.collection = collection;
        date = new Date();
    }

    public CopyOnWriteArrayList<Alice> getCollection() {
        return collection;
    }

    /**This method makes reverse.
     */
    protected String reorder(){
        if(reverse.get()){
            collection.sort(Comparator.naturalOrder());
            Collections.reverse(collection);
            reverse.lazySet(false);
        } else {
            collection.sort(Comparator.naturalOrder());
            reverse.lazySet(true);
        }
        return  "===\nКоллекция отсортирована в обратном порядке";
    }
    /**This method shows information about collection.
     */
    protected String info(){
        return  "===\nИнформация о коллекции:\n" +
                "\tКоллекция типа LinkedList и содержит экземпляры класса Алиса\n" +
                "\tДата: " + date + "\n\tРазмер коллекции: " + collection.size();
    }
    /**This method shows elements in collection.
     */
    protected String show(){
        if(reverse.get()){
            collection.sort((Comparator<Alice>) (o1, o2) -> {
                return o1.getName().compareTo(o2.getName()) ;
            });
        } else {
            collection.sort((Comparator<Alice>) (o1, o2) -> {
                return o1.getName().compareTo(o2.getName()) ;
            });
            Collections.reverse(collection);
        }
        return "===\n"+collection;
    }
    /**This method add new element in collection.
     * @param aliceforadd - Alice.class, which you want add.
     */
    protected String  add(Alice aliceforadd){
        collection.add(aliceforadd);
        return  "===\nЭлемент добавлен";
    }
    /**This method removes all alices, which greater than parametr.
     * @param primerforevery - Alice.class, which will be compared with alices in Collection.
     */
    protected String remove_greater(Alice primerforevery){
        Comparator<Alice> comparator = Comparator.naturalOrder();
        long count = collection.stream().filter((e)->comparator.compare(primerforevery,e)<0).count();
        collection.stream().filter((e)->comparator.compare(primerforevery,e)<0).forEach((e)->collection.remove(e));
        return "===\nКоличество удалённых алис: "+count;
    }
    /**This method removes all alices, which equal than parametr.
     * @param aliceforcompare - Alice.class, which will be compared with alices in Collection.
     */
    protected String remove_all(Alice aliceforcompare){
       long count = collection.stream().filter((e)->e.equals(aliceforcompare)).count();
        collection.stream().filter((e)->e.equals(aliceforcompare)).forEach((e)->collection.remove(e));
        return "===\nКоличество удалённых алис: "+count;
    }
    /**This method removes alice in collection equal to parametr.
     * @param aliceforremove - Alice.class, which will be compared with alices in Collection.
     */
    protected String remove(Alice aliceforremove) {
        if (collection.remove(aliceforremove)) return "===\nЭлемент удалён";
        else
            return "===\nТакого элемента и не было";
    }

    protected String importCollection (CopyOnWriteArrayList<Alice> collection){
        this.collection = collection;
        date = new Date();
        return "===\nКоллекция загружена на сервер";
    }

    protected synchronized String save(){
        return ServerReaderSaver.save(collection);
    }

    protected synchronized String load(){
        try {
            collection = ServerReaderSaver.justReadFile();
            return "===\nКоллекция загружена";
        } catch (FileNotFoundException e) {
            return  e.getMessage();
        }
    }
}