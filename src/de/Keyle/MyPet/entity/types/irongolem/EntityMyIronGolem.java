/*
 * Copyright (C) 2011-2012 Keyle
 *
 * This file is part of MyPet
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyPet. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.entity.types.irongolem;

import de.Keyle.MyPet.entity.pathfinder.PathfinderGoalAggressiveTarget;
import de.Keyle.MyPet.entity.pathfinder.PathfinderGoalControl;
import de.Keyle.MyPet.entity.pathfinder.PathfinderGoalControlTarget;
import de.Keyle.MyPet.entity.types.EntityMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.skill.skills.Control;
import de.Keyle.MyPet.util.MyPetConfig;
import net.minecraft.server.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class EntityMyIronGolem extends EntityMyPet
{
    public EntityMyIronGolem(World world, MyPet MPet)
    {
        super(world, MPet);
        this.texture = "/mob/villager_golem.png";
        this.b(1.4F, 2.9F);
        this.al().a(true);

        PathfinderGoalControl Control = new PathfinderGoalControl(MPet, 0.4F);

        this.goalSelector.a(2, this.a);
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 0.25F, true));
        this.goalSelector.a(5, Control);
        this.goalSelector.a(7, new de.Keyle.MyPet.entity.pathfinder.PathfinderGoalFollowOwner(this, 0.2F, 5.0F, 2.0F, Control));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new de.Keyle.MyPet.entity.pathfinder.PathfinderGoalOwnerHurtTarget(MPet));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true));
        this.targetSelector.a(4, new PathfinderGoalControlTarget(MPet, Control, 1));
        this.targetSelector.a(5, new PathfinderGoalAggressiveTarget(MPet, 10));
    }

    @Override
    public void setMyPet(MyPet MPet)
    {
        if (MPet != null)
        {
            this.MPet = MPet;
            isMyPet = true;
            if (!isTamed())
            {
                this.setTamed(true);
                this.setPathEntity(null);
                this.setSitting(MPet.isSitting());
                this.setHealth(MPet.getHealth() >= getMaxHealth() ? getMaxHealth() : MPet.getHealth());
                this.setOwnerName(MPet.getOwner().getName());
                this.world.broadcastEntityEffect(this, (byte) 7);
            }
        }
    }

    public int getMaxHealth()
    {
        return MyPetConfig.StartHP + (isTamed() && MPet.getSkillSystem().hasSkill("HP") ? MPet.getSkillSystem().getSkill("HP").getLevel() : 0);
    }

    public boolean b(EntityHuman entityhuman)
    {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (isMyPet() && entityhuman.name.equalsIgnoreCase(this.getOwnerName()))
        {
            if (MPet.getSkillSystem().hasSkill("Control") && MPet.getSkillSystem().getSkill("Control").getLevel() > 0)
            {
                if (MPet.getOwner().getPlayer().getItemInHand().getType() == Control.Item)
                {
                    return true;
                }
            }
        }

        if (this.a(itemstack))
        {
            ItemFood itemfood = (ItemFood) Item.byId[itemstack.id];

            if (getHealth() < getMaxHealth())
            {
                if (!entityhuman.abilities.canInstantlyBuild)
                {
                    --itemstack.count;
                }
                this.heal(itemfood.getNutrition(), RegainReason.EATING);
                if (itemstack.count <= 0)
                {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                }
                this.a(true);
                return true;
            }
        }
        else if (entityhuman.name.equalsIgnoreCase(this.getOwnerName()) && !this.world.isStatic)
        {
            this.a.a(!this.isSitting());
            this.aZ = false;
            this.setPathEntity(null);
        }

        return false;
    }

    public boolean a(Entity entity)
    {
        int damage = 7 + (isMyPet && MPet.getSkillSystem().hasSkill("Damage") ? MPet.getSkillSystem().getSkill("Damage").getLevel() : 0);

        this.c = 10;
        this.world.broadcastEntityEffect(this, (byte) 4);
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), damage + this.random.nextInt(15));

        if (flag)
        {
            entity.motY += 0.4000000059604645D;
        }

        this.world.makeSound(this, "mob.irongolem.throw", 1.0F, 1.0F);
        return flag;
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntity()
    {
        if (this.bukkitEntity == null)
        {
            this.bukkitEntity = new CraftMyIronGolem(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    //Changed Vanilla Methods ---------------------------------------------------------------------------------------

    @Override
    protected void g()
    {
    }

    protected void b()
    {
        super.b();
    }

    // Vanilla Methods

    protected String i()
    {
        return "none";
    }

    @Override
    protected String j()
    {
        return "mob.irongolem.hit";
    }

    @Override
    protected String k()
    {
        return "mob.irongolem.death";
    }

    @Override
    protected float p()
    {
        return 0.4F;
    }

    protected void a(int i, int j, int k, int l)
    {
        this.world.makeSound(this, "mob.irongolem.walk", 1.0F, 1.0F);
    }
}