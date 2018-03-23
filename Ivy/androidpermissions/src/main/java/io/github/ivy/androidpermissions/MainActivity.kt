package io.github.ivy.androidpermissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * android 运行是权限 过程
 * 1、第一次请求权限，会弹出用户选择的 dialog，如果成功则表示 granted，下次可直接进行逻辑操作，否则进入第二步
 * 2、第二次请求权限，这时候在弹出的 dialog 界面会出现不再询问的选项，如果用户未勾选不再询问，同第一步
 * 3、如果用户勾选了不再询问且选择的是 denied 的话，接下来继续请求权限将不再弹出 dialog，直接就是 denied
 * 4、如果拒绝了权限，且未勾选不再询问，则ActivityCompat.shouldShowRequestPermissionRationale 此方法将为 true，只要勾选了不再询问，则为 false
 *
 * 关于权限组
 * 只能说并不是所有的同组的权限有一个同意了，其他的就不需要重新授权，比如
 * 应用一开始只有获取了 READ_EXTERNAL_STORAGE ，并且 granted 了
 * 此时增加一个 WRITE_EXTERNAL_STORAGE ，他们虽然是同一个权限组，但是通过判断 WRITE_EXTERNAL_STORAGE 并没有被授权
 * 而如果
 * 应用一开始只获取了 WRITE_EXTERNAL_STORAGE 权限，并且 granted 了
 * 此时增加一个 READ_EXTERNAL_STORAGE 权限，这时却不需要授权
 * 所以，权限组应该有等级之分，如果有了高等级的权限，同组内地等级的权限就不需要再次授权；如果有了低等级的权限，增加高等级的权限时，还是需要授权
 *
 */
class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_hello_world.setOnClickListener {
                        requestSingle()
//            requestMultiple()
//            requestEach()
        }

        easyPermissions()
    }

    private fun easyPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(TAG, "已经有了写权限")
        } else {
            Log.i(TAG, "还没有写权限")
//            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun requestSingle() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        RxPermissions(this)
                .request(locationPermission)
                .subscribe {
                    if (it) {//同意权限
                        Toast.makeText(this, "定位权限获取成功", Toast.LENGTH_SHORT).show()
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, locationPermission)) {
                        //拒绝权限，没有勾选不再询问，如果是第一次请求，是没有不再询问可以勾选的
                        Toast.makeText(this, "你拒绝了定位权限", Toast.LENGTH_SHORT).show()
                    } else {
                        //拒绝权限，如果没有上面的判断，不管勾选没勾选不再询问，都会走这里；如果有了上面的判断，只有在勾选了不再询问才会走这里
                        Toast.makeText(this, "定位权限获取失败", Toast.LENGTH_SHORT).show()
                        AppSettingsDialog.Builder(this).build().show()
                    }
                }

    }

    /**
     * 只要有一个失败，回调里头的判断就是失败的，此时只能手动去判断哪个权限是否成功
     */
    private fun requestMultiple() {
        //定位权限必须有
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        RxPermissions(this)
                .request(Manifest.permission.CAMERA, locationPermission, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {//同意了所有权限
                        Log.i(TAG, "所有权限获取成功")
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, locationPermission)) {
                        Log.i(TAG, "拒绝了定位权限，没有勾选不再询问")
                        requestLocationPermission()
                    } else if (ActivityCompat.checkSelfPermission(this@MainActivity, locationPermission) == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "定位权限获取了，你想干嘛")
                    } else if (ActivityCompat.checkSelfPermission(this@MainActivity, locationPermission) == PackageManager.PERMISSION_DENIED) {
                        Log.i(TAG, "定位权限获取失败，去设置")
                        AppSettingsDialog.Builder(this).build().show()
                    } else {
                        Log.i(TAG, "拒绝了部分权限或者全部权限")
                    }
                }
    }

    private fun requestEach() {
        //定位权限必须有
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        RxPermissions(this)
                .requestEach(locationPermission, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it.name == locationPermission) {
                        if (it.granted) {
                            Log.i(TAG, "定位权限都有了，该干嘛干嘛去吧")
                        } else if (!it.shouldShowRequestPermissionRationale) {
                            //如果定位权限勾选了不再访问，且拒绝了，那么
                            Log.i(TAG, "其他的就不管了，定位无解了，打开设置吧")
                            showShouldSettingDialog()
                        } else {
                            Log.i(TAG, "其他的就不管了，定位获取还有救，继续")
                            requestLocationPermission()
                        }
                    }

                }
    }

    /**
     * 只请求定位权限
     */
    private fun requestLocationPermission() {
        RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe {
                    when {
                        it.granted -> {
                            Log.i(TAG, "requestLocationPermission 定位权限获取成功，该干嘛干嘛去吧")
                        }
                        it.shouldShowRequestPermissionRationale -> {
                            Log.i(TAG, "requestLocationPermission 定位权限获取失败，但是没有勾选不再询问，继续请求")
                            requestLocationPermission()
                        }
                        else -> {
                            Log.i(TAG, "requestLocationPermission 定位权限获取失败，勾选了不再询问，去设置吧")
                            showShouldSettingDialog()
                        }
                    }
                }
    }

    /**
     * 弹出需要打开设置的 dialog
     */
    private fun showShouldSettingDialog() {
        AlertDialog.Builder(this)
                .setMessage("无法完成定位,请查看权限管理!")
                .setPositiveButton("设置") { dialog, which ->
                    openSetting()
                }
                .setCancelable(false)
                .create()
                .show()
    }

    /**
     * 打开设置
     */
    private fun openSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

}
