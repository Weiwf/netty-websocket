package com.wei.demo.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话管理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    private String userId;

    private String userName;
}
