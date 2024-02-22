import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomAIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // 创建一个僵尸实体作为 AI 玩家
        Zombie zombie = Bukkit.getWorlds().get(0).spawn(new Location(Bukkit.getWorlds().get(0), 0, 64, 0), Zombie.class);

        // 设置血量为200
        zombie.setHealth(200);

        // 设置装备
        ItemStack helmet = new ItemStack(Material.SKULL_ITEM, 1, (short) 1); // 凋零骷髅头颅
        zombie.getEquipment().setHelmet(helmet);

        // 设置剑
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5); // 锐利
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2); // 火焰附加
        zombie.getEquipment().setItemInMainHand(sword);

        // 设置弓
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3); // 力量
        bow.addEnchantment(Enchantment.ARROW_FIRE, 1); // 火矢
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1); // 无限
        zombie.getEquipment().setItemInOffHand(bow);

        // 设置挖掘工具
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5); // 挖掘效率
        pickaxe.addEnchantment(Enchantment.DURABILITY, 3); // 耐久
        zombie.getEquipment().setItemInMainHand(pickaxe);

        // 设置盾牌
        ItemStack shield = new ItemStack(Material.SHIELD);
        zombie.getEquipment().setItemInOffHand(shield);

        // 添加追踪玩家逻辑
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Player targetPlayer = Bukkit.getServer().getOnlinePlayers().stream().findAny().orElse(null);
            if (targetPlayer != null) {
                zombie.setTarget(targetPlayer);
            }
        }, 0L, 20L); // 每秒执行一次

        getLogger().info("Custom AI Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Custom AI Plugin has been disabled!");
    }
}
