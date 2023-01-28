package com.loan.icreditapp.ui.widget

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

class BlankTextWatcher : TextWatcher {
    private var mEditText: AppCompatEditText? = null

    private var sb: StringBuffer = StringBuffer()

    constructor(editText: AppCompatEditText) {
        mEditText = editText
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null || s?.length == 0)
            return;
        val sbLength = sb.length
//删除字符串从0~sb_length-1处的内容 (这个方法就是用来清除StringBuffer中的内容的)
        if (sbLength > 0) {
            sb.delete(0, sbLength)
        }
        for (i in 0 until s.length) {
            if (i != 4 && i != 9 && i != 14 && i != 19 && s.toString()[i] == ' ') {
                continue
            } else {
                sb.append(s.toString()[i]);
                if ((sb.length == 5 || sb.length == 10 || sb.length == 15 || sb.length == 20)
                    && s.toString()[sb.length - 1]!= ' ') {
                    sb.insert(sb.length - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            var index = start + 1;
            if (sb.toString()[start] == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            mEditText?.setText(sb.toString());
            mEditText?.setSelection(index);
        }
    }

    override fun afterTextChanged(s: Editable?) {
//        if (s == null) {
//            return
//        }
////        var len = s?.length
////        //设置输入最大长度，后期按需增加此功能
////        if(len >= textMaxLength){
////            //	return;
////        }
//        //如果包含空格，就清除
//        var chars = s.toString().trim().replace(" ", "").toCharArray()
//        var len = chars.size;
//        //每4个分组，加上空格合成新的字符串
//        val sbLength = sb.length
////删除字符串从0~sb_length-1处的内容 (这个方法就是用来清除StringBuffer中的内容的)
//        if (sbLength > 0) {
//            sb.delete(0, sbLength)
//        }
//        //注意，一定要加上这一个判断，否则会陷入死循环，因为我们在这里改变了字符串s，这个事件会一直被监听到
//        //所以，只有当我们输入的字符串不在改变的时候就不在监听到了
//        if (len != tempCount) {
//            for (int i = 0;i < len;i++){
//
//                //如果是密码状态就替换字符串
//                if (isPassword) {
//                    if (chars[i] + "" != " ") {
//                        chars[i] = '*';
//                    }
//                }
//                //如果分割的话，每次遍历到4的倍数就添加一个空格
//
//                if (i % maxLength == 0 && i != 0) {
//                    sb.append(delimiter);
//                    sb.append(chars[i]);
//                } else {
//                    sb.append(chars[i]);
//                }
//                //改变需求，将所有字符替换成*号，表示输入的是密码
//
//            }
//            text = sb.toString();
//            //获取改变之后字符串的长度
//            tempCount = text.trim().replace(" ", "").length();
//            //设置新的字符串到文本
//            editText.setText(sb.toString());
//        }
//        //设置光标的位置
//        if (text != null) {
//            editText.setSelection(text.length());
//        }
    }
}