var Runnable = Java.type('org.bukkit.scheduler.BukkitRunnable');
// var Particle = Java.type('org.bukkit.Particle');

var height = 0;
var maxHeight = 5;
var angleStep = Math.PI / 16;

var task = new (Java.extend(Runnable, {
    run: function () {
        if (height > maxHeight) {
            task.cancel();
            return;
        }

        var radius = height * 0.5;

        for (var angle = 0; angle < 2 * Math.PI; angle += angleStep) {
            var x = radius * Math.cos(angle);
            var z = radius * Math.sin(angle);
            var loc = location.clone().add(x, height, z);
            location.getWorld().spawnParticle(particle, loc, 0, 0, 0, 0, 0);
        }

        height += 0.1;
    }
}))();

task.runTaskTimerAsynchronously(plugin, 0, 2);
