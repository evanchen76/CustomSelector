package evan.chen.tutorial.customselector

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_selector.view.*

class CustomSelector(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var drawables = intArrayOf()

    private var isOpen = false

    private val ROW_HEIGHT = 150

    interface IconSelectListener {
        fun onOpen()
        fun onSelected(iconIndex: Int)
        fun onCancel()
    }

    private var listener: IconSelectListener? = null

    fun setSelectIcon(drawables: IntArray) {
        this.drawables = drawables
        setting()
    }

    init {
        setting()
    }

    private fun setting() {
        View.inflate(context, R.layout.custom_selector, this)

        //繪制+的按鈕
        drawButton()
        //繪制Dialog
        drawDialog()
        //控制是否顯示Dialog
        displayDialog(false)

        //點下Button的事件
        select_imageview.setOnClickListener {

            //旋轉+按鈕
            rotateSelectImageView()

            //顯示Dialog
            displayDialog(!isOpen)

            //Callback
            if (isOpen) {
                listener?.onCancel()
            } else {
                listener?.onOpen()
            }

            isOpen = !isOpen
        }
    }

    private fun rotateSelectImageView() {
        //點選+後，旋轉變成x
        val fromDegree: Float
        val toDegree: Float

        if (isOpen) {
            //旋轉由-45度到0度。開啟狀態(x) -> 關閉狀態(+)
            fromDegree = -45.0f
            toDegree = 0.0f

        } else {
            //旋轉由0度到-45度。開啟狀態(x) -> 關閉狀態(+)
            fromDegree = 0.0f
            toDegree = -45.0f
        }

        val animRotate = RotateAnimation(fromDegree, toDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f)

        animRotate.duration = 300
        animRotate.fillAfter = true

        select_imageview.startAnimation(animRotate)
    }

    private fun dismissDialog() {
        isOpen = false
        rotateSelectImageView()
        displayDialog(isOpen)
    }

    private fun drawButton() {
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)

        //產生Paint
        val p = Paint()
        p.strokeWidth = 2f
        p.style = Paint.Style.FILL
        p.isAntiAlias = true
        p.color = Color.parseColor("#419DFF")

        //畫圓
        val canvas = Canvas(bitmap)
        canvas.drawCircle(25f, 25f, 25f, p)

        val circlePadding = 12
        p.color = Color.WHITE

        //畫中間的十字
        val rect = RectF((25 - 3).toFloat(), circlePadding.toFloat(), (25 + 3).toFloat(), (50 - circlePadding).toFloat())
        canvas.drawRect(rect, p)

        val rect2 = RectF(circlePadding.toFloat(), (25 - 3).toFloat(), (50 - circlePadding).toFloat(), (25 + 3).toFloat())
        canvas.drawRect(rect2, p)

        select_imageview.setImageBitmap(bitmap)
    }

    private fun drawDialog() {
        //畫Dialog
        val triangleWidth = 50
        val triangleHeight = 40

        //一列有三個Icon
        val imagesPerRow = 3

        //共有幾列
        var imageRows = drawables.size / imagesPerRow

        if (drawables.size % imagesPerRow > 0) {
            imageRows++
        }
        if (imageRows == 0) {
            imageRows = 1
        }

        val dialogWidth = 500
        val dialogImagesHeight = ROW_HEIGHT * imageRows
        val dialogHeight = ROW_HEIGHT * imageRows + triangleHeight

        //將Icon陣列放入LinearLayout
        for (i in 0 until imageRows) {
            // through every row

            val imageLayout = LinearLayout(this.context)
            imageLayout.orientation = LinearLayout.HORIZONTAL

            val imageParam = LinearLayout.LayoutParams(
                    0,
                    dialogWidth / 6,
                    1.0f
            )

            for (j in 0 until imagesPerRow) {
                val nums = i * imagesPerRow + j

                if (nums < drawables.size) {
                    val imageView = ImageView(this.context)
                    imageView.setImageResource(drawables[nums])
                    imageView.layoutParams = imageParam

                    imageView.setOnClickListener {
                        dismissDialog()
                        listener?.onSelected(nums)
                    }

                    imageLayout.addView(imageView)
                }
            }

            iconLinearLayout.addView(imageLayout)
        }

        //繪制Dialog的外觀
        val bitmap = Bitmap.createBitmap(dialogWidth, dialogHeight, Bitmap.Config.ARGB_8888)

        // 產生Paint
        val p = Paint()
        p.strokeWidth = 2f
        p.color = Color.WHITE
        p.style = Paint.Style.FILL
        p.isAntiAlias = true
        p.setShadowLayer(5f, 2f, 2f, Color.LTGRAY)
        p.strokeJoin = Paint.Join.ROUND
        p.strokeCap = Paint.Cap.ROUND
        p.pathEffect = CornerPathEffect(10f)

        //畫Dialog
        val canvas = Canvas(bitmap)

        val leftX = 0
        val leftY = 0

        val centerX = (dialogWidth - leftX) / 2 + leftX

        val path = Path()

        path.moveTo(leftX.toFloat(), leftY.toFloat())
        path.lineTo(dialogWidth.toFloat(), leftY.toFloat())
        path.lineTo(dialogWidth.toFloat(), dialogImagesHeight.toFloat())
        path.lineTo((centerX + triangleWidth).toFloat(), dialogImagesHeight.toFloat())
        path.lineTo(centerX.toFloat(), (dialogImagesHeight + triangleHeight).toFloat())
        path.lineTo((centerX - triangleWidth).toFloat(), dialogImagesHeight.toFloat())
        path.lineTo(leftX.toFloat(), dialogImagesHeight.toFloat())
        path.close()

        canvas.drawPath(path, p)

        val drawable = BitmapDrawable(resources, bitmap)

        this.dialog_select_linearlayout.background = drawable
    }

    private fun displayDialog(display: Boolean) {
        //開啟、關閉Dialog
        var fromScale = 0.0f
        var toScale = 1.0f

        if (display) {
            //由小變大展開
            fromScale = 0.0f
            toScale = 1.0f
            this.dialog_select_linearlayout.visibility = View.VISIBLE
            this.dialog_select_linearlayout.bringToFront()
        } else {
            //由大變小縮起
            fromScale = 1.0f
            toScale = 0.0f
            this.dialog_select_linearlayout.visibility = View.GONE
        }

        //Scale動畫，顯示時由小放大，關閉時由大放小
        val anim = ScaleAnimation(
                fromScale, toScale,
                fromScale, toScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1f)

        anim.duration = 300
        this.dialog_select_linearlayout.startAnimation(anim)
    }

    fun setListener(listener: IconSelectListener) {
        this.listener = listener
    }
}