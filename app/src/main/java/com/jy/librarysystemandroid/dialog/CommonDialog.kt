package com.jy.librarysystemandroid.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jy.librarysystemandroid.R

class CommonDialog : Dialog {
    private var mContext: Context? = null

    constructor(@NonNull context: Context?) : super(context) {}
    constructor(@NonNull context: Context?, themeResId: Int) : super(
        context,
        themeResId
    ) {
        mContext = context
        setCanceledOnTouchOutside(false)
    }

    //在dialog.show()之后调用
//    public static void setDialogWindowAttr(Dialog dlg){
//        Window window = dlg.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.gravity = Gravity.CENTER;
//        lp.horizontalMargin = Dp2px.dp2px(dlg.getContext(), 12);
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dlg.getWindow().setAttributes(lp);
//    }
    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveButtonText: String? = null
        private var negativeButtonText: String? = null
        private var contentView: View? = null
        private var positiveButtonClickListener: DialogInterface.OnClickListener? = null
        private var negativeButtonClickListener: DialogInterface.OnClickListener? = null
        fun setMessage(message: String?): Builder {
            this.message = message
            return this
        }

        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
        fun setMessage(message: Int): Builder {
            this.message = context.getText(message) as String
            return this
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        fun setTitle(title: Int): Builder {
            this.title = context.getText(title) as String
            return this
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setContentView(v: View?): Builder {
            contentView = v
            return this
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        fun setPositiveButton(
            positiveButtonText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.positiveButtonText = context
                .getText(positiveButtonText) as String
            positiveButtonClickListener = listener
            return this
        }

        fun setPositiveButton(
            positiveButtonText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.positiveButtonText = positiveButtonText
            positiveButtonClickListener = listener
            return this
        }

        fun setNegativeButton(
            negativeButtonText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.negativeButtonText = context
                .getText(negativeButtonText) as String
            negativeButtonClickListener = listener
            return this
        }

        fun setNegativeButton(
            negativeButtonText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.negativeButtonText = negativeButtonText
            negativeButtonClickListener = listener
            return this
        }

        fun create(): CommonDialog {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // instantiate the dialog with the custom Theme
            val dialog = CommonDialog(context, R.style.Dialog)
            val layout: View = inflater.inflate(R.layout.dialog_common_view, null)
            dialog.addContentView(
                layout, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            // set the dialog title
            (layout.findViewById<View>(R.id.title) as TextView).text = title
            // set the confirm button
            if (positiveButtonText != null) {
                (layout.findViewById<View>(R.id.positiveTextView) as TextView).text =
                    positiveButtonText
                if (positiveButtonClickListener != null) {
                    (layout.findViewById<View>(R.id.positiveTextView) as TextView)
                        .setOnClickListener {
                            positiveButtonClickListener!!.onClick(
                                dialog,
                                DialogInterface.BUTTON_POSITIVE
                            )
                        }
                }
            } else { // if no confirm button just set the visibility to GONE
                layout.findViewById<View>(R.id.positiveTextView).visibility = View.GONE
            }
            // set the cancel button
            if (negativeButtonText != null) {
                (layout.findViewById<View>(R.id.negativeTextView) as TextView).text =
                    negativeButtonText
                if (negativeButtonClickListener != null) {
                    (layout.findViewById<View>(R.id.negativeTextView) as TextView)
                        .setOnClickListener {
                            negativeButtonClickListener!!.onClick(
                                dialog,
                                DialogInterface.BUTTON_NEGATIVE
                            )
                        }
                }
            } else { // if no confirm button just set the visibility to GONE
                layout.findViewById<View>(R.id.negativeTextView).visibility = View.GONE
            }
            // set the content message
            if (message != null) {
                (layout.findViewById<View>(R.id.message) as TextView).text = message
            } else if (contentView != null) { // if no message set
// add the contentView to the dialog body
                (layout.findViewById<View>(R.id.content) as LinearLayout)
                    .removeAllViews()
                (layout.findViewById<View>(R.id.content) as LinearLayout)
                    .addView(
                        contentView,
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    )
            }
            dialog.setContentView(layout)
            return dialog
        }

    }
}