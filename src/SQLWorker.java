
import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLWorker {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lab7";
    private static final String DB_Driver = "org.postgresql.Driver";
    private static final String USER = "";
    private static final String PASS = "";
    private Connection connection;

    public  SQLWorker() throws ClassNotFoundException, SQLException {
        System.out.println("Testing connection to PostgreSQL JDBC");
            Class.forName(DB_Driver);
            System.out.println("PostgreSQL JDBC Driver successfully connected");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public  boolean checkLoginAndPassword(String login, String password){
        try {
//            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.users as t where t.login = '" + login + "' and t.password = '" + password + "'");
//            int x = set.getMetaData().getColumnCount();
//            set.next();
//            if (set.getInt(1) == 1){
//                return true;
//            }
            PreparedStatement statement = connection.prepareStatement("select count(*) from main.users as t where t.login = ? and t.password = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet set = statement.executeQuery();
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
//            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.users as t where t.login = '" + login + "'");
//            set.next();
//            if (set.getInt(1)==0){
//                return true;
//            }
            PreparedStatement statement = connection.prepareStatement("select count(*) from main.users as t where t.login = ?");
            statement.setString(1, login);
            ResultSet set = statement.executeQuery();
            set.next();
            if (set.getInt(1)==0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(String login, String password){
        try {
            PreparedStatement statement = connection.prepareStatement("insert into main.users (login, password) values (?, ?)");
            statement.setString(1, login);
            statement.setString(2, password);
//            connection.createStatement().execute("insert into main.users (login, password) values ('"+ login +"', '"+ password +"')");
//            return true;
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String add(Alice argument, String login){
        try {
            PreparedStatement statement = connection.prepareStatement("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
                    " values ((select id from main.users where login = ?)" +
                    ", ?, 'NORMAL', ?, ?, ?, '"+argument.getDate()+"', ?, ?)");
            statement.setString(1, login);
            statement.setString(2, argument.getPoliteness().toString());
            statement.setString(3, argument.getName());
            statement.setString(4, argument.getName());
            statement.setInt(5, argument.getfullness());
            statement.setInt(6, argument.getSize());
            statement.setInt(7, argument.getLocation());
            statement.execute();
//            connection.createStatement().execute("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
//                    " values ((select id from main.users where login = '" + login +"')" +
//                    ", '" +argument.getPoliteness().toString() +
//                    "', 'NORMAL'" +
//                    ", '" + argument.getName() + "'" +
//                    ", '"+argument.getName()+"'" +
//                    ", '"+argument.getfullness()+"'" +
//                    ", '"+argument.getDate()+"'" +
//                    ", '"+argument.getSize()+"'" +
//                    ", '"+argument.getLocation()+"')");
                return "===\nЭлемент добавлен";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String remove_greater(Alice argument, String login){
        try {
            PreparedStatement statement = connection.prepareStatement("select count(*) from main.collection" +
                    " where size > ? and user_id = (select id from main.users where login = ?)");
            statement.setInt(1, argument.getSize());
            statement.setString(2, login);
            ResultSet set = statement.executeQuery();
            set.next();
            int i = set.getInt(1);
            PreparedStatement statement1 = connection.prepareStatement("delete from main.collection" +
                    " where size > ? and user_id = (select id from main.users where login = ?)");
            statement1.setInt(1, argument.getSize());
            statement1.setString(2, login);
            statement1.execute();
//            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.collection" +
//                    " where size > " + argument.getSize() + " and user_id = (select id from main.users where login = '" + login +"')");
//            set.next();
//            int i = set.getInt(1);
//            connection.createStatement().execute("delete from main.collection" +
//                    " where size > " + argument.getSize() + " and user_id = (select id from main.users where login = '" + login +"')");
            return "===\nУдалено объектов: " + i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена, попробуйте ещё раз";
    }

    public String remove_all(Alice argument, String login){
        try {
            PreparedStatement statement = connection.prepareStatement("select count(*) from main.collection" +
                    " where user_id = (select id from main.users where login = ?) and politeness = ? and name = ? and fullness = ? and size = ? and x = ?");
            statement.setString(1, login);
            statement.setString(2, argument.getPoliteness().toString());
            statement.setString(3, argument.getName());
            statement.setInt(4, argument.getfullness());
            statement.setInt(5, argument.getSize());
            statement.setInt(6, argument.getLocation());
            ResultSet set = statement.executeQuery();
//            ResultSet set = connection.createStatement().executeQuery("select count(*) from main.collection" +
//                    " where user_id = (select id from main.users where login = '" + login +"')" +
//                    " and politeness = '" + argument.getPoliteness().toString() + "'" +
//                    " and name = '"+argument.getName() + "'" +
//                    " and fullness = " + argument.getfullness() +
//                    " and size = " + argument.getSize() +
//                    " and x = " + argument.getLocation());
            set.next();
            int i = set.getInt(1);
            PreparedStatement statement1 = connection.prepareStatement("delete from main.collection" +
                    " where user_id = (select id from main.users where login = ?) and politeness = ? and name = ? and fullness = ? and size = ? and x = ?");
            statement1.setString(1, login);
            statement1.setString(2, argument.getPoliteness().toString());
            statement1.setString(3, argument.getName());
            statement1.setInt(4, argument.getfullness());
            statement1.setInt(5, argument.getSize());
            statement1.setInt(6, argument.getLocation());
            statement1.execute();
//            connection.createStatement().execute("delete from main.collection" +
//                    " where user_id = (select id from main.users where login = '" + login +"')" +
//                    " and politeness = '" + argument.getPoliteness().toString() + "'" +
//                    " and name = '"+argument.getName() + "'" +
//                    " and fullness = " + argument.getfullness() +
//                    " and size = " + argument.getSize() +
//                    " and x = " + argument.getLocation());
            return "===\nУдалено объектов: " + i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена";
    }

    public String remove(Alice argument, String login){
        try {
//            ResultSet set = connection.createStatement().executeQuery("select count (*) from main.collection" +
//                    " where user_id = (select id from main.users where login = '" + login +"')" +
//                    " and politeness = '" + argument.getPoliteness().toString() + "'" +
//                    " and name = '"+argument.getName() + "'" +
//                    " and fullness = " + argument.getfullness() +
//                    " and size = " + argument.getSize() +
//                    " and x = " + argument.getLocation());
            PreparedStatement statement = connection.prepareStatement("select count(*) from main.collection" +
                    " where user_id = (select id from main.users where login = ?) and date = '"+argument.getDate()+"' and politeness = ? and name = ? and fullness = ? and size = ? and x = ?");
            statement.setString(1, login);
            statement.setString(2, argument.getPoliteness().toString());
            statement.setString(3, argument.getName());
            statement.setInt(4, argument.getfullness());
            statement.setInt(5, argument.getSize());
            statement.setInt(6, argument.getLocation());
            ResultSet set = statement.executeQuery();
            set.next();
            int i = set.getInt(1);
//            connection.createStatement().execute("delete from main.collection where ctid in " +
//                    "(select ctid from main.collection where user_id = (select id from main.users" +
//                    " where login = '"+login+"'" +
//                    " and politeness = '"+argument.getPoliteness().toString()+"'" +
//                    " and name = '"+argument.getName()+"'" +
//                    " and fullness = "+argument.getfullness()+"" +
//                    " and size = "+argument.getSize()+"" +
//                    " and x = "+argument.getLocation()+" ) limit 1)");
            PreparedStatement statement1 = connection.prepareStatement("delete from main.collection where ctid in " +
                    "(select ctid from main.collection where user_id = (select id from main.users where login = ?  and date = '"+argument.getDate()+"' and politeness = ? and name = ? and fullness = ? and size = ? and x = ?) limit 1 )");
            statement1.setString(1, login);
            statement1.setString(2, argument.getPoliteness().toString());
            statement1.setString(3, argument.getName());
            statement1.setInt(4, argument.getfullness());
            statement1.setInt(5, argument.getSize());
            statement1.setInt(6, argument.getLocation());
            statement1.execute();
            if(i>0) return "===\nОбъект удален"; else
            return "===\nТакого объекта нет";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "===\nКоманда не выполнена";
    }

    public String importCollection (CopyOnWriteArrayList<Alice> collection, String login){
        try {
            for(Alice argument : collection){
//            connection.createStatement().execute("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
//                    " values ((select id from main.users where login = '" + login +"')" +
//                    ", '" +argument.getPoliteness().toString() +
//                    "', 'NORMAL'" +
//                    ", '" + argument.getName() + "'" +
//                    ", '"+argument.getName()+"'" +
//                    ", '"+argument.getfullness()+"'" +
//                    ", '"+argument.getDate()+"'" +
//                    ", '"+argument.getSize()+"'" +
//                    ", '"+argument.getLocation()+"')");
                PreparedStatement statement = connection.prepareStatement("insert into main.collection (user_id, politeness, condition, name, name_Of_User, fullness, date, size, x)" +
                        " values ((select id from main.users where login = ?)" +
                        ", ?, 'NORMAL', ?, ?, ?, '"+argument.getDate()+"', ?, ?)");
                statement.setString(1, login);
                statement.setString(2, argument.getPoliteness().toString());
                statement.setString(3, argument.getName());
                statement.setString(4, argument.getName());
                statement.setInt(5, argument.getfullness());
                statement.setInt(6, argument.getSize());
                statement.setInt(7, argument.getLocation());
                statement.execute();
            }
            return "===\nКоллекция добавлена";
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "===\nКоманда не выполена";
    }

    public String show_yours(String login){
        try {
            PreparedStatement statement1 = connection.prepareStatement("select politeness, condition, name, name_of_user, fullness, date, size, x" +
                    " from main.collection where user_id = (select id from main.users where login = ?) order by name");
            ResultSet set = statement1.executeQuery();
//            ResultSet set = connection.createStatement().executeQuery("select politeness, condition, name, name_of_user, fullness, date, size, x" +
//                    " from main.collection where user_id = (select id from main.users where login = '" + login + "') order by name");
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
