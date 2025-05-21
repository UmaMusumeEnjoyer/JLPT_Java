HƯỚNG DẪN CÀI ĐẶT VÀ SỬ DỤNG CHƯƠNG TRÌNH QUẢN LÝ ĐỀ THI JLPT
============================================================

1. YÊU CẦU HỆ THỐNG
-------------------
- Máy tính chạy Windows (khuyến nghị Windows 10 trở lên)
- Java JDK 17 hoặc mới hơn (https://adoptium.net/ hoặc https://www.oracle.com/java/technologies/downloads/)
- Đã cài đặt MySQL Server (nếu sử dụng chức năng kết nối CSDL)

2. CÀI ĐẶT FONT TIẾNG NHẬT
--------------------------
- Đảm bảo file font `NotoSansJP-VariableFont_wght.ttf` đã có trong thư mục `lib/` của dự án (đã kèm sẵn).
- Nếu bị lỗi font khi xuất PDF, kiểm tra lại file font này.

3. CHẠY CHƯƠNG TRÌNH
---------------------
- Mở terminal/cmd tại thư mục gốc dự án (chứa thư mục `src/` và `lib/`).
- Biên dịch toàn bộ mã nguồn:
  ```
  javac -cp "lib/*;src" -d build src/GUI/ExamKanri.java
  ```
- Chạy chương trình:
  ```
  java -cp "lib/*;build" GUI.ExamKanri
  ```
- Nếu dùng IDE (IntelliJ/Eclipse), add toàn bộ file JAR trong `lib/` vào project libraries.

4. SỬ DỤNG CHƯƠNG TRÌNH
------------------------
- Giao diện chính cho phép:
  + Thêm, sửa, xóa đề thi JLPT.
  + Xem chi tiết đề thi, danh sách câu hỏi.
  + Xuất đề thi ra file PDF hoặc DOC:
    * Khi xuất, chương trình sẽ hỏi nơi lưu file.
    * File PDF/DOC chỉ chứa câu hỏi và các lựa chọn trả lời.
    * Đáp án đúng sẽ được xuất ra file TXT riêng (ví dụ: `TenDe_AnswerKey.txt`).
- Đảm bảo không xóa file font trong thư mục `lib/` để PDF hiển thị tiếng Nhật chuẩn.

5. LỖI THƯỜNG GẶP
-----------------
- **Lỗi font khi xuất PDF:**
  + Kiểm tra file `lib/NotoSansJP-VariableFont_wght.ttf` có tồn tại không.
  + Không dùng font hệ thống dạng `.ttc`.
- **Không chạy được:**
  + Kiểm tra đã cài đúng Java JDK và đã add JAR trong `lib/` vào classpath.

6. LIÊN HỆ HỖ TRỢ
-----------------
- Nếu gặp lỗi không giải quyết được, liên hệ người phát triển qua email hoặc Zalo.

7. THAY ĐỔI API KEY (NẾU SỬ DỤNG CHỨC NĂNG AI/ĐỌC ẢNH)
-----------------------------
- Nếu chương trình có sử dụng API (ví dụ: Google Vision, OpenAI, Azure, v.v.) để nhận diện ảnh hoặc xử lý AI, bạn cần cấu hình lại API key.
- Thông thường, API key sẽ được lưu trong file Python `ai.py` hoặc các file trong thư mục `ai/`.
- Để thay đổi API key:
  1. Mở file `ai.py` (hoặc file Python liên quan) bằng Notepad hoặc IDE.
  2. Tìm dòng có chứa API key (ví dụ: `API_KEY = "..."`).
  3. Thay thế giá trị API key cũ bằng API key mới bạn nhận được từ nhà cung cấp dịch vụ (Google, OpenAI, Azure, ...).
  4. Lưu lại file.
  5. Mở terminal/cmd tại thư mục dự án và chạy lệnh sau để tạo lại file thực thi (.exe):
     ```
     python sech.py
     ```
     (Yêu cầu đã cài Python và các thư viện cần thiết, ví dụ: pyinstaller)
  6. Sau khi chạy xong, file `.exe` mới sẽ được tạo ra, bạn dùng file này để chạy chương trình với API key mới.
- Nếu gặp lỗi xác thực API, kiểm tra lại key đã đúng và còn hiệu lực không.

8. TẠO DATABASE MYSQL (CẤU TRÚC BẢNG)
-----------------------------
- Để sử dụng chương trình, bạn cần tạo database và các bảng theo cấu trúc dưới đây trong MySQL.
- Mở MySQL Workbench hoặc terminal, đăng nhập vào MySQL và chạy các lệnh sau:

```sql
CREATE DATABASE `jlpt` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `answers` (
  `AnswerID` int NOT NULL AUTO_INCREMENT,
  `QuestionID` int NOT NULL,
  `Content` text NOT NULL,
  `IsCorrect` tinyint(1) DEFAULT '0',
  `Order` int DEFAULT NULL,
  PRIMARY KEY (`AnswerID`),
  KEY `idx_answers_question` (`QuestionID`),
  CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`QuestionID`) REFERENCES `questions` (`QuestionID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `examquestions` (
  `ExamID` int NOT NULL,
  `QuestionID` int NOT NULL,
  `Order` int DEFAULT NULL,
  `Score` float DEFAULT NULL,
  PRIMARY KEY (`ExamID`,`QuestionID`),
  KEY `idx_examquestions_exam` (`ExamID`),
  KEY `idx_examquestions_question` (`QuestionID`),
  CONSTRAINT `examquestions_ibfk_1` FOREIGN KEY (`ExamID`) REFERENCES `exams` (`ExamID`) ON DELETE CASCADE,
  CONSTRAINT `examquestions_ibfk_2` FOREIGN KEY (`QuestionID`) REFERENCES `questions` (`QuestionID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `exams` (
  `ExamID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(255) NOT NULL,
  `Level` varchar(50) DEFAULT NULL,
  `CreateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ExamID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `questions` (
  `QuestionID` int NOT NULL AUTO_INCREMENT,
  `Content` text NOT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `Level` varchar(10) DEFAULT NULL,
  `SoundUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`QuestionID`),
  KEY `idx_questions_level` (`Level`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

- Sau khi chạy xong các lệnh trên, database và các bảng sẽ sẵn sàng để sử dụng với chương trình.

==============================
日本語でのご利用ガイド
==============================

1. システム要件
-------------------
- Windowsパソコン（Windows 10以上推奨）
- Java JDK 17以上（https://adoptium.net/ または https://www.oracle.com/java/technologies/downloads/）
- MySQL Server（データベース機能を使う場合）

2. 日本語フォントの設定
--------------------------
- プロジェクトの `lib/` フォルダに `NotoSansJP-VariableFont_wght.ttf` フォントファイルがあることを確認してください（同梱済み）。
- PDF出力時にフォントエラーが出る場合、このファイルを再確認してください。

3. プログラムの起動
---------------------
- プロジェクトのルート（`src/` と `lib/` がある場所）でターミナル/コマンドプロンプトを開きます。
- ソースコードをコンパイル：
  ```
  javac -cp "lib/*;src" -d build src/GUI/ExamKanri.java
  ```
- プログラムを実行：
  ```
  java -cp "lib/*;build" GUI.ExamKanri
  ```
- IDE（IntelliJ/Eclipse等）を使う場合は、`lib/` 内のJARファイルを全てプロジェクトに追加してください。

4. プログラムの使い方
------------------------
- メイン画面で：
  + JLPT試験の追加・編集・削除
  + 試験の詳細・問題リストの表示
  + PDFまたはDOC形式で試験を出力：
    * 出力時に保存先を選択できます。
    * PDF/DOCファイルには問題文と選択肢のみが含まれます。
    * 正解は別のTXTファイル（例：`TenDe_AnswerKey.txt`）に出力されます。
- PDFで日本語が正しく表示されるよう、`lib/` フォルダのフォントファイルを削除しないでください。

5. よくあるエラー
-----------------
- **PDF出力時のフォントエラー：**
  + `lib/NotoSansJP-VariableFont_wght.ttf` ファイルが存在するか確認してください。
  + システム標準の `.ttc` フォントは使わないでください。
- **起動できない場合：**
  + Java JDKが正しくインストールされているか、`lib/` のJARがクラスパスに追加されているか確認してください。

6. サポート連絡先
-----------------
- 解決できないエラーが発生した場合は、開発者までメールまたはZaloでご連絡ください。

7. APIキーの変更（AI/画像認識機能を使う場合）
-----------------------------
- プログラムがAPI（Google Vision, OpenAI, Azure等）を使う場合、APIキーの再設定が必要です。
- 通常、APIキーはPythonファイル（`ai.py`等）や設定ファイルに保存されています。
- APIキーの変更手順：
  1. `ai.py` などのPythonファイルをエディタで開く
  2. APIキーの行（例：`API_KEY = "..."`）を探す
  3. 新しいAPIキーに書き換えて保存
  4. コマンドプロンプトで `python sech.py` を実行し、新しい `.exe` ファイルを作成
  5. 生成された `.exe` で新しいAPIキーが反映されます
- API認証エラーが出る場合は、キーが正しいか・有効か再確認してください。

8. TẠO DATABASE MYSQL (CẤU TRÚC BẢNG)
-----------------------------
- Để sử dụng chương trình, bạn cần tạo database và các bảng theo cấu trúc dưới đây trong MySQL.
- Mở MySQL Workbench hoặc terminal, đăng nhập vào MySQL và chạy các lệnh sau:

```sql
CREATE DATABASE `jlpt` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `answers` (
  `AnswerID` int NOT NULL AUTO_INCREMENT,
  `QuestionID` int NOT NULL,
  `Content` text NOT NULL,
  `IsCorrect` tinyint(1) DEFAULT '0',
  `Order` int DEFAULT NULL,
  PRIMARY KEY (`AnswerID`),
  KEY `idx_answers_question` (`QuestionID`),
  CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`QuestionID`) REFERENCES `questions` (`QuestionID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `examquestions` (
  `ExamID` int NOT NULL,
  `QuestionID` int NOT NULL,
  `Order` int DEFAULT NULL,
  `Score` float DEFAULT NULL,
  PRIMARY KEY (`ExamID`,`QuestionID`),
  KEY `idx_examquestions_exam` (`ExamID`),
  KEY `idx_examquestions_question` (`QuestionID`),
  CONSTRAINT `examquestions_ibfk_1` FOREIGN KEY (`ExamID`) REFERENCES `exams` (`ExamID`) ON DELETE CASCADE,
  CONSTRAINT `examquestions_ibfk_2` FOREIGN KEY (`QuestionID`) REFERENCES `questions` (`QuestionID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `exams` (
  `ExamID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(255) NOT NULL,
  `Level` varchar(50) DEFAULT NULL,
  `CreateDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ExamID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `questions` (
  `QuestionID` int NOT NULL AUTO_INCREMENT,
  `Content` text NOT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `Level` varchar(10) DEFAULT NULL,
  `SoundUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`QuestionID`),
  KEY `idx_questions_level` (`Level`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

- Sau khi chạy xong các lệnh trên, database và các bảng sẽ sẵn sàng để sử dụng với chương trình.
