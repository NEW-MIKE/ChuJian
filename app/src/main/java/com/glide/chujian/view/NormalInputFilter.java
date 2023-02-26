package com.glide.chujian.view;

import android.text.Spanned;
import android.text.method.DigitsKeyListener;

public class NormalInputFilter extends DigitsKeyListener {

    private static final String TAG = "NormalInputFilter";
    private int POINT_LENGTH = 2;
    public NormalInputFilter() {
        super(false, true);
    }

    private int mDigits = 1;

    public NormalInputFilter setmDigits(int d) {
        mDigits = d;
        return this;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        CharSequence mstart = dest.subSequence(0, dstart);
        CharSequence mend = dest.subSequence(dend, dest.length());
        String target = mstart.toString() + source + mend;//字符串变化后的结果
        CharSequence backup = dest.subSequence(dstart, dend);//将要被替换的字符串

        if (target.indexOf(".") == 0) {//不允许第一个字符为.
            return backup;
        }

        if (target.startsWith("0") && !target.startsWith("0.") && "0" != target) {//不允许出现0123、0456这类字符串
            return backup;
        }

        //限制小数点后面只能有两位小数
        int index = target.indexOf(".");
        if (index >= 0 && index + POINT_LENGTH + 2 <= target.length()) {
            return backup;
        }

        return source;
    }
}
