import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final Map<String, String> users = new HashMap<>();
    static {
        users.put("Raman", "123");
    }
    public static boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false; 
        }
        users.put(username, password);
        return true;
    }
    public static boolean validate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
    public static void updateUser(String oldName, String newName, String newPass) {
        if (users.containsKey(oldName)) {
            users.remove(oldName);
            users.put(newName, newPass);
        }
    }
}