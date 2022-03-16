package com.easy.aop

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.easy.aop.annotation.*
import com.easy.aop.auto.AutoAction
import com.easy.aop.callback.DoProceed
import com.easy.aop.enumerate.Statistics
import com.easy.aop.helper.permission.AopPermissionUtils
import com.easy.aop.utils.ktxRunOnUiDelay
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initOnclick()
    }

    private fun initOnclick() {
        aop1.setOnClickListener {
            testMt("测试参数")
        }
        run1.setOnClickListener {
            Thread {
                testIO()
            }.start()
        }
        lock1.setOnClickListener {
            ktxRunOnUiDelay(1000) {
                lockUtils("1")
            }
            ktxRunOnUiDelay(1000) {
                lockUtils("2")
            }
            ktxRunOnUiDelay(1000) {
                lockUtils("3")
            }
        }
        auto1.setOnClickListener {
            testAuto()
        }
        // 设置统一拦截
        AopManager.instance.setAutoListener(object : AutoAction {
            override fun proceedBefore(
                action: String,
                map: MutableMap<String, String>,
                proceed: DoProceed
            ) {
                Log.d("统一处理", "在方法执行前  action=$action")
                proceed.runMethod()
            }

            override fun proceedAfter(action: String, map: MutableMap<String, String>) {
                Log.d("统一处理", "在方法执行后  action=$action")
            }
        })
        auto2.setOnClickListener {
            permission(it)
        }
    }


    /**
     * 开启请求权限注解
     * @ value 权限值
     * @ rationale 拒绝后的下一次提示(开启后，拒绝后，下一次会先提示该权限申请提示语)
     * @ requestCode 权限请求码标识
     */
    @Permission(
        value = [Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE],
        rationale = "为了更好的体验，请打开相关权限"
    )
    fun permission(view: View) {
        Log.e("Permission", "permission: 权限已打开")
    }

    /**
     * 请求拒绝注解回调
     * @param requestCode 权限请求码标识
     * @param denyList 被拒绝的权限集合
     */
    @PermissionDenied
    fun permissionDenied(requestCode: Int, denyList: List<String?>) {
        Log.e("Permission", "permissionDenied: $requestCode")
        Log.e("Permission", "permissionDenied>>>: $denyList")
    }

    /**
     * 请求拒绝且不在提示注解回调
     * @param requestCode 权限请求码标识
     * @param denyNoAskList 被拒绝且不再提示的权限集合
     */
    @PermissionNoAskDenied
    fun permissionNoAskDenied(requestCode: Int, denyNoAskList: List<String?>) {
        Log.e("Permission", "permissionNoAskDenied: $requestCode")
        Log.e("Permission", "permissionNoAskDenied>>>: $denyNoAskList")
        AopPermissionUtils.showGoSetting(this, "去设置页XXXXX")
    }


    @Auto(action = "action1", parameter = [AutoParameter(key = "1", value = "2")])
    fun testAuto() {
        Log.d("auto", "auto doing")
    }

    @Lock
    fun lockUtils(value: String) {
        Thread.sleep(1000)//每个停一会儿
        Log.d("value = ", value)// 不会一次性的把 1 2 3全部输出，而是间隔1000ms左右逐个输出
    }

    @Run(type = Statistics.MAIN)
    fun testIO() {
        try {
            run1.text = "在子线程中切换到主线程，再修改了按钮"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //    插桩
    @Stub(tag = "插桩tag", content = "插桩日志内容")
    fun testMt(name: String) {
        Log.d("StubAspect", " = !23 $name")
    }


}