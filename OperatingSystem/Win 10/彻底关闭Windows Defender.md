# 1.链接

[如何彻底关闭Win10内置的Windows Defender杀毒软件]: https://www.windows10.pro/disable-windows-defender/



# 2.方法

## 2.1 **安装其他安全软件即自动禁用Windows Defender**

## 2.2 **Win10专业版/企业版/教育版通过组策略关闭Windows Defender**

按 Win + R 快捷键调出“运行”对话框，输入“gpedit.msc”，确定，打开“本地组策略编辑器”，在左侧列表中定位至“计算机配置 - 管理模板 - Windows组件 - Windows Defender” 。

在右侧空格中找到“关闭 Windows Defender”配置项，双击该项打开配置窗口 。

选中“已启用”，确定。

这样就关闭了Windows Defender。

## 2.3**通过修改注册表关闭Windows Defender**

*注：本方法适用于所有Win10版本，尤其是数量众多的没有组策略配置工具的\**Win10家庭版**用户。* 

输入 regedit 并定位至：HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Microsoft\Windows Defender 

在右侧窗口中点击右键，选择“新建 - DWORD（32位）值”，并把新建的值命名为**DisableAntiSpyware**。 

双击DisableAntiSpyware值打开编辑窗口，把数值数据修改为 1 。 

重启系统后设置生效 .



以后想要重新启用Windows Defender的话，把DisableAntiSpyware的数值数据修改为 0 ，或直接删除该值即可 s