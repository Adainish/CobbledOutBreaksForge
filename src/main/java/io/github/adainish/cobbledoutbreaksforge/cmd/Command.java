package io.github.adainish.cobbledoutbreaksforge.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class Command
{
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("lastoutbreak")
                .executes(cc -> {
                    if (cc.getSource().isPlayer())
                    {
                        CobbledOutBreaksForge.previousOutBreaks.open(cc.getSource().getPlayer());
                    } else {
                        cc.getSource().sendSystemMessage(Component.literal(Util.formattedString("&aThe last outbreak was: &b" + CobbledOutBreaksForge.outbreaksManager.lastOutBreak + " &aago.")));
                    }
                    return 1;
                });
    }
}
