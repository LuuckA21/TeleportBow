var Runnable = Java.type('org.bukkit.scheduler.BukkitRunnable');
var Particle = Java.type('org.bukkit.Particle');

var radius = 0.5;
var height = 0;
var angle = 0;

var task = new (Java.extend(Runnable, {
    run: function () {
        for (var i = 0; i < 3; i++) {
            var strandAngle = angle + i * Math.PI * 2 / 3;
            var x = radius * Math.cos(strandAngle);
            var z = radius * Math.sin(strandAngle);
            var loc = location.clone().add(x, height, z);
            location.getWorld().spawnParticle(Particle.CLOUD, loc, 0, 0, 0, 0, 0);
        }

        angle += Math.PI / 8;
        height += 0.05;
        radius += 0.01;

        if (height > 5) {
            task.cancel();
        }
    }
}))();

task.runTaskTimerAsynchronously(plugin, 0, 2);
