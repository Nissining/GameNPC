package nissining.gamenpc.gameUtils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameAPI {

    public static boolean quickJoin(Player player, String pluginName) {
        Plugin plugin = Server.getInstance().getPluginManager().getPlugin(pluginName);
        if (plugin == null)
            return false;

        Method[] methods = plugin.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("quickJoin")) {
                try {
                    method.invoke(plugin, player, "");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static List<String> getGameData(String pluginName) {
        Plugin plugin = Server.getInstance().getPluginManager().getPlugin(pluginName);
        if (plugin == null)
            return new ArrayList<>();

        ArrayList<String> data = new ArrayList<>();
        Method[] methods = plugin.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("getGameName")) {
                try {
                    data.add((String) method.invoke(plugin));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            if (method.getName().equals("getAllGCount")) {
                try {
                    data.add((String) method.invoke(plugin));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }


}
