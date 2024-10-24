package net.jadedmc.cactusrush;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jadedmc.cactusrush.game.Game;
import net.jadedmc.cactusrush.game.GameState;
import net.jadedmc.cactusrush.game.team.Team;
import net.jadedmc.cactusrush.player.CactusPlayer;
import net.jadedmc.cactusrush.utils.LevelUtils;
import net.jadedmc.jadedcore.JadedAPI;
import net.jadedmc.jadedcore.player.JadedPlayer;
import net.jadedmc.jadedutils.chat.ChatUtils;
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

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard().keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_overall")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard().values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 1v1
        if(identifier.contains("top_wins_name_solo")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("1v1").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_solo")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("1v1").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 2v2
        if(identifier.contains("top_wins_name_doubles")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("2v2").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_doubles")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("2v2").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 3v3
        if(identifier.contains("top_wins_name_threes")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("3v3").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_threes")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("3v3").values());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place) + "";
        }

        // 4v4
        if(identifier.contains("top_wins_name_fours")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<String> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("4v4").keySet());

            if(temp.size() < place + 1) {
                return "---";
            }

            return temp.get(place);
        }

        if(identifier.contains("top_wins_level_fours")) {
            int place = Integer.parseInt(identifier.replaceAll("\\D+","")) - 1;

            ArrayList<Integer> temp = new ArrayList<>(plugin.getLeaderboardManager().getWinsLeaderboard("4v4").values());

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
            JadedPlayer jadedPlayer = JadedAPI.getJadedPlayer(player);

            if(jadedPlayer == null) {
                return "";
            }

            Game game = plugin.getGameManager().getLocalGames().getGame(player);

            if(game == null) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + jadedPlayer.getName();
            }

            if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                return "%jadedcore_rank_chat_prefix_legacy%&7" + jadedPlayer.getName();
            }

            if(game.getSpectators().contains(player.getUniqueId())) {
                return "&7[SPEC] " + jadedPlayer.getName();
            }

            Team team = game.getTeamManager().getTeam(player);

            if(game.getGameState() == GameState.BETWEEN_ROUND) {
                return team.getColor().getTextColor() + jadedPlayer.getName();
            }
            else {
                return team.getColor().getTextColor() + jadedPlayer.getName() + " &8[" + plugin.getAbilityManager().getAbility(player).name() + "&8]";
            }
        }

        switch (identifier) {
            case "wins" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getModeWins("overall") + "";
            }

            case "wins_1v1" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getModeWins("1v1") + "";
            }

            case "wins_2v2" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getModeWins("2v2") + "";
            }

            case "wins_3v3" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getModeWins("3v3") + "";
            }

            case "wins_4v4" -> {
                return plugin.getCactusPlayerManager().getPlayer(player).getModeWins("4v4") + "";
            }

            case "playing_1v1" -> {
                // TODO: return plugin.getCactusPlayerManager().playing(Mode.ONE_V_ONE) + "";
                return 0 + "";
            }

            case "playing_2v2" -> {
                // TODO: return plugin.getGameManager().playing(Mode.TWO_V_TWO) + "";
                return 0 + "";
            }

            case "playing_3v3" -> {
                // TODO: return plugin.getCactusPlayerManager().playing(Mode.THREE_V_THREE) + "";
                return 0 + "";
            }

            case "playing_4v4" -> {
                // TODO: return plugin.getCactusPlayerManager().playing(Mode.FOUR_V_FOUR) + "";
                return 0 + "";
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
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null || game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                    return PlaceholderAPI.setPlaceholders(player, "%jadedcore_rank_chat_prefix_legacy%&7");
                }

                Team team = game.getTeamManager().getTeam(player);

                return team.getColor().getTextColor() + "[" + team.getColor().getAbbreviation() + "] ";
            }

            case "chat_prefix" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

                if(game == null) {
                    return "";
                }

                if(game.getGameState() == GameState.WAITING || game.getGameState() == GameState.COUNTDOWN) {
                    return "";
                }

                if(game.getSpectators().contains(player.getUniqueId())) {
                    return "&7[SPEC]";
                }

                Team team = game.getTeamManager().getTeam(player);
                return ChatUtils.replaceLegacy(team.getColor().getTextColor() + "[" + team.getColor().getTeamName().toUpperCase() + "]");
            }

            case "game_team" -> {
                Game game = plugin.getGameManager().getLocalGames().getGame(player);

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

                return team.getColor().getTeamName();
            }

            case "current_experience" -> {
                final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
                return LevelUtils.getFormattedExperience(cactusPlayer.getExperience());
            }

            case "required_experience" -> {
                final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
                return LevelUtils.getFormattedRequiredExperience(cactusPlayer.getLevel());
            }

            case "progress" -> {
                final CactusPlayer cactusPlayer = plugin.getCactusPlayerManager().getPlayer(player);
                return LevelUtils.getSmallLevelBar(cactusPlayer.getExperience(), cactusPlayer.getLevel());
            }
        }

        return null;
    }
}