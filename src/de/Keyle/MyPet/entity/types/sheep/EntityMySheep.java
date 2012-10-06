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

package de.Keyle.MyPet.entity.types.sheep;

import de.Keyle.MyPet.entity.pathfinder.PathfinderGoalControl;
import de.Keyle.MyPet.entity.pathfinder.PathfinderGoalFollowOwner;
import de.Keyle.MyPet.entity.types.EntityMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import net.minecraft.server.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class EntityMySheep extends EntityMyPet
{
    public EntityMySheep(World world, MyPet MPet)
    {
        super(world, MPet);
        this.texture = "/mob/sheep.png";
        this.a(0.9F, 1.3F);
        this.bw = 0.25F;
        this.getNavigation().a(true);

        PathfinderGoalControl Control = new PathfinderGoalControl(MPet, 0.38F);

        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.d);
        this.goalSelector.a(3, Control);
        this.goalSelector.a(4, new PathfinderGoalPanic(this, 0.38F));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, this.bw, 5.0F, 2.0F, Control));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
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
                //this.setColor(((MySheep) MPet).getColor());
            }
        }
    }

    public int getMaxHealth()
    {
        return MySheep.getStartHP() + (isTamed() && MPet.getSkillSystem().hasSkill("HP") ? MPet.getSkillSystem().getSkill("HP").getLevel() : 0);
    }

    public boolean c(EntityHuman entityhuman)
    {
        super.c(entityhuman);

        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.id == org.bukkit.Material.WHEAT.getId())
        {
            if (getHealth() < getMaxHealth())
            {
                if (!entityhuman.abilities.canInstantlyBuild)
                {
                    --itemstack.count;
                }
                this.heal(3, RegainReason.EATING);
                if (itemstack.count <= 0)
                {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                }
                this.e(true);
                return true;
            }
        }
        else if (itemstack != null && itemstack.id == 351)
        {
            if (itemstack.getData() <= 15)
            {
                ((MySheep) MPet).setColor(15 - itemstack.getData());
                return true;
            }
        }
        else if (entityhuman.name.equalsIgnoreCase(this.getOwnerName()) && !this.world.isStatic)
        {
            this.d.a(!this.isSitting());
            this.bu = false;
            this.setPathEntity(null);
        }

        return false;
    }

    public boolean k(Entity entity)
    {
        int damage = 1 + (isMyPet && MPet.getSkillSystem().hasSkill("Damage") ? MPet.getSkillSystem().getSkill("Damage").getLevel() : 0);

        return entity.damageEntity(DamageSource.mobAttack(this), damage);
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntity()
    {
        if (this.bukkitEntity == null)
        {
            this.bukkitEntity = new CraftMySheep(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    //Unused changed Vanilla Methods ---------------------------------------------------------------------------------------

    @Override
    protected void bd()
    {
        this.datawatcher.watch(18, this.getHealth());
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(18, this.getHealth());
    }

    // Vanilla Methods

    protected String aQ()
    {
        return "mob.sheep";
    }

    @Override
    protected String aR()
    {
        return "mob.sheep";
    }

    @Override
    protected String aS()
    {
        return "mob.sheep";
    }

    public int getColor()
    {
        return this.datawatcher.getByte(16) & 15;
    }

    public void setColor(int i)
    {
        byte b0 = this.datawatcher.getByte(16);

        this.datawatcher.watch(16, (byte) (b0 & 240 | i & 15));
    }

    @Override
    protected float aP()
    {
        return 0.4F;
    }
}