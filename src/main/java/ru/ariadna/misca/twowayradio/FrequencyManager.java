package ru.ariadna.misca.twowayradio;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.entity.player.EntityPlayer;

public class FrequencyManager {
    LinkedListMultimap<Float, EntityPlayer> frequencies = LinkedListMultimap.create();
}
