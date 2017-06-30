package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.combat.FighterManager;
import ru.ariadna.misca.crabs.lobby.Lobby;
import ru.ariadna.misca.crabs.lobby.LobbyActionMessage;
import ru.ariadna.misca.crabs.lobby.LobbyUpdateMessage;

import java.util.LinkedList;

public class LobbyGuiScreen extends GuiScreen {
    private static final ResourceLocation bg_texture = new ResourceLocation("misca", "textures/gui/crabs/lobby.png");
    private static final int bg_width = 256;
    private static final int bg_height = 160;

    private int topLeftX;
    private int topLeftY;

    private GuiButtonExt createLobbyBtn;
    private GuiButtonExt joinLobbyBtn;
    private GuiButtonExt leaveLobbyBtn;
    private GuiTextField playerToJoinText;

    private Lobby lobby;

    public LobbyGuiScreen() {
//        MinecraftForge.EVENT_BUS.register(this);
        Crabs.instance.network.sendToServer(new LobbyActionMessage(LobbyActionMessage.Type.NOOP));
    }

    @Override
    public void initGui() {
        createLobbyBtn = new GuiButtonExt(0, 0, 0, 100, 20, MiscaUtils.localize("misca.lobby.create_lobby"));
        playerToJoinText = new GuiTextField(this.fontRendererObj, 0, 20, 100, 20);
        joinLobbyBtn = new GuiButtonExt(1, 0, 40, 100, 20, "join");
        leaveLobbyBtn = new GuiButtonExt(2, 0, 60, 100, 20, "leave");

        buttonList.add(createLobbyBtn);
        buttonList.add(joinLobbyBtn);
        buttonList.add(leaveLobbyBtn);
    }

    @Override
    public void updateScreen() {
        playerToJoinText.updateCursorCounter();
    }

    private void drawLobby() {
        StringBuilder sb = new StringBuilder();
        sb.append("Master: ");
        sb.append(lobby.master().getDisplayName());
        for (Fighter f : lobby.members()) {
            sb.append("\n- ");
            // TODO Use char data
//            sb.append(f.character().name);
            EntityLivingBase e = f.entity();
            if (e instanceof EntityPlayer) {
                sb.append(((EntityPlayer) e).getDisplayName());
                if (e == lobby.master()) {
                    sb.append(" (M)");
                }
            } else {
                sb.append(e.getCommandSenderName());
                sb.append(" (E)");
            }
        }
        this.fontRendererObj.drawSplitString(sb.toString(), 120, 0, 200, 0xFFFFFF);
    }

    private void drawBackground(int mouseX, int mouseY, float tick) {
//        this.mc.getTextureManager().bindTexture(bg_texture);
//        GuiUtils.drawContinuousTexturedBox(0, 0, 0, 0, bg_width, bg_height, bg_width, bg_height, 0, this.zLevel);
//
//        this.fontRendererObj.drawSplitString("Very long string to test out this feature of font renderer in minecraft.",
//                topLeftX + 129, topLeftY + 3, 115, 0xFFFFFF);

        if (lobby != null)
            drawLobby();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        topLeftX = (this.width - bg_width) / 2;
        topLeftY = (this.height - bg_height) / 2;

        drawBackground(mouseX, mouseY, tick);
        super.drawScreen(mouseX, mouseY, tick);
        playerToJoinText.drawTextBox();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        playerToJoinText.mouseClicked(x, y, button);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        this.playerToJoinText.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: // Create Lobby
                LobbyActionMessage create_msg = new LobbyActionMessage(LobbyActionMessage.Type.CREATE);
                Crabs.instance.network.sendToServer(create_msg);
                break;
            case 1: // Join lobby
                LobbyActionMessage join_msg = new LobbyActionMessage(LobbyActionMessage.Type.JOIN);
                join_msg.name = playerToJoinText.getText();
                Crabs.instance.network.sendToServer(join_msg);
                break;
            case 2: // Leave lobby
                LobbyActionMessage leave_msg = new LobbyActionMessage(LobbyActionMessage.Type.LEAVE);
                Crabs.instance.network.sendToServer(leave_msg);
                break;
            case 3: // Include in lobby
                LobbyActionMessage include_msg = new LobbyActionMessage(LobbyActionMessage.Type.INCLUDE);
                Crabs.instance.network.sendToServer(include_msg);
                break;
            case 4: // Exclude from lobby
                LobbyActionMessage exclude_msg = new LobbyActionMessage(LobbyActionMessage.Type.EXCLUDE);
                Crabs.instance.network.sendToServer(exclude_msg);
                break;
        }
    }

    public void onLobbyUpdate(LobbyUpdateMessage message) {
        WorldClient worldClient = Minecraft.getMinecraft().theWorld;

        if (message.fighters.isEmpty()) {
            lobby = null;
            return;
        }

        Fighter master = FighterManager.makeFighterClient(message.masterEntityId);
        LinkedList<Fighter> members = message.fighters;
        for (Fighter f : members) {
            Entity e = worldClient.getEntityByID(f.entityId());
            if (e != null && e instanceof EntityLivingBase)
                f.setEntity((EntityLivingBase) e);
        }

        if (lobby == null)
            lobby = new Lobby(master);
        else if (master != null)
            lobby.setMaster((EntityPlayer) master.entity());
        lobby.setMembers(members);
    }
}
