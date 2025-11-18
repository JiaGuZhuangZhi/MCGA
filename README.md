# 🚀 MCGA - Make Color Great Again!

<p align="center">
  <img src="./icon.svg" alt="MCGA Logo" width="128" />
</p>

<p align="center">
  <b>一款为 ColorOS 用户打造的定制化 Lsposed 模块</b><br/>
  隐藏推荐应用名称 · 调整卡片高度 · 精细化 UI 控制 · 无广告 · 轻量 · 开源
</p>

<p align="center">
  <a href="https://github.com/JiaGuZhuangZhi/MCGA/stargazers">
    <img src="https://img.shields.io/github/stars/JiaGuZhuangZhi/MCGA?style=flat&logo=github&color=gold" alt="Stars" />
  </a>
  <a href="https://github.com/JiaGuZhuangZhi/MCGA/network/members">
    <img src="https://img.shields.io/github/forks/JiaGuZhuangZhi/MCGA?style=flat&logo=github&color=blue" alt="Forks" />
  </a>
  <a href="https://github.com/JiaGuZhuangZhi/MCGA/releases">
    <img src="https://img.shields.io/github/downloads/JiaGuZhuangZhi/MCGA/total?color=green" alt="Downloads" />
  </a>
  <a href="https://github.com/JiaGuZhuangZhi/MCGA/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/JiaGuZhuangZhi/MCGA?color=purple" alt="License" />
  </a>
</p>

---

## 📦 下载方式

你可以通过以下任一方式获取 MCGA 模块：

- **GitHub Releases（推荐）**  
  👉 [https://github.com/JiaGuZhuangZhi/MCGA/releases](https://github.com/JiaGuZhuangZhi/MCGA/releases)

- **第三方分发链接（提取码 MCGA）**  
  👉 [https://www.123865.com/s/qQ9uVv-Eugo?pwd=MCGA](https://www.123865.com/s/qQ9uVv-Eugo?pwd=MCGA)

> 💡 建议优先使用 GitHub Releases，确保文件完整性与安全性。

---

## ✨ 功能特性

- **主要**
  - 系统界面
    1. 背景覆盖颜色
    2. 背景模糊半径
    3. 背景圆角半径
  - 系统桌面
    1. 强制启用 Dock 栏模糊
    2. 隐藏“抽屉全部页”应用名称
- **更多**
  - 全局搜索
    1. 隐藏“应用建议”中的应用名称
    2. 修正“应用建议”卡片的高度

---

## 🛠️ 技术栈

本模块基于以下开源技术构建：

```kotlin
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.recyclerview)
implementation(libs.androidx.compose)
implementation(libs.kyant0.capsule)
implementation(libs.compose.colorpicker)
compileOnly(libs.xposed.api)
```

### 核心依赖说明

| 依赖           | 用途         |
|--------------|------------|
| `Xposed API` | 提供 Hook 能力 |
| `Compose`    | 用于 UI      |
| `Capsule`    | 平滑圆角       |

> 📌 当前版本为纯代码配置型模块，无需图形设置界面，重启生效。

---

## ⚠️ 安全须知与法律声明

### 🔒 安全说明
- 本模块**仅用于个人设备定制**，**不修改系统文件**，所有操作通过 Xposed Hook 实现。
- **不会收集、上传或共享任何用户数据**，包括应用列表、使用习惯、设备信息等。
- 模块代码完全开源，可自行编译验证。

### ⚖️ 法律声明
- 本项目**基于反编译分析 ColorOS 系统应用**实现功能，**仅用于学习与个人使用**。
- **禁止用于商业用途、二次分发牟利或破坏系统安全**。
- 使用本模块即表示你已阅读并同意：
    - 遵守所在国家/地区的法律法规。
    - **不向设备厂商（OPPO/一加/realme）投诉因本模块导致的问题**。
    - 项目涉及对系统 APK 的**逆向分析**，仅用于理解内部逻辑，**未包含任何反编译代码**。
    - **请勿用于商业用途**，亦不得用于破坏系统稳定性或侵犯他人设备安全。
    - 使用本模块可能导致系统 UI 异常、崩溃或升级失败。**请自行承担风险**。
    - 作者 **不承担** 因使用本模块导致的任何设备损坏、数据丢失或违反厂商保修条款的责任。

> 📌 **遵守当地法律法规。在某些国家或地区，修改系统行为可能违反服务条款或法律。请确保您有权对设备进行此类修改。**

> 📜 本项目 **不隶属于 OPPO、OnePlus、ColorOS 或任何商业实体**。

---

## 🙏 致谢

本项目站在巨人的肩膀上，特别感谢以下开源项目：

- [**LSPosed**](https://github.com/LSPosed/LSPosed) – 现代化 Xposed 实现
- [**Xposed Bridge**](https://github.com/rovo89/XposedBridge) – Hook 框架基石
- [**AndroidX**](https://developer.android.com/jetpack/androidx) – Jetpack 核心组件
- [**Capsule**](https://github.com/kyant0/Capsule) – 平滑圆角
- [**Compose ColorPicker**](https://github.com/SmartToolFactory/Compose-Colorful) – 颜色选择器

同时也感谢所有反编译工具（JADX）和开源社区的无私分享。

<div align="center"><a href="https://github.com/Safouene1/support-palestine-banner/blob/master/Markdown-pages/Support.md"><img src="https://raw.githubusercontent.com/Safouene1/support-palestine-banner/master/banner-support.svg" alt="Support Palestine" style="width: 100%;"></a></div>

---

## 📬 反馈与贡献

- 🐞 发现 Bug？ → [提交 Issue](https://github.com/JiaGuZhuangZhi/MCGA/issues)
- 💡 有新想法？ → 欢迎 PR！
- ❓ 使用问题？ → 请先查阅 [Wiki](https://github.com/JiaGuZhuangZhi/MCGA/wiki)（建设中）

> 🌟 如果你觉得 MCGA 好用，请点个 **Star**！这是对我最大的鼓励！

---

## 📄 许可证

本项目采用 [**Apache License 2.0**](LICENSE) 开源协议。

```
Copyright 2025 JiaGuZhuangZhi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

> Made with ❤️ by a 16-year-old high school student who just wants a cleaner home screen.  
> Keep coding, keep customizing!

---

> **Make Color Great Again.**  
> —— 致敬每一个不甘于默认界面的灵魂。

---