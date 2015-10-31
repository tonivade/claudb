/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import tonivade.db.command.hash.HashDeleteCommand;
import tonivade.db.command.hash.HashExistsCommand;
import tonivade.db.command.hash.HashGetAllCommand;
import tonivade.db.command.hash.HashGetCommand;
import tonivade.db.command.hash.HashKeysCommand;
import tonivade.db.command.hash.HashLengthCommand;
import tonivade.db.command.hash.HashSetCommand;
import tonivade.db.command.hash.HashValuesCommand;
import tonivade.db.command.key.DeleteCommand;
import tonivade.db.command.key.ExistsCommand;
import tonivade.db.command.key.ExpireCommand;
import tonivade.db.command.key.KeysCommand;
import tonivade.db.command.key.PersistCommand;
import tonivade.db.command.key.RenameCommand;
import tonivade.db.command.key.TimeToLiveCommand;
import tonivade.db.command.key.TypeCommand;
import tonivade.db.command.list.LeftPopCommand;
import tonivade.db.command.list.LeftPushCommand;
import tonivade.db.command.list.ListIndexCommand;
import tonivade.db.command.list.ListLengthCommand;
import tonivade.db.command.list.ListRangeCommand;
import tonivade.db.command.list.ListSetCommand;
import tonivade.db.command.list.RightPopCommand;
import tonivade.db.command.list.RightPushCommand;
import tonivade.db.command.pubsub.PublishCommand;
import tonivade.db.command.pubsub.SubscribeCommand;
import tonivade.db.command.pubsub.UnsubscribeCommand;
import tonivade.db.command.server.FlushDBCommand;
import tonivade.db.command.server.InfoCommand;
import tonivade.db.command.server.SelectCommand;
import tonivade.db.command.server.SlaveOfCommand;
import tonivade.db.command.server.SyncCommand;
import tonivade.db.command.set.SetAddCommand;
import tonivade.db.command.set.SetCardinalityCommand;
import tonivade.db.command.set.SetDifferenceCommand;
import tonivade.db.command.set.SetIntersectionCommand;
import tonivade.db.command.set.SetIsMemberCommand;
import tonivade.db.command.set.SetMembersCommand;
import tonivade.db.command.set.SetRemoveCommand;
import tonivade.db.command.set.SetUnionCommand;
import tonivade.db.command.string.DecrementByCommand;
import tonivade.db.command.string.DecrementCommand;
import tonivade.db.command.string.GetCommand;
import tonivade.db.command.string.GetSetCommand;
import tonivade.db.command.string.IncrementByCommand;
import tonivade.db.command.string.IncrementCommand;
import tonivade.db.command.string.MultiGetCommand;
import tonivade.db.command.string.MultiSetCommand;
import tonivade.db.command.string.SetCommand;
import tonivade.db.command.string.SetExpiredCommand;
import tonivade.db.command.string.StringLengthCommand;
import tonivade.db.command.zset.SortedSetAddCommand;
import tonivade.db.command.zset.SortedSetCardinalityCommand;
import tonivade.db.command.zset.SortedSetRangeByScoreCommand;
import tonivade.db.command.zset.SortedSetRangeCommand;
import tonivade.db.command.zset.SortedSetRemoveCommand;
import tonivade.db.command.zset.SortedSetReverseRangeCommand;
import tonivade.redis.command.CommandSuite;
import tonivade.redis.command.ICommand;

public class RedisCommandSuite extends CommandSuite {

    public RedisCommandSuite() {
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

        // keys
        addCommand(DeleteCommand.class);
        addCommand(ExistsCommand.class);
        addCommand(TypeCommand.class);
        addCommand(RenameCommand.class);
        addCommand(KeysCommand.class);
        addCommand(ExpireCommand.class);
        addCommand(PersistCommand.class);
        addCommand(TimeToLiveCommand.class);

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
    }

    @Override
    protected ICommand wrap(Object command) {
        if (command instanceof IRedisCommand) {
            return new RedisCommandWrapper((IRedisCommand) command);
        }
        return super.wrap(command);
    }
}
