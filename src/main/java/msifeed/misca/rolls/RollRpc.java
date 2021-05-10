package msifeed.misca.rolls;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.CharstateSync;
import msifeed.misca.charstate.cap.ICharstate;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class RollRpc {
    private static final String effortRoll = "roll.effort";
    private static final String effortDice = "roll.effortDice";

    @RpcMethodHandler(effortRoll)
    public void onEffortRoll(RpcContext ctx, int effortOrd, int amount, int difficulty) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;

        final CharEffort effort = CharEffort.values()[effortOrd];
        final ICharstate state = CharstateProvider.get(sender);

        final float available = state.efforts().get(effort);
        if (amount > available) {
            sender.sendStatusMessage(new TextComponentString("Not enough efforts in pool!"), true);
            return;
        }

        state.efforts().set(effort, available - amount);
        CharstateSync.sync(sender);

        final double chance = amount / (double) difficulty;
        final boolean success = Dices.check(chance);
        ChatexRpc.broadcastEffortRoll(sender, effort, amount, difficulty, success);
    }

    @RpcMethodHandler(effortDice)
    public void onEffortDice(RpcContext ctx, int effortOrd) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;

        final CharEffort effort = CharEffort.values()[effortOrd];
        final ICharsheet sheet = CharsheetProvider.get(sender);

        final int pool = sheet.effortPools().get(effort);
        final String spec = String.format("%s %d", effort.tr(), pool);
        final int result = new Random().nextInt(20) + 1 + pool;

        ChatexRpc.broadcastDiceRoll(sender, spec, result);
    }

    @SideOnly(Side.CLIENT)
    public static void doEffortRoll(CharEffort effort, int amount, int difficulty) {
        Misca.RPC.sendToServer(effortRoll, effort.ordinal(), amount, difficulty);
    }

    @SideOnly(Side.CLIENT)
    public static void doEffortDice(CharEffort effort) {
        Misca.RPC.sendToServer(effortDice, effort.ordinal());
    }
}
