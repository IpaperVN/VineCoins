package me.ipapervn.vinecoins.utils;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class PermissionUtils {
    // Định nghĩa các Permission dưới dạng hằng số (String) để dùng trong hasPermission()
    public static final String ADMIN = "vinecoins.admin";
    public static final String USE = "vinecoins.use";
    public static final String RELOAD = "vinecoins.reload";

    /**
     * Hàm này dùng để đăng ký quyền vào hệ thống của Bukkit
     * (Thay thế cho việc khai báo trong plugin.yml)
     */
    public static void registerAll() {
        registerPerm(ADMIN, "Quyền quản trị cao nhất của VineCoins", PermissionDefault.OP);
        registerPerm(USE, "Quyền xem số dư cơ bản", PermissionDefault.TRUE);
        registerPerm(RELOAD, "Quyền nạp lại cấu hình plugin", PermissionDefault.OP);
    }

    private static void registerPerm(String name, String description, PermissionDefault defaultValue) {
        // Kiểm tra xem quyền đã tồn tại chưa để tránh lỗi khi reload
        if (org.bukkit.Bukkit.getPluginManager().getPermission(name) == null) {
            Permission perm = new Permission(name, description, defaultValue);
            org.bukkit.Bukkit.getPluginManager().addPermission(perm);
        }
    }
}
