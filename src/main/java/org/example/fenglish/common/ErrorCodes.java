package org.example.fenglish.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常枚举类
 */

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    /*---- 用户 ----*/

    USER_NOT_FOUND(1001, "用户不存在"),
    DUPLICATE_USER(1004, "用户名已注册"),
    PASSWORD_MISMATCH(1010, "密码错误"),
    ACCOUNT_BANNED(1011, "账号已被禁用"),
    ACCOUNT_NOT_ACTIVATED(1012, "账号未激活"),
    OLD_PASSWORD_WRONG(1013, "原密码输入错误"),

    /*---- 单词书 & 单词 ----*/

    BOOK_NOT_FOUND(1003, "单词书不存在"),
    WORD_NOT_FOUND(1002, "单词不存在"),
    BOOK_NAME_DUPLICATE(1020, "单词书名称已存在"),
    WORD_ALREADY_IN_BOOK(1021, "该单词已存在于本书"),
    CANNOT_DELETE_BOOK_WITH_WORDS(1022, "单词书内仍存在单词，无法删除"),

    /*---- 学习记录 ----*/

    LEARN_RECORD_EXIST(1030, "学习记录已存在"),
    LEARN_RECORD_NOT_FOUND(1031, "学习记录不存在"),
    CANNOT_REVIEW_UNLEARNED(1032, "未学单词不能复习"),
    PROFICIENCY_NOT_CHANGED(1033, "掌握程度未变化"),

    /*---- 收藏 ----*/

    COLLECT_NOT_FOUND(1005, "收藏记录不存在"),
    ALREADY_COLLECTED(1040, "已收藏，无需重复收藏"),
    COLLECT_LIMIT_REACHED(1041, "个人收藏数量已达上限"),
    /*---- 新增：收藏对象类型非法 ----*/
    INVALID_TARGET_TYPE(1042, "收藏对象类型只能是 1-单词 或 2-单词书");
    private final int code;
    private final String msg;
}