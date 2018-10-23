package evan.chen.tutorial.customselector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //放大icon array
        val drawables = intArrayOf(R.mipmap.icon1, R.mipmap.icon2, R.mipmap.icon3)

        selectDialog.setSelectIcon(drawables)

        //事件
        selectDialog.setListener(object : CustomSelector.IconSelectListener {
            //開啟Dialog
            override fun onOpen() {
                selectResult.text = ""
            }

            //被選取事件
            override fun onSelected(iconIndex: Int) {
                selectResult.text = "Select  icon: $iconIndex"
            }

            //點X事件。
            override fun onCancel() {
                selectResult.text = "Cancel"
            }
        })
    }
}
