package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.lobby.Lobby;
import ru.ariadna.misca.crabs.lobby.LobbyActionMessage;

public class GuiScreenLobby extends GuiScreen {
    public static final GuiScreenLobby instance = new GuiScreenLobby();

    private static final ResourceLocation bg_texture = new ResourceLocation("misca", "textures/gui/crabs/lobby.png");
    private static final int bg_width = 256;
    private static final int bg_height = 160;
    private Lobby lobby;
    private int topLeftX;
    private int topLeftY;
    private GuiButtonExt createLobbyBtn;
    private GuiButtonExt joinLobbyBtn;
    private GuiButtonExt leaveLobbyBtn;
    private GuiButtonExt startFightBtn;
    private GuiTextField playerToJoinText;

    @Override
    public void initGui() {
        createLobbyBtn = new GuiButtonExt(0, 0, 0, 100, 20, MiscaUtils.localize("misca.lobby.gui.create_lobby"));
        playerToJoinText = new GuiTextField(this.fontRendererObj, 0, 20, 100, 20);
        joinLobbyBtn = new GuiButtonExt(1, 0, 40, 100, 20, "join");
        leaveLobbyBtn = new GuiButtonExt(2, 0, 60, 100, 20, "leave");
        startFightBtn = new GuiButtonExt(3, 0, 80, 100, 20, "fight");

        onLobbyUpdate(lobby);

        buttonList.add(createLobbyBtn);
        buttonList.add(joinLobbyBtn);
        buttonList.add(leaveLobbyBtn);
        buttonList.add(startFightBtn);
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
        playerToJoinText.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: // Create Lobby
                LobbyActionMessage create_msg = new LobbyActionMessage(LobbyActionMessage.Type.CREATE);
                Misca.crabs.network.sendToServer(create_msg);
                break;
            case 1: // Join lobby
                LobbyActionMessage join_msg = new LobbyActionMessage(LobbyActionMessage.Type.JOIN);
                join_msg.name = playerToJoinText.getText();
                Misca.crabs.network.sendToServer(join_msg);
                break;
            case 2: // Leave lobby
                LobbyActionMessage leave_msg = new LobbyActionMessage(LobbyActionMessage.Type.LEAVE);
                Misca.crabs.network.sendToServer(leave_msg);
                break;
            case 3: // Start fight
                LobbyActionMessage fight_msg = new LobbyActionMessage(LobbyActionMessage.Type.FIGHT);
                Misca.crabs.network.sendToServer(fight_msg);
                Minecraft.getMinecraft().displayGuiScreen(GuiScreenCombat.instance);
                break;
//            case 3: // Include in lobby
//                LobbyActionMessage include_msg = new LobbyActionMessage(LobbyActionMessage.Type.INCLUDE);
//                Crabs.crabs.network.sendToServer(include_msg);
//                break;
            case 4: // Exclude from lobby
                LobbyActionMessage exclude_msg = new LobbyActionMessage(LobbyActionMessage.Type.EXCLUDE);
                Misca.crabs.network.sendToServer(exclude_msg);
                break;
        }
    }

    public void onLobbyUpdate(Lobby lobby) {
        this.lobby = lobby;

        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (lobby != null && !lobby.hasMember(self))
            lobby = null;

        if (Minecraft.getMinecraft().currentScreen != instance) return;

        playerToJoinText.setEnabled(lobby == null);
        createLobbyBtn.enabled = lobby == null;
        joinLobbyBtn.enabled = lobby == null;
        leaveLobbyBtn.enabled = lobby != null;
        startFightBtn.enabled = (lobby != null && self == lobby.master() && lobby.members().size() > 1);
    }
}
