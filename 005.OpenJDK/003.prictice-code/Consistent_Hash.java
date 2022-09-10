package link.bosswang.wei;

import java.util.SortedMap;
import java.util.TreeMap;

public class Consistent_Hash {

    private static TreeMap<Long, String> Consistent_Hash = new TreeMap<>();

    private static long hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static String getServerName(Long hash) {
        if (Consistent_Hash.isEmpty()) {
            return null;
        }

        // 返回此地图部分的视图，其键大于等于 fromKey
        SortedMap<Long, String> tailMap = Consistent_Hash.tailMap(hash);

        // 如果为空，则表示已经到了Hash环的末尾，那么需要使用第一个Key
        if (!tailMap.isEmpty()) {
            return tailMap.get(tailMap.firstKey());
        }

        return Consistent_Hash.firstEntry().getValue();
    }


    public static void main(String[] args) {
        // 初始化服务器 2^n
        for (long i = 0; i < 8; i++) {
            Consistent_Hash.put(i, "Server-" + i);
        }

        System.out.println(getServerName((hash(4) & (8 - 1))));
        System.out.println(getServerName(hash(5) & (8 - 1)));
        System.out.println(getServerName(hash(6) & (8 - 1)));

    }
}
