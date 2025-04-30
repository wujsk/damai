package com.damai.servicelock;


import lombok.Getter;

/**
 * @author: haonan
 * @description: 锁类型
 */
public enum LockType {

    /**
     * 可重入锁
     */
    Reentrant("Reentrant"),

    /**
     * 公平锁
     */
    Fair("Fair"),

    /**
     * 读锁
     */
    Read("Read"),

    /**
     * 写锁
     */
    Write("Write");

    @Getter
    private final String type;

    LockType(String type) {
        this.type = type;
    }

    public static boolean isInLockType(String type) {
        for (LockType lockType : LockType.values()) {
            if (lockType.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static LockType getLockType(String type) {
        for (LockType lockType : LockType.values()) {
            if (lockType.getType().equals(type)) {
                return lockType;
            }
        }
        return null;
    }
}
