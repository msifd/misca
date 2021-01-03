package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharsheetProvider;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Set;

public class CombatantInfo {
    public final ICharsheet cs;
    public final Set<WeaponTrait> traits;
    public final Vec3d pos;

    public CombatantInfo(EntityLivingBase attacker, LivingHurtEvent event) {
        this.cs = CharsheetProvider.get(attacker);
        this.traits = WeaponTrait.get(event.getSource(), attacker);
        this.pos = attacker.getPositionVector();
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.cs = CharsheetProvider.get(victim);
        this.traits = WeaponTrait.get(victim);
        this.pos = CombatantProvider.get(victim).getPosition();
    }

    public boolean is(WeaponTrait trait) {
        return traits.contains(trait);
    }
}
