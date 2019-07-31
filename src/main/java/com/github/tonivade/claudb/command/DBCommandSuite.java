/*
 * Copyright (c) 2015-2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.command.bitset.BitCountCommand;
import com.github.tonivade.claudb.command.bitset.GetBitCommand;
import com.github.tonivade.claudb.command.bitset.SetBitCommand;
import com.github.tonivade.claudb.command.hash.HashDeleteCommand;
import com.github.tonivade.claudb.command.hash.HashExistsCommand;
import com.github.tonivade.claudb.command.hash.HashGetAllCommand;
import com.github.tonivade.claudb.command.hash.HashGetCommand;
import com.github.tonivade.claudb.command.hash.HashKeysCommand;
import com.github.tonivade.claudb.command.hash.HashLengthCommand;
import com.github.tonivade.claudb.command.hash.HashMultiGetCommand;
import com.github.tonivade.claudb.command.hash.HashMultiSetCommand;
import com.github.tonivade.claudb.command.hash.HashSetCommand;
import com.github.tonivade.claudb.command.hash.HashValuesCommand;
import com.github.tonivade.claudb.command.key.DeleteCommand;
import com.github.tonivade.claudb.command.key.ExistsCommand;
import com.github.tonivade.claudb.command.key.ExpireCommand;
import com.github.tonivade.claudb.command.key.KeysCommand;
import com.github.tonivade.claudb.command.key.PersistCommand;
import com.github.tonivade.claudb.command.key.RenameCommand;
import com.github.tonivade.claudb.command.key.TimeToLiveMillisCommand;
import com.github.tonivade.claudb.command.key.TimeToLiveSecondsCommand;
import com.github.tonivade.claudb.command.key.TypeCommand;
import com.github.tonivade.claudb.command.list.LeftPopCommand;
import com.github.tonivade.claudb.command.list.LeftPushCommand;
import com.github.tonivade.claudb.command.list.ListIndexCommand;
import com.github.tonivade.claudb.command.list.ListLengthCommand;
import com.github.tonivade.claudb.command.list.ListRangeCommand;
import com.github.tonivade.claudb.command.list.ListSetCommand;
import com.github.tonivade.claudb.command.list.RightPopCommand;
import com.github.tonivade.claudb.command.list.RightPushCommand;
import com.github.tonivade.claudb.command.pubsub.PatternSubscribeCommand;
import com.github.tonivade.claudb.command.pubsub.PatternUnsubscribeCommand;
import com.github.tonivade.claudb.command.pubsub.PublishCommand;
import com.github.tonivade.claudb.command.pubsub.SubscribeCommand;
import com.github.tonivade.claudb.command.pubsub.UnsubscribeCommand;
import com.github.tonivade.claudb.command.scripting.EvalCommand;
import com.github.tonivade.claudb.command.scripting.EvalShaCommand;
import com.github.tonivade.claudb.command.scripting.ScriptCommands;
import com.github.tonivade.claudb.command.server.DatabaseSizeCommand;
import com.github.tonivade.claudb.command.server.FlushDBCommand;
import com.github.tonivade.claudb.command.server.InfoCommand;
import com.github.tonivade.claudb.command.server.RoleCommand;
import com.github.tonivade.claudb.command.server.SelectCommand;
import com.github.tonivade.claudb.command.server.SlaveOfCommand;
import com.github.tonivade.claudb.command.server.SyncCommand;
import com.github.tonivade.claudb.command.set.SetAddCommand;
import com.github.tonivade.claudb.command.set.SetCardinalityCommand;
import com.github.tonivade.claudb.command.set.SetDifferenceCommand;
import com.github.tonivade.claudb.command.set.SetIntersectionCommand;
import com.github.tonivade.claudb.command.set.SetIsMemberCommand;
import com.github.tonivade.claudb.command.set.SetMembersCommand;
import com.github.tonivade.claudb.command.set.SetRemoveCommand;
import com.github.tonivade.claudb.command.set.SetUnionCommand;
import com.github.tonivade.claudb.command.string.DecrementByCommand;
import com.github.tonivade.claudb.command.string.DecrementCommand;
import com.github.tonivade.claudb.command.string.GetCommand;
import com.github.tonivade.claudb.command.string.GetSetCommand;
import com.github.tonivade.claudb.command.string.IncrementByCommand;
import com.github.tonivade.claudb.command.string.IncrementCommand;
import com.github.tonivade.claudb.command.string.MultiGetCommand;
import com.github.tonivade.claudb.command.string.MultiSetCommand;
import com.github.tonivade.claudb.command.string.MultiSetIfNotExistsCommand;
import com.github.tonivade.claudb.command.string.SetCommand;
import com.github.tonivade.claudb.command.string.SetExpiredCommand;
import com.github.tonivade.claudb.command.string.SetIfNotExistsCommand;
import com.github.tonivade.claudb.command.string.StringLengthCommand;
import com.github.tonivade.claudb.command.transaction.DiscardCommand;
import com.github.tonivade.claudb.command.transaction.ExecCommand;
import com.github.tonivade.claudb.command.transaction.MultiCommand;
import com.github.tonivade.claudb.command.zset.SortedSetAddCommand;
import com.github.tonivade.claudb.command.zset.SortedSetCardinalityCommand;
import com.github.tonivade.claudb.command.zset.SortedSetIncrementByCommand;
import com.github.tonivade.claudb.command.zset.SortedSetRangeByScoreCommand;
import com.github.tonivade.claudb.command.zset.SortedSetRangeCommand;
import com.github.tonivade.claudb.command.zset.SortedSetRemoveCommand;
import com.github.tonivade.claudb.command.zset.SortedSetReverseRangeCommand;
import com.github.tonivade.resp.command.CommandSuite;

public class DBCommandSuite extends CommandSuite {

  private static final Set<String> COMMAND_BLACK_LIST = new HashSet<>(asList("ping", "echo", "quit", "time"));

  public DBCommandSuite() {
    super(new DBCommandWrapperFactory());
    // connection
    addCommand(SelectCommand.class);
    addCommand(SyncCommand.class);
    addCommand(SlaveOfCommand.class);

    // server
    addCommand(FlushDBCommand.class);
    addCommand(InfoCommand.class);
    addCommand(RoleCommand.class);
    addCommand(DatabaseSizeCommand.class);

    // strings
    addCommand(GetCommand.class);
    addCommand(MultiGetCommand.class);
    addCommand(SetCommand.class);
    addCommand(MultiSetCommand.class);
    addCommand(GetSetCommand.class);
    addCommand(IncrementCommand.class);
    addCommand(IncrementByCommand.class);
    addCommand(DecrementCommand.class);
    addCommand(DecrementByCommand.class);
    addCommand(StringLengthCommand.class);
    addCommand(SetExpiredCommand.class);
    addCommand(BitCountCommand.class);
    addCommand(SetBitCommand.class);
    addCommand(GetBitCommand.class);
    addCommand(SetIfNotExistsCommand.class);
    addCommand(MultiSetIfNotExistsCommand.class);

    // keys
    addCommand(DeleteCommand.class);
    addCommand(ExistsCommand.class);
    addCommand(TypeCommand.class);
    addCommand(RenameCommand.class);
    addCommand(KeysCommand.class);
    addCommand(ExpireCommand.class);
    addCommand(PersistCommand.class);
    addCommand(TimeToLiveMillisCommand.class);
    addCommand(TimeToLiveSecondsCommand.class);

    // hash
    addCommand(HashSetCommand.class);
    addCommand(HashGetCommand.class);
    addCommand(HashGetAllCommand.class);
    addCommand(HashExistsCommand.class);
    addCommand(HashDeleteCommand.class);
    addCommand(HashKeysCommand.class);
    addCommand(HashLengthCommand.class);
    addCommand(HashMultiGetCommand.class);
    addCommand(HashMultiSetCommand.class);
    addCommand(HashValuesCommand.class);

    // list
    addCommand(LeftPushCommand.class);
    addCommand(LeftPopCommand.class);
    addCommand(RightPushCommand.class);
    addCommand(RightPopCommand.class);
    addCommand(ListLengthCommand.class);
    addCommand(ListRangeCommand.class);
    addCommand(ListIndexCommand.class);
    addCommand(ListSetCommand.class);

    // set
    addCommand(SetAddCommand.class);
    addCommand(SetMembersCommand.class);
    addCommand(SetCardinalityCommand.class);
    addCommand(SetIsMemberCommand.class);
    addCommand(SetRemoveCommand.class);
    addCommand(SetUnionCommand.class);
    addCommand(SetIntersectionCommand.class);
    addCommand(SetDifferenceCommand.class);

    // sorted set
    addCommand(SortedSetAddCommand.class);
    addCommand(SortedSetCardinalityCommand.class);
    addCommand(SortedSetRemoveCommand.class);
    addCommand(SortedSetRangeCommand.class);
    addCommand(SortedSetRangeByScoreCommand.class);
    addCommand(SortedSetReverseRangeCommand.class);
    addCommand(SortedSetIncrementByCommand.class);

    // pub & sub
    addCommand(PublishCommand.class);
    addCommand(SubscribeCommand.class);
    addCommand(UnsubscribeCommand.class);
    addCommand(PatternSubscribeCommand.class);
    addCommand(PatternUnsubscribeCommand.class);

    // transactions
    addCommand(MultiCommand.class);
    addCommand(ExecCommand.class);
    addCommand(DiscardCommand.class);

    // scripting
    addCommand(EvalCommand.class);
    addCommand(EvalShaCommand.class);
    addCommand(ScriptCommands.class);
  }

  public boolean isReadOnly(String command) {
    return COMMAND_BLACK_LIST.contains(command) || isPresent(command, ReadOnly.class);
  }
}
