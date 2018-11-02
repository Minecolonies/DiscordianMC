package com.ldt.chatchainmc.core.commands.general;

import com.ldt.chatchainmc.api.StaticAPIChannels;
import com.ldt.chatchainmc.api.capabilities.ChannelProvider;
import com.ldt.chatchainmc.api.capabilities.IChannelStorage;
import com.ldt.chatchainmc.core.ChatChainMC;
import com.ldt.chatchainmc.core.commands.AbstractSingleCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListenCommand extends AbstractSingleCommand
{
    public static final String DESC = "listen";

    public ListenCommand(@NotNull final String... parents)
    {
        super(parents);
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException
    {
        if (args.length >= 1 && sender instanceof EntityPlayer)
        {
            final String channel = args[0].toLowerCase();
            final IChannelStorage channelStorage = ((EntityPlayer) sender).getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);
            if (channelStorage != null)
            {
                if (channelStorage.getChannels().contains(channel) && (ChatChainMC.instance.getMainConfig().createdChannels.contains(channel) || (channel.equalsIgnoreCase(
                  StaticAPIChannels.MAIN) || channel.equalsIgnoreCase(StaticAPIChannels.STAFF))))
                {
                    channelStorage.addLChannel(channel);
                    sender.sendMessage(new TextComponentString("§6Channel §e" + channel + "§6 is now being listened to"));
                }
                else if (!ChatChainMC.instance.getMainConfig().createdChannels.contains(channel))
                {
                    channelStorage.removeLChannel(channel);
                    channelStorage.removeChannel(channel);
                    sender.sendMessage(new TextComponentString("§4This channel does not exist!"));
                }
                else
                {
                    channelStorage.removeLChannel(channel);
                    channelStorage.removeChannel(channel);
                    sender.sendMessage(new TextComponentString("§4You do not have access to this channel!"));
                }
            }
        }
    }

    @Override
    public @NotNull List<String> getTabCompletionOptions(
      final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, @NotNull final String[] args, @Nullable final BlockPos pos)
    {
        if (sender instanceof EntityPlayer)
        {
            final IChannelStorage channelStorage = ((EntityPlayer) sender).getCapability(ChannelProvider.CHANNEL_STORAGE_CAP, null);
            if (channelStorage != null)
            {
                final List<String> mutedChannels = new ArrayList<>();

                for (final String channel : channelStorage.getChannels())
                {
                    if (!channelStorage.getListeningChannels().contains(channel))
                    {
                        mutedChannels.add(channel);
                    }
                }

                if (args.length <= 1
                      || !mutedChannels.contains(args[0]))
                {
                    return mutedChannels.stream().filter(k -> k.startsWith(args[0])).collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@NotNull final String[] args, final int index)
    {
        return false;
    }
}
