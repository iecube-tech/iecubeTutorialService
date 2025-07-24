package com.iecube.iecubetutorial.model.htmlEditAi.enums;

public enum MessageType {
    user, ai, error, current, stream, stream_start, stream_end, complete
    // 用于ProjectMessage 的type  当 type 为 user 或 ai 时 Content的内容为json对象
}
