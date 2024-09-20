package moe.luoluo.hypixel;

import com.google.gson.JsonObject;
import moe.luoluo.Api;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Objects;

public class WoolGames {
    public static void wool(CommandSender context, String player, String type) throws IOException, URISyntaxException {
        MessageChainBuilder chain = new MessageChainBuilder();
        JsonObject woolJson;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        type = type.toLowerCase();

        JsonObject json;
        String uuid = Api.mojang(player, "uuid");
        if (Objects.equals(uuid, "NotFound")) {
            context.sendMessage("玩家不存在");
            return;
        } else json = Api.hypixel("player", uuid);

        if (json.get("player").isJsonObject()) {
            if (json.get("player").getAsJsonObject().has("stats") && json.get("player").getAsJsonObject().get("stats").getAsJsonObject().has("WoolGames")) {
                woolJson = json.get("player").getAsJsonObject().get("stats").getAsJsonObject().get("WoolGames").getAsJsonObject();

                chain.append(new PlainText(Rank.rank(json.get("player").getAsJsonObject()) + " "));
                chain.append(new PlainText(json.get("player").getAsJsonObject().get("displayname").getAsString()));
                chain.append(new PlainText(" | 羊毛游戏数据:"));

                chain.append(new PlainText("\n 等级: "));
                if (woolJson.has("progression")) {
                    long exp = woolJson.get("progression").getAsJsonObject().get("experience").getAsLong();
                    int[] expNeeded = {1000, 2000, 3000, 4000, 5000};
                    int level = 1;
                    for (int j : expNeeded) {
                        if (exp >= j) {
                            exp -= j;
                            level++;
                        } else break;
                    }
                    while (exp >= 5000) {
                        level++;
                        exp -= 5000;
                    }
                    chain.append(new PlainText(String.valueOf(level)));
                    //经验进度
                    int xpLevel = Math.min(level, 4);
                    chain.append(new PlainText(" (" + exp +
                            "/" + exp(xpLevel) + " " +
                            decimalFormat.format((float) exp / expNeeded[xpLevel] * 100) + "%)"
                    ));
                } else chain.append("null");

                chain.append(new PlainText("\n 羊毛: "));
                if (woolJson.has("coins")) {
                    chain.append(new PlainText(String.valueOf(woolJson.get("coins").getAsInt())));
                } else chain.append(new PlainText("null"));
                if (woolJson.has("progression") && woolJson.get("progression").getAsJsonObject().has("available_layers")) {
                    chain.append(new PlainText(" | 羊毛层数: "));
                    chain.append(new PlainText(String.valueOf(woolJson.get("progression").getAsJsonObject().get("available_layers").getAsInt())));
                }
                if (woolJson.has("playtime")) {
                    chain.append("\n 游玩时间: ");
                    int t = woolJson.get("playtime").getAsInt();
                    if (t >= 86400) {
                        chain.append(new PlainText(t / 86400 + "天" +
                                String.format("%02d", t % 86400 / 3600) + ":" +
                                String.format("%02d", t % 3600 / 60) + ":" +
                                String.format("%02d", t % 60)));
                    } else {
                        chain.append(new PlainText(String.format("%02d", t % 86400 / 3600) + ":" +
                                String.format("%02d", t % 3600 / 60) + ":" +
                                String.format("%02d", t % 60)));
                    }
                }

                JsonObject ctwstats = new JsonObject();
                JsonObject sheepstats = new JsonObject();
                JsonObject woolstats = new JsonObject();
                boolean ctw = woolJson.has("capture_the_wool") && woolJson.get("capture_the_wool").getAsJsonObject().has("stats");
                boolean sheep = woolJson.has("sheep_wars") && woolJson.get("sheep_wars").getAsJsonObject().has("stats");
                boolean wool = woolJson.has("wool_wars") && woolJson.get("wool_wars").getAsJsonObject().has("stats");
                if (ctw) ctwstats = woolJson.get("capture_the_wool").getAsJsonObject().get("stats").getAsJsonObject();
                if (sheep) sheepstats = woolJson.get("sheep_wars").getAsJsonObject().get("stats").getAsJsonObject();
                if (wool) woolstats = woolJson.get("wool_wars").getAsJsonObject().get("stats").getAsJsonObject();


                if (type.isEmpty() || type.equals("all")) {
                    chain.append("\n|- 总体数据: ");
                    int wins = 0;
                    int losses = 0;
                    int kills = 0;
                    int deaths = 0;
                    int assists = 0;
                    if (ctw) {
                        if (ctwstats.has("participated_wins")) wins += ctwstats.get("participated_wins").getAsInt();
                        if (ctwstats.has("participated_losses"))
                            losses += ctwstats.get("participated_losses").getAsInt();
                        if (ctwstats.has("kills")) kills += ctwstats.get("kills").getAsInt();
                        if (ctwstats.has("deaths")) deaths += ctwstats.get("deaths").getAsInt();
                        if (ctwstats.has("assists")) assists += ctwstats.get("assists").getAsInt();
                    }
                    if (sheep) {
                        if (sheepstats.has("wins")) wins += sheepstats.get("wins").getAsInt();
                        if (sheepstats.has("losses")) losses += sheepstats.get("losses").getAsInt();
                        if (sheepstats.has("kills")) kills += sheepstats.get("kills").getAsInt();
                        if (sheepstats.has("deaths")) deaths += sheepstats.get("deaths").getAsInt();
                    }
                    if (wool) {
                        if (woolstats.has("wins")) wins += woolstats.get("wins").getAsInt();
                        if (woolstats.has("games_played"))
                            if (woolstats.has("wins"))
                                losses += woolstats.get("games_played").getAsInt() - woolstats.get("wins").getAsInt();
                            else losses += woolstats.get("games_played").getAsInt();
                        if (woolstats.has("kills")) kills += woolstats.get("kills").getAsInt();
                        if (woolstats.has("deaths")) deaths += woolstats.get("deaths").getAsInt();
                        if (woolstats.has("assists")) assists += woolstats.get("assists").getAsInt();
                    }
                    chain.append(new PlainText("\n| 胜场: "));
                    chain.append(new PlainText(String.valueOf(wins)));
                    chain.append(new PlainText(" | 败场: "));
                    chain.append(new PlainText(String.valueOf(losses)));
                    if (wins > 0) {
                        chain.append(" | WLR: ");
                        if (losses > 0) chain.append(new PlainText(decimalFormat.format((float) wins / losses)));
                        else chain.append(new PlainText(decimalFormat.format(wins)));
                    }
                    chain.append("\n| 击杀: ");
                    chain.append(new PlainText(String.valueOf(kills)));
                    chain.append(" | 死亡: ");
                    chain.append(new PlainText(String.valueOf(deaths)));
                    if (kills > 0) {
                        chain.append(" | KDR: ");
                        if (deaths > 0) chain.append(new PlainText(decimalFormat.format((float) kills / deaths)));
                        else chain.append(new PlainText(decimalFormat.format(kills)));
                    }
                    chain.append("\n| 助攻: ");
                    chain.append(new PlainText(String.valueOf(assists)));
                    if (assists > 0) {
                        chain.append(" | (K+A)/D: ");
                        if (deaths > 0)
                            chain.append(new PlainText(decimalFormat.format((float) (kills + assists) / deaths)));
                        else chain.append(new PlainText(decimalFormat.format(kills + assists)));
                    }
                }
                if (type.equals("ctw") || type.equals("all")) {
                    chain.append("\n|- 捕捉羊毛大作战: ");
                    if (ctwstats.has("wools_stolen")) {
                        chain.append("\n| 捡起羊毛:");
                        chain.append(new PlainText(String.valueOf(ctwstats.get("wools_stolen").getAsInt())));
                        if (ctwstats.has("wools_captured")) {
                            chain.append(" | 捕获羊毛: ");
                            chain.append(new PlainText(String.valueOf(ctwstats.get("wools_captured").getAsInt())));
                        }
                    }
                    chain.append(new PlainText("\n| 胜场: "));
                    if (ctwstats.has("participated_wins"))
                        chain.append(new PlainText(String.valueOf(ctwstats.get("participated_wins").getAsInt())));
                    else chain.append("null");
                    chain.append(new PlainText(" | 败场: "));
                    if (ctwstats.has("participated_losses"))
                        chain.append(new PlainText(String.valueOf(ctwstats.get("participated_losses").getAsInt())));
                    else chain.append("null");
                    if (ctwstats.has("participated_wins")) {
                        chain.append(" | WLR: ");
                        if (ctwstats.has("participated_losses"))
                            chain.append(new PlainText(decimalFormat.format((float) ctwstats.get("participated_wins").getAsInt() / ctwstats.get("participated_losses").getAsInt())));
                        else
                            chain.append(new PlainText(decimalFormat.format(ctwstats.get("participated_wins").getAsInt())));
                    }
                    chain.append("\n| 击杀: ");
                    if (ctwstats.has("kills"))
                        chain.append(new PlainText(String.valueOf(ctwstats.get("kills").getAsInt())));
                    else chain.append("null");
                    chain.append(" | 死亡: ");
                    if (ctwstats.has("deaths"))
                        chain.append(new PlainText(String.valueOf(ctwstats.get("deaths").getAsInt())));
                    else chain.append("null");
                    if (ctwstats.has("kills")) {
                        chain.append(" | KDR: ");
                        if (ctwstats.has("deaths"))
                            chain.append(new PlainText(decimalFormat.format((float) ctwstats.get("kills").getAsInt() / ctwstats.get("deaths").getAsInt())));
                        else chain.append(new PlainText(decimalFormat.format(ctwstats.get("kills").getAsInt())));
                    }
                    chain.append("\n| 助攻: ");
                    if (ctwstats.has("assists")) {
                        chain.append(new PlainText(String.valueOf(ctwstats.get("assists").getAsInt())));
                        chain.append(" | (K+A)/D: ");
                        if (ctwstats.has("kills")) {
                            if (ctwstats.has("deaths"))
                                chain.append(new PlainText(decimalFormat.format((float) (ctwstats.get("kills").getAsInt() + ctwstats.get("assists").getAsInt()) / ctwstats.get("deaths").getAsInt())));
                            else
                                chain.append(new PlainText(decimalFormat.format(ctwstats.get("kills").getAsInt() + ctwstats.get("assists").getAsInt())));
                        } else {
                            if (ctwstats.has("deaths"))
                                chain.append(new PlainText(decimalFormat.format((float) ctwstats.get("assists").getAsInt() / ctwstats.get("deaths").getAsInt())));
                            else chain.append(new PlainText(decimalFormat.format(ctwstats.get("assists").getAsInt())));
                        }
                    } else chain.append("null");
                    if (ctwstats.has("kills_with_wool") || ctwstats.has("deaths_with_wool")) {
                        chain.append("\n| 持有羊毛时");
                        if (ctwstats.has("kills_with_wool")) {
                            chain.append(" 击杀: ");
                            chain.append(new PlainText(String.valueOf(ctwstats.get("kills_with_wool").getAsInt())));
                        }
                        if (ctwstats.has("deaths_with_wool")) {
                            chain.append(" 死亡: ");
                            chain.append(new PlainText(String.valueOf(ctwstats.get("deaths_with_wool").getAsInt())));
                        }
                    }
                    if (ctwstats.has("kills_on_woolholder")) {
                        chain.append("\n| 击杀持有羊毛的玩家: ");
                        chain.append(new PlainText(String.valueOf(ctwstats.get("kills_on_woolholder").getAsInt())));
                    }
                    if (ctwstats.has("fastest_wool_capture")) {
                        chain.append("\n| 最快捕获羊毛: ");
                        int t = ctwstats.get("fastest_wool_capture").getAsInt();
                        chain.append(new PlainText(String.format("%02d", t / 60) + ":" + String.format("%02d", t % 60)));
                    }
                    if (ctwstats.has("fastest_win")) {
                        chain.append("\n| 最快胜利: ");
                        int t = ctwstats.get("fastest_win").getAsInt();
                        chain.append(new PlainText(String.format("%02d", t / 60) + ":" + String.format("%02d", t % 60)));
                    }
                    if (ctwstats.has("longest_game")) {
                        chain.append("\n| 单局最长: ");
                        int t = ctwstats.get("longest_game").getAsInt();
                        chain.append(new PlainText(String.format("%02d", t / 60) + ":" + String.format("%02d", t % 60)));
                    }
                }
                if (type.equals("sheep") || type.equals("all")) {

                }
            } else {
                chain.append(new PlainText("该玩家的羊毛游戏数据为空"));
            }
            context.sendMessage(chain.build());
        }
    }

    public static String exp(int exp) {
        String[] map = {"1k", "2k", "3k", "4k", "5k"};
        return map[exp];
    }
}