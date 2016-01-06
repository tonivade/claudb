/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        Pattern pattern = createPattern(request.getParam(0));
        Predicate<? super DatabaseKey> predicate = (key) -> {
            return pattern.matcher(key.toString()).matches();
        };
        Set<SafeString> keys = db.keySet().stream().filter(predicate).map(DatabaseKey::getValue).collect(toSet());
        response.addArray(keys);
    }

    private Pattern createPattern(SafeString param) {
        return Pattern.compile(convertGlobToRegEx(param.toString()));
    }

    /*
     * taken from http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
     */
    private String convertGlobToRegEx(String line) {
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
            case '*':
                if (escaping) {
                    sb.append("\\*");
                } else {
                    sb.append(".*");
                }
                escaping = false;
                break;
            case '?':
                if (escaping) {
                    sb.append("\\?");
                } else {
                    sb.append('.');
                }
                escaping = false;
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                sb.append('\\');
                sb.append(currentChar);
                escaping = false;
                break;
            case '\\':
                if (escaping) {
                    sb.append("\\\\");
                    escaping = false;
                } else {
                    escaping = true;
                }
                break;
            case '{':
                if (escaping) {
                    sb.append("\\{");
                } else {
                    sb.append('(');
                    inCurlies++;
                }
                escaping = false;
                break;
            case '}':
                if (inCurlies > 0 && !escaping) {
                    sb.append(')');
                    inCurlies--;
                } else if (escaping) {
                    sb.append("\\}");
                } else {
                    sb.append("}");
                }
                escaping = false;
                break;
            case ',':
                if (inCurlies > 0 && !escaping) {
                    sb.append('|');
                } else if (escaping) {
                    sb.append("\\,");
                } else {
                    sb.append(",");
                }
                break;
            default:
                escaping = false;
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

}
