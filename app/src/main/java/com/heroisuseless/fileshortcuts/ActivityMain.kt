package com.heroisuseless.fileshortcuts

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.os.Build
import java.io.File
import java.io.FileReader
// 添加一个新的文件类型分四步
// 向mainfest中添加
// 添加图片资源
// 在getImgRes中添加
// 在getfileType中添加
class ActivityMain : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Intent.ACTION_VIEW.equals(intent.action)) { // 是，则创建快捷方式
            val uri = intent.data
            val filePath = Uri.decode(uri!!.encodedPath)
            createShortCuts(filePath)
        }
        else{                                           // 否，则打开快捷方式
            var filePath: String? = intent.getStringExtra("filePath")
            if(filePath != null){
                openFileByThirdAPP(filePath)
            }
        }
    }
//-----------------------------------------------------------------------------------------------------
    fun createShortCuts(filePath: String) {
        var fileName: String = filePath.split('/').last() // 文件名
        var fileType: String = fileName.split('.').last() // 文件类型
        val shortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT")// 安装的Intent
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, fileName)// 快捷名称
        // 快捷图标
        val iconRes = Intent.ShortcutIconResource.fromContext(this, getImgRes(fileType))
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
        shortcut.putExtra("duplicate", true)// 快捷图标不允许重复
        val shortcutIntent = Intent(Intent.ACTION_MAIN)
        shortcutIntent.putExtra("filePath", filePath)
        shortcutIntent.setClass(this, ActivityMain::class.java)
        shortcutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        this.sendBroadcast(shortcut)
        Toast.makeText(this, "生成"+fileName, Toast.LENGTH_SHORT).show() // 提示生成成功
    }

    fun openFileByThirdAPP(filePath: String) {
        var fileName: String = filePath.split('/').last() // 文件名
        var fileType: String = fileName.split('.').last() // 文件类型
        if(File(filePath).exists()){ // 首先判断文件存不存在
            val intent = Intent(Intent.ACTION_VIEW)
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build()) // 严格模式
            }
            val data = Uri.fromFile(File(filePath))
            intent.setDataAndType(data, getFileType(fileType))
            this.startActivity(intent) // 开启第三方程序打开文件
        }else{//如果不存在就删除这个快捷方式，在小米系统上不管用，在三星上可以移除
            Toast.makeText(this, "文件已不存在", Toast.LENGTH_SHORT).show()
            val intent = Intent("com.android.launcher.action.UNINSTALL_SHORTCUT")
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, fileName)// 名字
            val launcherIntent = Intent(this, ActivityMain::class.java).setAction(Intent.ACTION_MAIN) // 设置关联程序
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent)
            sendBroadcast(intent)// 发送广播
        }
    }

    fun getImgRes(fileType: String): Int {
        val map = HashMap<String, Int>()
        map.put("txt", R.drawable.txt)
        map.put("doc", R.drawable.doc)
        map.put("docx", R.drawable.doc)
        map.put("ppt", R.drawable.ppt)
        map.put("xls", R.drawable.xls)
        map.put("html", R.drawable.html)
        map.put("htm", R.drawable.html)
        map.put("xml", R.drawable.xml)
        map.put("css", R.drawable.css)
        map.put("pdf", R.drawable.pdf)
        map.put("gif", R.drawable.gif)
        map.put("png", R.drawable.png)
        map.put("jpg", R.drawable.jpg)
        map.put("jpeg", R.drawable.jpg)
        map.put("bmp", R.drawable.bmp)
        map.put("mp3", R.drawable.mp3)
        map.put("mp4", R.drawable.mp4)
        map.put("ogg", R.drawable.ogg)
        map.put("avi", R.drawable.avi)
        map.put("rar", R.drawable.rar)
        map.put("zip", R.drawable.zip)
        map.put("7z", R.drawable.z7)
        //容错处理
        if(fileType.toLowerCase() in map.keys){
            return map[fileType.toLowerCase()]!!
        }
        else{
            return R.drawable.file
        }
    }

    fun getFileType(fileType: String): String? {
        val map = HashMap<String, String>()
        map.put("txt", "text/html")
        map.put("doc", "application/msword")
        map.put("docx", "application/msword")
        map.put("ppt", "application/mspowerpoint")
        map.put("xls", "application/excel")
        map.put("html", "text/html")
        map.put("htm", "text/html")
        map.put("xml", "text/html")
        map.put("pdf","application/pdf")
        map.put("gif", "image/gif")
        map.put("jpg", "image/jpg")
        map.put("jpeg", "image/jpeg")
        map.put("png", "image/png")
        map.put("mp3", "audio/mpeg")
        map.put("ogg", "audio/ogg")
        map.put("mp4", "vedio/mp4")
        map.put("zip", "application/zip")
        map.put("rar", "application/rar")
        map.put("7z", "application/7z")
        // 容错处理
        if(fileType.toLowerCase() in map.keys){
            return map[fileType.toLowerCase()]
        }
        else{
            return "*/*"
        }
    }

    override fun onResume() {
        super.onResume()
        this.finish()
    }

}
