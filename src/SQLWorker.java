
import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLWorker {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lab7";
    private static final String DB_Driver = "org.postgresql.Driver";
    private static final String USER = "postgres";
    private static final String PASS = "sa";
    private Connection connection;

    public  SQLWorker() throws ClassNotFoundException, SQLException {
        System.out.println("Testing connection to PostgreSQL JDBC");
            Class.forName(DB_Driver);
            System.out.println("PostgreSQL JDBC Driver successfully connected");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public  boolean checkLoginAndPassword(String login, String password){
        try {
            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.users as t where t.login = '" + login + "' and t.password = '" + password + "'");
            int x = set.getMetaData().getColumnCount();
            set.next();
            if (set.getInt(1) == 1){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkRegistration(String login){
        try {
            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.users as t where t.login = '" + login + "'");
            set.next();
            if (set.getInt(1)==0){
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean createUser(String login, String password){
        try {
            connection.createStatement().execute("insert into main.users (login, password) values ('"+ login +"', '"+ password +"')");
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    public String info() {
        try {
            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.collection");
            set.next();
            return  "===\nИнформация о коллекции:\n" +
                    "\tКоллекция типа LinkedList и содержит экземпляры класса Алиса\n" +
                    "\tРазмер коллекции: " + set.getInt(1);
        } catch (SQLException e) {
        }
            return "===\nКоманда не выполнена, попробуйте ещё раз";
    }


    public String show(){
        try {
            ResultSet set = connection.createStatement().executeQuery("select politeness, condition, name, name_of_user, fullness, date, size, x" +
                    " from main.collection order by name");
          //  "\nКласс: Alice\n" + "Имя: " + getName() + "\nВежливость: politeness = " + politeness + "\nDate: " + date+"\nSize: "+ size + "\nСостояние: condition = " + condition + "\nHashcode: " + Integer.toHexString(hashCode())+ "\nКоордината: "+getLocation()+"\ncap: \n\tNameOfUser: " +getName()+"\n\tFullness :"+ cap.getFullness()+'\n';
           StringBuilder statement = new StringBuilder("===\n");
            while(set.next()){
                statement.append("\nКласс: Alice\n" + "Имя: ")
                        .append(set.getString(3))
                        .append("\nВежливость: politeness = ")
                        .append(set.getString(1))
                        .append("\nDate: ")
                        .append(set.getString(6))
                        .append("\nSize: ")
                        .append(set.getString(7))
                        .append("\nСостояние: condition = ")
                        .append(set.getString(2))
                        .append("\nКоордината: ")
                        .append(set.getString(8))
                        .append("\ncap: \n\tNameOfUser: ")
                        .append(set.getString(4))
                        .append("\n\tFullness :")
                        .append(set.getString(5))
                        .append('\n');
            }
            return statement.toString();
        } catch (SQLException e) {
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String add(Alice argument, String login){
        try {
            connection.createStatement().execute("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
                    " values ((select id from main.users where login = '" + login +"')" +
                    ", '" +argument.getPoliteness().toString() +
                    "', 'NORMAL'" +
                    ", '" + argument.getName() + "'" +
                    ", '"+argument.getName()+"'" +
                    ", '"+argument.getfullness()+"'" +
                    ", '"+argument.getDate()+"'" +
                    ", '"+argument.getSize()+"'" +
                    ", '"+argument.getLocation()+"')");
            return "===\nЭлемент добавлен";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String remove_greater(Alice argument, String login){
        try {
            connection.createStatement().execute("delete from main.collection" +
                    " where size > " + argument.getSize() + " and user_id = (select id from main.users where login = '" + login +"')");
            return "===\nКоманда выполнена";
        } catch (SQLException e) {
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String remove_all(Alice argument, String login){
        try {
            connection.createStatement().execute("delete from main.collection" +
                    " where user_id = (select id from main.users where login = '" + login +"')" +
                    " and politeness = '" + argument.getPoliteness().toString() + "'" +
                    " and name = '"+argument.getName() + "'" +
                    " and fullness = " + argument.getfullness() +
                    " and size = " + argument.getSize() +
                    " and x = " + argument.getLocation());
            return "===\nКоманда выполнена";
        } catch (SQLException e) {
        }
        return "===\nКоманда не выполнена";
    }

    public String remove(Alice argument, String login){
        try {
            connection.createStatement().execute("delete from main.collection where ctid in " +
                    "(select ctid from main.collection where user_id = (select id from main.users" +
                    " where login = '"+login+"'" +
                    " and politeness = '"+argument.getPoliteness().toString()+"'" +
                    " and name = '"+argument.getName()+"'" +
                    " and fullness = "+argument.getfullness()+"" +
                    " and size = "+argument.getSize()+"" +
                    " and x = "+argument.getLocation()+" ) limit 1)");
            return "===\nКоманда выполнена";
        } catch (SQLException e) {
        }
        return "===\nКоманда не выполнена";
    }

    public String importCollection (CopyOnWriteArrayList<Alice> collection, String login){
        try {
            for(Alice argument : collection){
            connection.createStatement().execute("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
                    " values ((select id from main.users where login = '" + login +"')" +
                    ", '" +argument.getPoliteness().toString() +
                    "', 'NORMAL'" +
                    ", '" + argument.getName() + "'" +
                    ", '"+argument.getName()+"'" +
                    ", '"+argument.getfullness()+"'" +
                    ", '"+argument.getDate()+"'" +
                    ", '"+argument.getSize()+"'" +
                    ", '"+argument.getLocation()+"')");
            }
            return "===\nКоллекция добавлена";
        }catch (SQLException e){
        }
        return "===\nКоманда не выполена";
    }

    public String show_yours(String login){
        try {
            ResultSet set = connection.createStatement().executeQuery("select politeness, condition, name, name_of_user, fullness, date, size, x" +
                    " from main.collection where user_id = (select id from main.users where login = '" + login + "') order by name");
            //  "\nКласс: Alice\n" + "Имя: " + getName() + "\nВежливость: politeness = " + politeness + "\nDate: " + date+"\nSize: "+ size + "\nСостояние: condition = " + condition + "\nHashcode: " + Integer.toHexString(hashCode())+ "\nКоордината: "+getLocation()+"\ncap: \n\tNameOfUser: " +getName()+"\n\tFullness :"+ cap.getFullness()+'\n';
            StringBuilder statement = new StringBuilder("===\n");
            while(set.next()){
                statement.append("\nКласс: Alice\n" + "Имя: ")
                        .append(set.getString(3))
                        .append("\nВежливость: politeness = ")
                        .append(set.getString(1))
                        .append("\nDate: ")
                        .append(set.getString(6))
                        .append("\nSize: ")
                        .append(set.getString(7))
                        .append("\nСостояние: condition = ")
                        .append(set.getString(2))
                        .append("\nКоордината: ")
                        .append(set.getString(8))
                        .append("\ncap: \n\tNameOfUser: ")
                        .append(set.getString(4))
                        .append("\n\tFullness :")
                        .append(set.getString(5))
                        .append('\n');
            }
            return statement.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }
}
