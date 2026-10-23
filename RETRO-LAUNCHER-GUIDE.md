# Hướng dẫn Launcher Retro Việt Nam (Lịch Bloc, Giá Vàng & 4 Chủ Đề)

Dự án mới **Retro Vietnam TV Launcher** (`launcher-vietnam/`) đã được tạo độc lập hoàn toàn, **không can thiệp hay ghi đè lên Launcher Apple TV cũ** (`launcher/`).

---

## 1. Các tính năng nổi bật theo hình mẫu bạn gửi

1. **Tờ lịch Bloc xé tường truyền thống:**
   - Đầu lịch đỏ có 2 lỗ xỏ lịch và dòng chữ tháng năm tiếng Việt.
   - Số ngày cực lớn màu đỏ nổi bật.
   - Thứ trong tuần dạng chữ lớn trang trọng.
   - Phần đáy lịch hiển thị ngày tháng **Âm lịch** và **Can Chi** đầy đủ (Năm Bính Ngọ, Tháng Mậu Tuất, Ngày Canh Ngọ...) tính toán tự động theo thuật toán Lịch thiên văn Việt Nam.
2. **Đồng hồ số thời gian thực:**
   - Chạy từng giây `HH:mm:ss` với lời chào tùy chỉnh ở phía trên.
3. **Bảng Lịch tháng thu nhỏ:**
   - Hiển thị ngày dương kèm ngày âm nhỏ bên dưới góc của từng ô ngày.
   - Ngày hôm nay được khoanh đỏ nổi bật.
4. **Widget Giá Vàng & Xăng Dầu:**
   - Bảng giá vàng: Vàng miếng SJC và Nhẫn 999.9.
   - Bảng giá xăng dầu: Xăng E10, Xăng E5, Dầu DO.
5. **Dãy ứng dụng phong cách Tem thư (Postage Stamp):**
   - Viền răng cưa đục lỗ tem thư cổ điển độc đáo.
   - Hỗ trợ đầy đủ phím D-pad của remote TV (Trái, Phải, OK để mở app).
6. **Popup "Chọn giao diện" (4 chủ đề):**
   - Bấm vào biểu tượng bảng màu (hoặc bấm lên góc phải) để mở popup chọn giao diện:
     1. **Retro Việt Nam:** Nền giấy báo cũ cổ điển, lịch bloc đỏ trắng, tem thư truyền thống.
     2. **Bìa tạp chí:** Phong cách tạp chí Editorial thời thượng, font Serif nghệ thuật, khối màu vàng - đỏ - xanh.
     3. **Pastel tươi vui:** Tone màu xanh mint pastel nhẹ nhàng, hiện đại.
     4. **Sơn mài:** Nền đen bóng sơn mài truyền thống với viền nhũ vàng kim sang trọng.
   - Ghi nhớ chủ đề đã chọn sau khi tắt TV mở lại.

---

## 2. Cách nạp Launcher mới lên TV

1. Bật TV lên và đảm bảo TV kết nối cùng mạng Wi-Fi với máy tính.
2. Nếu trước đó bạn đã tắt USB Debugging, hãy vào:
   - **Cài đặt** trên TV → **Tùy chọn nhà phát triển** → Bật lại **USB debugging**.
3. Chạy lệnh cài đặt 1 chạm:
   ```bash
   ./install-retro.sh
   ```
   *(Nếu IP của TV thay đổi, bạn có thể truyền IP trực tiếp: `TV_HOST=192.168.1.xxx ./install-retro.sh`)*.

---

## 3. Cách chuyển đổi qua lại giữa 2 Launcher

Vì 2 launcher là 2 ứng dụng độc lập trên TV, bạn có thể chuyển qua lại bất kỳ lúc nào:
- **Chuyển sang Launcher Retro Việt Nam:**
  ```bash
  ./install-retro.sh
  ```
- **Chuyển về Launcher Apple TV tối giản cũ:**
  ```bash
  ./switch-to-apple-launcher.sh
  ```
