import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ZombieHunterPlugin extends JavaPlugin implements Listener {

    private Random random = new Random();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("插件已启动！");
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        LivingEntity entity = event.getEntity();

        // 处理僵尸生成时的逻辑
        if (entity.getType() == EntityType.ZOMBIE && isNight(entity.getLocation())) {
            int chance = random.nextInt(100);
            if (chance < 30) {
                spawnZombieHunter(entity.getLocation());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // 处理击败僵尸时的逻辑
        if (entity.getType() == EntityType.ZOMBIE && entity.getCustomName() != null && entity.getCustomName().equals(ChatColor.RED + "Zombie Hunter")) {
            // event.getDrops().clear();  移除掉落物品
            int chance = random.nextInt(100);
            if (chance < 3) {
                spawnZombie(entity.getLocation());
            }
        }
    }

    private boolean isNight(Location location) {
        long time = location.getWorld().getTime();
        return time > 13000 && time < 23000;
    }

    private void spawnZombieHunter(Location location) {
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        zombie.setCustomName(ChatColor.RED + "Zombie Hunter");
        zombie.setCustomNameVisible(true);
        zombie.setMaxHealth(100);
        zombie.setHealth(100);
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        Bukkit.getScheduler().scheduleWithFixedDelay(() -> {
            if (zombie.isDead()) return;
            if (zombie.getHealth() < 10) {
                teleportAndAttackPlayer(zombie, 5, 1);
            } else if (zombie.getHealth() < 20) {
                spawnSwordZombies(zombie.getLocation(), 3);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void spawnSwordZombies(Location location, int count) {
        for (int i = 0; i < count; i++) {
            Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }
    }

    private void teleportAndAttackPlayer(Zombie zombie, int times, int delaySeconds) {
        if (times <= 0) return;

        Player player = findNearestPlayer(zombie.getLocation());
        if (player != null) {
            double x = player.getLocation().getX() + random.nextDouble() * 2 - 1;
            double z = player.getLocation().getZ() + random.nextDouble() * 2 - 1;
            Location location = new Location(player.getWorld(), x, player.getLocation().getY(), z);
            zombie.teleport(location);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (player.isDead()) return;
                zombie.setTarget(player);
                player.damage(2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 60, 0));
                teleportAndAttackPlayer(zombie, times - 1, delaySeconds);
            }, delaySeconds * 20L);
        }
    }

    private Player findNearestPlayer(Location location) {
        double minDistanceSquared = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(location);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                nearestPlayer = player;
            }
        }

        return nearestPlayer;
    }

    private void spawnZombie(Location location) {
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    }
}
