package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharsheetProvider;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Set;

public class CombatantInfo {
    public final ICharsheet cs;
    public final Vec3d pos;
    private final Set<WeaponTrait> traits;

    public CombatantInfo(EntityLivingBase attacker, DamageSource source) {
        this.cs = CharsheetProvider.get(attacker);
        this.pos = attacker.getPositionVector();
        this.traits = WeaponTrait.get(source, attacker);
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.cs = CharsheetProvider.get(victim);
        this.pos = CombatantProvider.get(victim).getPosition();
        this.traits = WeaponTrait.get(victim);
    }

    public boolean is(WeaponTrait trait) {
        return traits.contains(trait);
    }
}
