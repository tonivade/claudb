/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static java.util.stream.Collectors.toSet;
import static tonivade.db.redis.SafeString.safeString;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@Command("keys")
@ParamLength(1)
public class KeysCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        Pattern pattern = createPattern(request.getParam(0));
        Predicate<? super String> predicate = (key) -> {
            return pattern.matcher(key).matches();
        };
        Set<SafeString> keys = db.keySet().stream().filter(predicate).map((item) -> safeString(item)).collect(toSet());
        response.addArray(keys);
    }

    private Pattern createPattern(String param) {
        return Pattern.compile(convertGlobToRegEx(param));
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
