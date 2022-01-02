/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
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
    addCommand(SelectCommand::new);
    addCommand(SyncCommand::new);
    addCommand(SlaveOfCommand::new);

    // server
    addCommand(FlushDBCommand::new);
    addCommand(InfoCommand::new);
    addCommand(RoleCommand::new);
    addCommand(DatabaseSizeCommand::new);

    // strings
    addCommand(GetCommand::new);
    addCommand(MultiGetCommand::new);
    addCommand(SetCommand::new);
    addCommand(MultiSetCommand::new);
    addCommand(GetSetCommand::new);
    addCommand(IncrementCommand::new);
    addCommand(IncrementByCommand::new);
    addCommand(DecrementCommand::new);
    addCommand(DecrementByCommand::new);
    addCommand(StringLengthCommand::new);
    addCommand(SetExpiredCommand::new);
    addCommand(BitCountCommand::new);
    addCommand(SetBitCommand::new);
    addCommand(GetBitCommand::new);
    addCommand(SetIfNotExistsCommand::new);
    addCommand(MultiSetIfNotExistsCommand::new);

    // keys
    addCommand(DeleteCommand::new);
    addCommand(ExistsCommand::new);
    addCommand(TypeCommand::new);
    addCommand(RenameCommand::new);
    addCommand(KeysCommand::new);
    addCommand(ExpireCommand::new);
    addCommand(PersistCommand::new);
    addCommand(TimeToLiveMillisCommand::new);
    addCommand(TimeToLiveSecondsCommand::new);

    // hash
    addCommand(HashSetCommand::new);
    addCommand(HashGetCommand::new);
    addCommand(HashGetAllCommand::new);
    addCommand(HashExistsCommand::new);
    addCommand(HashDeleteCommand::new);
    addCommand(HashKeysCommand::new);
    addCommand(HashLengthCommand::new);
    addCommand(HashMultiGetCommand::new);
    addCommand(HashMultiSetCommand::new);
    addCommand(HashValuesCommand::new);

    // list
    addCommand(LeftPushCommand::new);
    addCommand(LeftPopCommand::new);
    addCommand(RightPushCommand::new);
    addCommand(RightPopCommand::new);
    addCommand(ListLengthCommand::new);
    addCommand(ListRangeCommand::new);
    addCommand(ListIndexCommand::new);
    addCommand(ListSetCommand::new);

    // set
    addCommand(SetAddCommand::new);
    addCommand(SetMembersCommand::new);
    addCommand(SetCardinalityCommand::new);
    addCommand(SetIsMemberCommand::new);
    addCommand(SetRemoveCommand::new);
    addCommand(SetUnionCommand::new);
    addCommand(SetIntersectionCommand::new);
    addCommand(SetDifferenceCommand::new);

    // sorted set
    addCommand(SortedSetAddCommand::new);
    addCommand(SortedSetCardinalityCommand::new);
    addCommand(SortedSetRemoveCommand::new);
    addCommand(SortedSetRangeCommand::new);
    addCommand(SortedSetRangeByScoreCommand::new);
    addCommand(SortedSetReverseRangeCommand::new);
    addCommand(SortedSetIncrementByCommand::new);

    // pub & sub
    addCommand(PublishCommand::new);
    addCommand(SubscribeCommand::new);
    addCommand(UnsubscribeCommand::new);
    addCommand(PatternSubscribeCommand::new);
    addCommand(PatternUnsubscribeCommand::new);

    // transactions
    addCommand(MultiCommand::new);
    addCommand(ExecCommand::new);
    addCommand(DiscardCommand::new);

    // scripting
    addCommand(EvalCommand::new);
    addCommand(EvalShaCommand::new);
    addCommand(ScriptCommands::new);
  }

  public boolean isReadOnly(String command) {
    return COMMAND_BLACK_LIST.contains(command) || isPresent(command, ReadOnly.class);
  }
}
