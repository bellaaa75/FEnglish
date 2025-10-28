package org.example.fenglish.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DataSource dataSource;

    // 数据库连接测试
    @GetMapping("/db-connection")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ 数据库连接成功！\n" +
                    "📊 数据库: " + conn.getMetaData().getDatabaseProductName() + "\n" +
                    "🔗 URL: " + conn.getMetaData().getURL();
        } catch (SQLException e) {
            return "❌ 数据库连接失败: " + e.getMessage();
        }
    }

    // 其他测试方法...
}