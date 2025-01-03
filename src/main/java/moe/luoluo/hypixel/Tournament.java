package moe.luoluo.hypixel;

import com.google.gson.JsonObject;
import moe.luoluo.Api;
import moe.luoluo.ApiResult;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Tournament {
    public static void tourney(CommandSender context, String player, String type) throws IOException, URISyntaxException {
        MessageChainBuilder chain = new MessageChainBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        ApiResult result;
        JsonObject json;
        String uuid = Api.mojang(player, "uuid");
        if (Objects.equals(uuid, "NotFound")) {
            context.sendMessage("玩家不存在");
            return;
        } else {
            result = Api.hypixel("player", uuid);
            json = result.getJson();
        }

        if (result.getTime() != -1) {
            Instant instant = Instant.ofEpochMilli(result.getTime());
            LocalDateTime localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            chain.append("\uD83D\uDFE5").append(localDate.toString()).append("\n");
        }

        if (!(json.get("player").isJsonObject() && json.get("player").getAsJsonObject().has("tourney"))) {
            chain.append(new PlainText("该玩家的锦标赛数据为空"));
            context.sendMessage(chain.build());
            return;
        }
        JsonObject tourneyJson = json.get("player").getAsJsonObject().get("tourney").getAsJsonObject();
        chain.append(new PlainText(Rank.rank(json.get("player").getAsJsonObject()) + " ")); //玩家名称前缀
        chain.append(new PlainText(json.get("player").getAsJsonObject().get("displayname").getAsString()));
        chain.append(new PlainText(" | 锦标赛数据: "));
        if (tourneyJson.has("first_join_lobby")) {
            chain.append(new PlainText("\n首次进入大厅: "));
            chain.append(new PlainText(simpleDateFormat.format(new Date(tourneyJson.get("first_join_lobby").getAsLong()))));
        }
        if (tourneyJson.has("total_tributes")) {
            chain.append(new PlainText("\n战魂: "));
            chain.append(new PlainText(String.valueOf(tourneyJson.get("total_tributes").getAsInt())));
        }
        if (tourneyJson.has("sw_normal_doubles_0")) {
            chain.append(new PlainText("\n\n24年6月空岛战争: "));
            JsonObject current = tourneyJson.get("sw_normal_doubles_0").getAsJsonObject();
            if (current.has("games_played")) {
                chain.append(new PlainText("\n游玩场次: "));
                chain.append(new PlainText(String.valueOf(current.get("games_played").getAsInt())));
            }
            if (current.has("playtime")) {
                chain.append(new PlainText(" | 游玩时间: "));
                chain.append(new PlainText(current.get("playtime").getAsInt() / 60 + "h" + current.get("playtime").getAsInt() % 60 + "m"));
            }
            if (current.has("tributes_earned")) {
                chain.append(new PlainText(" | 获得战魂: "));
                chain.append(new PlainText(String.valueOf(current.get("tributes_earned").getAsInt())));
            }
            if (current.has("claimed_ranking_reward")) {
                chain.append(new PlainText("\n奖励领取时间: "));
                chain.append(new PlainText(simpleDateFormat.format(new Date(tourneyJson.get("claimed_ranking_reward").getAsLong()))));
            }
        }
        context.sendMessage(chain.build());
    }
}
