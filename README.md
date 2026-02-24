# 💰 VineCoins

> **Hệ thống tiền tệ kép chuyên nghiệp cho Minecraft Server**

[![Version](https://img.shields.io/badge/version-1.0-brightgreen.svg)](https://github.com/ipapervn/vinecoins)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-blue.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)

---

## 📋 Giới thiệu

**VineCoins** là plugin quản lý tiền tệ mạnh mẽ với 2 loại tiền:
- **Coin** - Tiền tệ chính
- **MCoin** - Tiền tệ cao cấp

Plugin được thiết kế tối ưu cho các server Skyblock và RPG với khả năng mở rộng cao.

---

## ✨ Tính năng

### 🎯 Hệ thống tiền tệ kép
- ✅ Quản lý 2 loại tiền độc lập (Coin & MCoin)
- ✅ Hỗ trợ số dư lớn với độ chính xác cao
- ✅ API đơn giản cho developer

### 💾 Lưu trữ linh hoạt
- ✅ **YAML** - Lưu trữ file (mặc định)
- ✅ **MySQL** - Database với HikariCP connection pool
- ✅ Auto-save mỗi 10 phút
- ✅ Lưu khi người chơi thoát server

### 🎮 Tính năng game
- ✅ **Mob Rewards** - Nhận tiền khi giết quái
- ✅ Tỉ lệ rơi tiền tùy chỉnh
- ✅ Action bar thông báo nhận tiền
- ✅ Hỗ trợ tất cả loại mob

### 🔧 Quản lý admin
- ✅ Thêm/Trừ/Set tiền cho người chơi
- ✅ Reset tiền cá nhân hoặc toàn server
- ✅ Kiểm tra số dư người chơi
- ✅ Reload config không cần restart

### 🎨 PlaceholderAPI
- ✅ Hiển thị số dư trên scoreboard, tab, chat
- ✅ Nhiều định dạng: raw, formatted, smart, fixed
- ✅ Tích hợp dễ dàng với các plugin khác

---

## 📦 Cài đặt

1. Tải file `VineCoins.jar`
2. Đặt vào thư mục `plugins/`
3. Khởi động lại server
4. Cấu hình trong `plugins/VineCoins/config.yml`

### Yêu cầu
- **Minecraft**: 1.21+
- **Java**: 21+
- **PlaceholderAPI**: 2.11.5+ (tùy chọn)

---

## ⚙️ Cấu hình

### config.yml
```yaml
storage-type: "YAML" # Hoặc "MYSQL"

database:
  host: "localhost"
  port: 3306
  database: "minecraft"
  username: "root"
  password: ""

mob-rewards:
  enabled: true
  rewards:
    ZOMBIE:
      amount: 1.5
      chance: 50.0
    SKELETON:
      amount: 2.0
      chance: 30.0
    CREEPER:
      amount: 5.0
      chance: 10.0
```

---

## 📝 Lệnh

| Lệnh                                  | Mô tả                      | Permission          |
|---------------------------------------|----------------------------|---------------------|
| `/vc help`                            | Hiển thị trợ giúp          | -                   |
| `/vc <coin/mcoin> check [player]`     | Xem số dư                  | `vinecoins.check`   |
| `/vc <coin/mcoin> add <player> <amount>` | Cộng tiền               | `vinecoins.admin`   |
| `/vc <coin/mcoin> set <player> <amount>` | Set tiền                | `vinecoins.admin`   |
| `/vc <coin/mcoin> take <player> <amount>` | Trừ tiền               | `vinecoins.admin`   |
| `/vc reset <player> [coin/mcoin]`     | Reset tiền 1 người         | `vinecoins.admin`   |
| `/vc resetall [coin/mcoin]`           | Reset tiền toàn server     | `vinecoins.admin`   |
| `/vc reload`                          | Reload plugin              | `vinecoins.reload`  |

**Aliases**: `/vcoins`, `/vc`

---

## 🎯 PlaceholderAPI

### Coin Placeholders
```
%vinecoins_coin%           → 1234567
%vinecoins_coin_formatted% → 1,234,567
%vinecoins_coin_smart%     → 1.2M
%vinecoins_coin_fixed%     → 1234567.0
%vinecoins_coin_raw%       → 1234567.89
```

### MCoin Placeholders
```
%vinecoins_mcoin%           → 1234567
%vinecoins_mcoin_formatted% → 1,234,567
%vinecoins_mcoin_smart%     → 1.2M
%vinecoins_mcoin_fixed%     → 1234567.0
%vinecoins_mcoin_raw%       → 1234567.89
```

---

## 🎨 Screenshots

### In-game Commands
```
/vc coin check iPaperVN
→ Số dư Coin của người chơi iPaperVN là: 1,234,567

/vc mcoin add iPaperVN 100
→ Bạn đã cộng 100 MCoin cho iPaperVN.
```

### Mob Kill Rewards
```
[Action Bar] + 5.0 MCoin (Giết CREEPER)
```

---

## 🛠️ Hỗ trợ

- **Discord**: [iStudioVN](https://discord.gg/istudiovn)
- **GitHub**: [Issues](https://github.com/ipapervn/vinecoins/issues)
- **Email**: support@istudiovn.com

---

## 📄 License

Copyright © 2024 iPaperVN - iStudioVN

---

## 🌟 Credits

**Developed by**: iPaperVN & iStudioVN  
**For**: VineSkyblock Project  
**Version**: 1.0

---

<div align="center">
  <sub>Made with ❤️ for Minecraft Community</sub>
</div>
