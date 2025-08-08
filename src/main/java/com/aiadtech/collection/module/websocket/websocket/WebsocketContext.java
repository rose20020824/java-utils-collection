package com.aiadtech.collection.module.websocket.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket房间管理（会话隔离）
 */
@Component
public class WebsocketContext {

    /**
     * 路由房间组
     * key:RoutePath -> value:Rooms
     */
    private static final Map<String, Rooms> routeRooms = new HashMap<>();

    /**
     * 初始化路由房间组
     */
    public static void initRouteRooms(String routePath) {
        routeRooms.put(routePath, new Rooms());
    }

    /**
     * 获取指定路由下的房间列表
     */
    public Rooms getRooms(String route) {
        return routeRooms.get(route);
    }

    /**
     * 通过路由和房间ID获取指定房间
     */
    public Room findRoom(String route, String roomId) {
        return Optional.ofNullable(getRooms(route)).map(r -> r.find(roomId)).orElse(null);
    }

    /**
     * 通过路由和Channel获取指定房间
     */
    public Room findRoom(String route, Channel channel) {
        return Optional.ofNullable(getRooms(route)).map(r -> r.find(channel)).orElse(null);
    }

    /**
     * 离开房间
     */
    public void leaveRoom(String route, Channel channel) {
        getRooms(route).leave(channel);
    }

    /**
     * 创建房间
     */
    public void createRoom(String route, String roomId, Channel channel) {
        getRooms(route).putIfAbsent(roomId, channel);
    }

    /**
     * 生成房间ID，有规律的房间ID有利于快速查找，方便内部通讯
     */
    public static String roomId(String source, String customerId) {
        return source + "-" + customerId;
    }

    public static class Rooms {

        /**
         * 房间列表
         * key: room.id
         * value: Room对象 {@link Room}
         */
        private final Map<String, Room> roomIdMap = new ConcurrentHashMap<>();

        /**
         * 房间列表，一个房间包含多个channel，任意channel都可以查找到对应的房间
         * key: channel
         * value: Room对象 {@link Room}
         */
        private final Map<Channel, Room> roomChannelMap = new ConcurrentHashMap<>();

        /**
         * 通过房间ID查找
         */
        Room find(String roomId) {
            return roomIdMap.get(roomId);
        }

        /**
         * 通过channel查找
         */
        Room find(Channel channel) {
            return roomChannelMap.get(channel);
        }

        void putIfAbsent(String roomId, Channel channel) {
            // 基于 ConcurrentHashMap 原子操作，返回 null 表示已存在，直接get出来即可
            var room = roomIdMap.putIfAbsent(roomId, new Room());
            if (room == null) {
                room = roomIdMap.get(roomId);
            }

            room.id = roomId;
            room.channelGroup.add(channel);
            roomChannelMap.put(channel, room);
        }

        void leave(Channel channel) {
            var room = roomChannelMap.get(channel);
            if (room != null && room.channelGroup.isEmpty()) {
                roomIdMap.remove(room.id);
            }
            roomChannelMap.remove(channel);
        }
    }

    @Getter
    public static class Room {

        /**
         * 房间ID（唯一标识）
         */
        private String id;

        /**
         * Channel组，表示该房间内的所有客户端
         */
        private final DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    }
}
