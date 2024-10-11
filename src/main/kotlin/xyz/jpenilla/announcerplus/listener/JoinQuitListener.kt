package xyz.jpenilla.announcerplus.listener

import net.trueog.utilitiesog.UtilitiesOG
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.jpenilla.announcerplus.config.ConfigManager

class JoinQuitListener : Listener, KoinComponent {
    private val configManager: ConfigManager by inject()

    @Suppress("DEPRECATION")  // Suppressing deprecated joinMessage and quitMessage fields
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onJoin(event: PlayerJoinEvent) {
        if (configManager.mainConfig.joinFeatures) {
            // Suppress the default join message
            event.joinMessage = null

            // Handle first join configuration if enabled
            if (configManager.mainConfig.firstJoinConfigEnabled && !event.player.hasPlayedBefore()) {
                configManager.firstJoinConfig.onJoin(event.player)
                return
            }

            // Fetch the join configuration from join-quit-configs
            val player = event.player
            val joinConfig = configManager.joinQuitConfigs["default"] // Replace "default" with actual config name
            if (joinConfig != null) {
                // Send join message to the joining player
                joinConfig.join.messages.forEach { message ->
                    val expandedMessage = UtilitiesOG.trueogExpand(message, player)
                    UtilitiesOG.trueogMessage(player, expandedMessage.content())
                }

                // Broadcast join message to all players except the joining player
                joinConfig.join.broadcasts.forEach { broadcastMessage ->
                    val expandedBroadcastMessage = UtilitiesOG.trueogExpand(broadcastMessage, player)
                    // Broadcast to all players except the joining player
                    event.joinMessage = expandedBroadcastMessage.content()  // Still setting joinMessage to null
                }
            }

            // Apply all other join configurations (e.g., sounds or commands)
            joinConfig?.onJoin(player)
        }
    }

    @Suppress("DEPRECATION")  // Suppressing deprecated quitMessage field
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(event: PlayerQuitEvent) {
        if (configManager.mainConfig.quitFeatures) {
            // Suppress the default quit message
            event.quitMessage = null

            // Fetch the quit configuration from join-quit-configs
            val player = event.player
            configManager.joinQuitConfigs["default"]?.let { quitConfig ->
                // Broadcast quit message to all players except the quitting player
                quitConfig.quit.broadcasts.forEach { broadcastMessage ->
                    val expandedBroadcastMessage = UtilitiesOG.trueogExpand(broadcastMessage, player)
                    // Set quitMessage to the expanded broadcast message
                    event.quitMessage = expandedBroadcastMessage.content()
                }
            }

            // Apply all quit configurations
            for (config in configManager.joinQuitConfigs.values) {
                config.onQuit(player)
            }

        }
    }
}