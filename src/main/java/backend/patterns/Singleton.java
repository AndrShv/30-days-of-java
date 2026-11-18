package backend.patterns;

import lombok.Getter;

public class Singleton {
    static DBConnection fst_connection = DBConnection.getInstance("root1", "password1");
    static DBConnection scd_connection = DBConnection.getInstance("root2", "password2");
    public static void main(String[] args){
        System.out.println(fst_connection == scd_connection);
    }
}


@Getter
class DBConnection {
    private final String root;
    private final String password;
    private static volatile DBConnection instance;

    private DBConnection(String root, String password) {
        this.root = root;
        this.password = password;
    }

    public static DBConnection getInstance(String root, String password) {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection(root, password);
                }
            }
        }
        return instance;
    }
}
