/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2014 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.listeners;

import de.Keyle.MyPet.api.event.MyPetLevelUpEvent;
import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.entity.types.MyPet.PetState;
import de.Keyle.MyPet.skill.Experience;
import de.Keyle.MyPet.skill.skills.info.ISkillInfo;
import de.Keyle.MyPet.skill.skilltree.SkillTree;
import de.Keyle.MyPet.skill.skilltree.SkillTreeLevel;
import de.Keyle.MyPet.util.Colorizer;
import de.Keyle.MyPet.util.Configuration;
import de.Keyle.MyPet.util.Util;
import de.Keyle.MyPet.util.locale.Locales;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

public class LevelUpListener implements Listener {
    @EventHandler
    public void onLevelUp(MyPetLevelUpEvent event) {
        MyPet myPet = event.getPet();
        int lvl = event.getLevel();

        if (!event.isQuiet()) {
            int maxlevel = myPet.getSkillTree() != null ? myPet.getSkillTree().getMaxLevel() : 0;
            if (maxlevel != 0 && lvl >= maxlevel) {
                myPet.sendMessageToOwner(Util.formatText(Locales.getString("Message.LevelSystem.ReachedMaxLevel", event.getOwner().getLanguage()), myPet.getPetName(), maxlevel));
            } else {
                myPet.sendMessageToOwner(Util.formatText(Locales.getString("Message.LevelSystem.LevelUp", event.getOwner().getLanguage()), myPet.getPetName(), event.getLevel()));
            }

            if (Experience.FIREWORK_ON_LEVELUP) {
                Firework fw = (Firework) myPet.getLocation().getWorld().spawnEntity(myPet.getLocation(), EntityType.FIREWORK);
                FireworkEffect fwe = FireworkEffect.builder().with(Type.STAR).withColor(Color.fromRGB(Configuration.LEVELUP_FIREWORK_COLOR)).withTrail().withFlicker().build();
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.addEffect(fwe);
                fwm.addEffect(fwe);
                fwm.addEffect(fwe);
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);
                fw.detonate();
            }
        }
        SkillTree skillTree = myPet.getSkillTree();
        if (skillTree != null && skillTree.hasLevel(lvl)) {
            SkillTreeLevel level = skillTree.getLevel(lvl);
            if (!event.isQuiet()) {
                if (level.hasLevelupMessage()) {
                    myPet.sendMessageToOwner(Colorizer.setColors(level.getLevelupMessage()));
                }
            }

            List<ISkillInfo> skillList = level.getSkills();
            for (ISkillInfo skill : skillList) {
                myPet.getSkills().getSkill(skill.getName()).upgrade(skill, event.isQuiet());
            }
        }

        if (!event.isQuiet() && myPet.getStatus() == PetState.Here) {
            myPet.setHealth(myPet.getMaxHealth());
            myPet.setHungerValue(100);
        }
    }
}