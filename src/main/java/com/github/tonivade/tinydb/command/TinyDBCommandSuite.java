/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import com.github.tonivade.resp.command.CommandSuite;
import com.github.tonivade.tinydb.command.bitset.BitCountCommand;
import com.github.tonivade.tinydb.command.bitset.GetBitCommand;
import com.github.tonivade.tinydb.command.bitset.SetBitCommand;
import com.github.tonivade.tinydb.command.hash.HashDeleteCommand;
import com.github.tonivade.tinydb.command.hash.HashExistsCommand;
import com.github.tonivade.tinydb.command.hash.HashGetAllCommand;
import com.github.tonivade.tinydb.command.hash.HashGetCommand;
import com.github.tonivade.tinydb.command.hash.HashKeysCommand;
import com.github.tonivade.tinydb.command.hash.HashLengthCommand;
import com.github.tonivade.tinydb.command.hash.HashSetCommand;
import com.github.tonivade.tinydb.command.hash.HashValuesCommand;
import com.github.tonivade.tinydb.command.key.DeleteCommand;
import com.github.tonivade.tinydb.command.key.ExistsCommand;
import com.github.tonivade.tinydb.command.key.ExpireCommand;
import com.github.tonivade.tinydb.command.key.KeysCommand;
import com.github.tonivade.tinydb.command.key.PersistCommand;
import com.github.tonivade.tinydb.command.key.RenameCommand;
import com.github.tonivade.tinydb.command.key.TimeToLiveMillisCommand;
import com.github.tonivade.tinydb.command.key.TimeToLiveSecondsCommand;
import com.github.tonivade.tinydb.command.key.TypeCommand;
import com.github.tonivade.tinydb.command.list.LeftPopCommand;
import com.github.tonivade.tinydb.command.list.LeftPushCommand;
import com.github.tonivade.tinydb.command.list.ListIndexCommand;
import com.github.tonivade.tinydb.command.list.ListLengthCommand;
import com.github.tonivade.tinydb.command.list.ListRangeCommand;
import com.github.tonivade.tinydb.command.list.ListSetCommand;
import com.github.tonivade.tinydb.command.list.RightPopCommand;
import com.github.tonivade.tinydb.command.list.RightPushCommand;
import com.github.tonivade.tinydb.command.pubsub.PublishCommand;
import com.github.tonivade.tinydb.command.pubsub.SubscribeCommand;
import com.github.tonivade.tinydb.command.pubsub.UnsubscribeCommand;
import com.github.tonivade.tinydb.command.server.FlushDBCommand;
import com.github.tonivade.tinydb.command.server.InfoCommand;
import com.github.tonivade.tinydb.command.server.SelectCommand;
import com.github.tonivade.tinydb.command.server.SlaveOfCommand;
import com.github.tonivade.tinydb.command.server.SyncCommand;
import com.github.tonivade.tinydb.command.set.SetAddCommand;
import com.github.tonivade.tinydb.command.set.SetCardinalityCommand;
import com.github.tonivade.tinydb.command.set.SetDifferenceCommand;
import com.github.tonivade.tinydb.command.set.SetIntersectionCommand;
import com.github.tonivade.tinydb.command.set.SetIsMemberCommand;
import com.github.tonivade.tinydb.command.set.SetMembersCommand;
import com.github.tonivade.tinydb.command.set.SetRemoveCommand;
import com.github.tonivade.tinydb.command.set.SetUnionCommand;
import com.github.tonivade.tinydb.command.string.DecrementByCommand;
import com.github.tonivade.tinydb.command.string.DecrementCommand;
import com.github.tonivade.tinydb.command.string.GetCommand;
import com.github.tonivade.tinydb.command.string.GetSetCommand;
import com.github.tonivade.tinydb.command.string.IncrementByCommand;
import com.github.tonivade.tinydb.command.string.IncrementCommand;
import com.github.tonivade.tinydb.command.string.MultiGetCommand;
import com.github.tonivade.tinydb.command.string.MultiSetCommand;
import com.github.tonivade.tinydb.command.string.SetCommand;
import com.github.tonivade.tinydb.command.string.SetExpiredCommand;
import com.github.tonivade.tinydb.command.string.StringLengthCommand;
import com.github.tonivade.tinydb.command.transaction.ExecCommand;
import com.github.tonivade.tinydb.command.transaction.MultiCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetAddCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetCardinalityCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetRangeByScoreCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetRangeCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetRemoveCommand;
import com.github.tonivade.tinydb.command.zset.SortedSetReverseRangeCommand;

public class TinyDBCommandSuite extends CommandSuite {

    public TinyDBCommandSuite() {
        super(new TinyDBCommandWrapperFactory());
        // connection
        addCommand(SelectCommand.class);
        addCommand(SyncCommand.class);
        addCommand(SlaveOfCommand.class);

        // server
        addCommand(FlushDBCommand.class);
        addCommand(InfoCommand.class);

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

        // pub & sub
        addCommand(PublishCommand.class);
        addCommand(SubscribeCommand.class);
        addCommand(UnsubscribeCommand.class);

        // transactions
        addCommand(MultiCommand.class);
        addCommand(ExecCommand.class);
    }
}
