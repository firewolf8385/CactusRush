package net.jadedmc.cactusrush;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.Mode;
import net.jadedmc.cactusrush.game.teams.Team;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.cactusrush.utils.chat.ChatUtils;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.features.player.JadedPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
class Placeholders extends PlaceholderExpansion {
    private final CactusRushPlugin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public Placeholders(CactusRushPlugin plugin){
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

        // Overall
        if(identifier.contains("top_wins_name_overall")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard().keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_overall")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard().values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 1v1
        if(identifier.contains("top_wins_name_solo")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("1v1").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_solo")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("1v1").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 2v2
        if(identifier.contains("top_wins_name_doubles")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("2v2").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_doubles")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("2v2").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 3v3
        if(identifier.contains("top_wins_name_threes")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("3v3").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_threes")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("3v3").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 4v4
        if(identifier.contains("top_wins_name_fours")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("4v4").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_fours")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getWinsLeaderboard("4v4").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        if(identifier.contains("top_level_name")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.leaderboardManager().getLevelLeaderboard().keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_level_level")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.leaderboardManager().getLevelLeaderboard().values());

            if(temp.size() < place + 1) {
                return "---";
            }


            return LevelUtils.getFormattedLevel(temp.get(place));
        }

        if(identifier.equalsIgnoreCase( "game_displayname")) {
            JadedPlayer jadedPlayer = JadedAPI.getJadedPlayer(player);

            if(jadedPlayer == null) {
                return "";
            }

            Game game = plugin.gameManager().getGame(player);

            if(game == null) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + jadedPlayer.getName();
            }

            if(game.gameState() == GameState.WAITING || game.gameState() == GameState.COUNTDOWN) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + jadedPlayer.getName();
            }

            if(game.spectators().contains(player)) {
                return "&7[SPEC] " + jadedPlayer.getName();
            }

            Team team = game.teamManager().getTeam(player);

            if(game.gameState() == GameState.BETWEEN_ROUND) {
                return team.color().textColor() + jadedPlayer.getName();
            }
            else {
                return team.color().textColor() + jadedPlayer.getName() + " &8[" + plugin.abilityManager().getAbility(player).name() + "&8]";
            }
        }

        switch (identifier) {
            case "wins" -> {
                return plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().modeWins("overall") + "";
            }

            case "wins_1v1" -> {
                return plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().modeWins("1v1") + "";
            }

            case "wins_2v2" -> {
                return plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().modeWins("2v2") + "";
            }

            case "wins_3v3" -> {
                return plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().modeWins("3v3") + "";
            }

            case "wins_4v4" -> {
                return plugin.cactusPlayerManager().getPlayer(player).statisticsTracker().modeWins("4v4") + "";
            }

            case "playing_1v1" -> {
                return plugin.gameManager().playing(Mode.ONE_V_ONE) + "";
            }

            case "playing_2v2" -> {
                return plugin.gameManager().playing(Mode.TWO_V_TWO) + "";
            }

            case "playing_3v3" -> {
                return plugin.gameManager().playing(Mode.THREE_V_THREE) + "";
            }

            case "playing_4v4" -> {
                return plugin.gameManager().playing(Mode.FOUR_V_FOUR) + "";
            }

            case "level" -> {
                CactusPlayer cactusPlayer = plugin.cactusPlayerManager().getPlayer(player);
                return LevelUtils.getFormattedLevel(cactusPlayer.level());
            }

            case "coins" -> {
                CactusPlayer woolPlayer = plugin.cactusPlayerManager().getPlayer(player);
                return woolPlayer.coins() + "";
            }

            case "prefix" -> {
                Game game = plugin.gameManager().getGame(player);

                if(game == null || game.gameState() == GameState.WAITING || game.gameState() == GameState.COUNTDOWN) {
                    return PlaceholderAPI.setPlaceholders(player, "%jadedcore_rank_chat_prefix_legacy%&7");
                }

                Team team = game.teamManager().getTeam(player);

                return team.color().textColor() + "[" + team.color().abbreviation() + "] ";
            }

            case "chat_prefix" -> {
                Game game = plugin.gameManager().getGame(player);

                if(game == null) {
                    return "";
                }

                if(game.gameState() == GameState.WAITING || game.gameState() == GameState.COUNTDOWN) {
                    return "";
                }

                if(game.spectators().contains(player)) {
                    return "&7[SPEC]";
                }

                Team team = game.teamManager().getTeam(player);
                return ChatUtils.replaceLegacy(team.color().textColor() + "[" + team.color().name().toUpperCase() + "]");
            }

            case "game_team" -> {
                Game game = plugin.gameManager().getGame(player);

                if(game == null) {
                    return "";
                }

                if(game.gameState() == GameState.WAITING || game.gameState() == GameState.COUNTDOWN) {
                    return "";
                }

                Team team = game.teamManager().getTeam(player);

                if(team == null) {
                    return "zTeam";
                }

                return team.color().name();
            }
        }

        return null;
    }
}