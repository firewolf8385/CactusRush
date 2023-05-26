package com.github.firewolf8385.cactusrush;

import com.github.firewolf8385.cactusrush.game.Game;
import com.github.firewolf8385.cactusrush.game.GameState;
import com.github.firewolf8385.cactusrush.game.team.Team;
import com.github.firewolf8385.cactusrush.player.CactusPlayer;
import com.github.firewolf8385.cactusrush.utils.LevelUtils;
import com.github.firewolf8385.cactusrush.utils.chat.ChatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
class Placeholders extends PlaceholderExpansion {
    private final CactusRush plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public Placeholders(CactusRush plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "cr";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if(identifier.contains("top_wins_name")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard().keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard().values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        if(identifier.contains("top_level_name")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getLevelLeaderboard().keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_level_level")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getLevelLeaderboard().values());

            if(temp.size() < place + 1) {
                return "---";
            }


            return LevelUtils.getFormattedLevel(temp.get(place));
        }

        if(identifier.equalsIgnoreCase( "game_displayname")) {

            Game game = plugin.getGameManager().getGame(player);

            if(game == null) {
                return "%luckperms_prefix%&7" + player.getName();
            }

            if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                return "%luckperms_prefix%&7" + player.getName();
            }

            if(game.getSpectators().contains(player)) {
                return "<gray>[SPEC] " + player.getName();
            }

            Team team = game.getTeamManager().getTeam(player);
            return team.getColor().getChatColor() + player.getName() + " &8[" + plugin.getAbilityManager().getAbility(player).getName() + "&8]";
        }

        switch (identifier) {
            case "wins" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getWins() + "";
            }

            case "playing_1v1" -> {
                return plugin.getGameManager().getPlaying(2, 1) + "";
            }

            case "playing_2v2" -> {
                return plugin.getGameManager().getPlaying(2, 2) + "";
            }

            case "playing_3v3" -> {
                return plugin.getGameManager().getPlaying(2, 3) + "";
            }

            case "playing_4v4" -> {
                return plugin.getGameManager().getPlaying(2, 4) + "";
            }

            case "level" -> {
                CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
                return LevelUtils.getFormattedLevel(cactusPlayer.getLevel());
            }

            case "coins" -> {
                CactusPlayer woolPlayer = plugin.getCactusPlayerManager().getPlayer(player);
                return woolPlayer.getCoins() + "";
            }

            case "prefix" -> {
                Game game = plugin.getGameManager().getGame(player);

                if(game == null || game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                    return PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%&7");
                }

                Team team = game.getTeamManager().getTeam(player);

                return team.getColor().getChatColor() + "[" + team.getColor().getAbbreviation() + "] ";
            }

            case "chat_prefix" -> {
                Game game = plugin.getGameManager().getGame(player);

                if(game == null) {
                    return "";
                }

                if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                    return "";
                }

                if(game.getSpectators().contains(player)) {
                    return "&7[SPEC]";
                }

                Team team = game.getTeamManager().getTeam(player);
                return ChatUtils.replaceChatColor(team.getColor().getChatColor()) + "[" + team.getColor().getName().toUpperCase() + "]";
            }

            case "game_team" -> {
                Game game = plugin.getGameManager().getGame(player);

                if(game == null) {
                    return "";
                }

                if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                    return "";
                }

                Team team = game.getTeamManager().getTeam(player);

                if(team == null) {
                    return "zTeam";
                }

                return team.getColor().getName();
            }
        }

        return null;
    }
}