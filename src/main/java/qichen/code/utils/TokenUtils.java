package qichen.code.utils;

import java.util.UUID;

public class TokenUtils {

    public static String getTokenId() {
        // 先取系统中存在的TokenId，如果不存在，则创建
        return UUID.randomUUID().toString();
    }

}
